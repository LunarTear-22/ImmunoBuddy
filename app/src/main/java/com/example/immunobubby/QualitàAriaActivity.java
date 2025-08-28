package com.example.immunobubby;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class QualitàAriaActivity extends BaseActivity {

    private static final String TAG = "QualitaAriaActivity";

    private TextView tvTemperature;
    private TextView tvLocation;
    private TextView tvEmptyPollens;
    private RecyclerView recyclerPollens;
    private PollenAdapter pollenAdapter;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private final Map<Integer, Runnable> permissionCallbacks = new HashMap<>();

    private final ArrayList<PollenData> pollenList = new ArrayList<>();
    private final OkHttpClient client = new OkHttpClient();
    private final String POLLEN_API_KEY = "AIzaSyAdeZWce_XP6K9Iq5GuTz6-bGHZ8XcWMsU";

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_quality);

        Log.d(TAG, "Activity avviata, controllo permessi posizione...");

        tvTemperature = findViewById(R.id.tvTemperature);
        tvLocation = findViewById(R.id.tvLocation);
        recyclerPollens = findViewById(R.id.recycler_pollens);
        tvEmptyPollens = findViewById(R.id.tvEmptyPollens);

        pollenAdapter = new PollenAdapter(pollenList);
        recyclerPollens.setLayoutManager(new LinearLayoutManager(this));
        recyclerPollens.setAdapter(pollenAdapter);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION,
                LOCATION_PERMISSION_REQUEST_CODE,
                this::fetchLocationWeatherAndPollens);
    }

    private void checkPermission(String permission, int requestCode, Runnable onGranted) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionCallbacks.put(requestCode, onGranted);
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        } else {
            Log.d(TAG, "Permesso già concesso: " + permission);
            onGranted.run();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Runnable callback = permissionCallbacks.get(requestCode);
            if (callback != null) {
                callback.run();
                permissionCallbacks.remove(requestCode);
            }
        } else {
            Toast.makeText(this, "Permesso negato: " + permissions[0], Toast.LENGTH_LONG).show();
        }
    }

    private void fetchLocationWeatherAndPollens() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Permesso posizione non garantito");
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                Log.d(TAG, "Posizione ottenuta: lat=" + location.getLatitude() + ", lon=" + location.getLongitude());
                fetchWeatherAndPollensForLocation(location);
            } else {
                Toast.makeText(this, "Impossibile ottenere la posizione", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchWeatherAndPollensForLocation(Location location) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();

        Log.d(TAG, "Recupero meteo per lat=" + lat + ", lon=" + lon);

        WeatherManager weatherManager = new WeatherManager(this, "7ad36df0013e9b8963d7d47bcca7cfec");
        weatherManager.fetchWeather(new WeatherManager.WeatherCallback() {
            @Override
            public void onWeatherUpdated(String temperature, int iconResId, String locationName) {
                Log.d(TAG, "Meteo ottenuto: " + temperature + " @ " + locationName);

                tvTemperature.setText(temperature);
                Drawable weatherIcon = ContextCompat.getDrawable(QualitàAriaActivity.this, iconResId);
                tvTemperature.setCompoundDrawablesWithIntrinsicBounds(null, null, weatherIcon, null);
                tvLocation.setText(locationName);

                fetchPollenData(lat, lon);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(QualitàAriaActivity.this, "Impossibile recuperare il meteo", Toast.LENGTH_SHORT).show()
                );
            }

        });
    }

    private void fetchPollenData(double lat, double lon) {
        String url = "https://pollen.googleapis.com/v1/forecast:lookup?key=" + POLLEN_API_KEY;
        Log.d(TAG, "Chiamata API pollini: " + url);

        JSONObject jsonBody = new JSONObject();
        try {
            JSONObject loc = new JSONObject();
            loc.put("latitude", lat);
            loc.put("longitude", lon);
            jsonBody.put("location", loc);
            jsonBody.put("days", 3); // previsioni per 3 giorni
        } catch (Exception e) {
            e.printStackTrace();
        }

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(jsonBody.toString(), MediaType.parse("application/json")))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Errore recupero pollini", e);
                runOnUiThread(() -> {
                    Toast.makeText(QualitàAriaActivity.this, "Errore recupero pollini", Toast.LENGTH_SHORT).show();
                    recyclerPollens.setVisibility(View.GONE);
                    tvEmptyPollens.setVisibility(View.VISIBLE);
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d(TAG, "Risposta pollini codice: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    String resStr = response.body().string();
                    Log.d(TAG, "Risposta pollini body: " + resStr);

                    try {
                        JSONObject json = new JSONObject(resStr);
                        JSONArray forecasts = json.optJSONArray("forecasts");

                        pollenList.clear();
                        if (forecasts != null && forecasts.length() > 0) {
                            JSONObject firstForecast = forecasts.getJSONObject(0);
                            JSONObject types = firstForecast.optJSONObject("types");
                            if (types != null) {
                                JSONArray names = types.names();
                                if (names != null) {
                                    for (int i = 0; i < names.length(); i++) {
                                        String pollenName = names.getString(i);
                                        int index = types.getJSONObject(pollenName).optInt("index", 0);
                                        pollenList.add(new PollenData(pollenName, index));
                                    }
                                }
                            }
                        }

                        runOnUiThread(() -> {
                            if (pollenList.isEmpty()) {
                                Log.d(TAG, "Nessun polline trovato");
                                recyclerPollens.setVisibility(View.GONE);
                                tvEmptyPollens.setVisibility(View.VISIBLE);
                            } else {
                                Log.d(TAG, "Pollini trovati: " + pollenList.size());
                                recyclerPollens.setVisibility(View.VISIBLE);
                                tvEmptyPollens.setVisibility(View.GONE);
                                pollenAdapter.notifyDataSetChanged();
                            }
                        });

                    } catch (Exception e) {
                        Log.e(TAG, "Errore parsing pollini", e);
                        runOnUiThread(() -> {
                            recyclerPollens.setVisibility(View.GONE);
                            tvEmptyPollens.setVisibility(View.VISIBLE);
                        });
                    }
                } else {
                    Log.e(TAG, "Risposta pollini non valida, code=" + response.code());
                    runOnUiThread(() -> {
                        recyclerPollens.setVisibility(View.GONE);
                        tvEmptyPollens.setVisibility(View.VISIBLE);
                    });
                }
            }
        });
    }
}
