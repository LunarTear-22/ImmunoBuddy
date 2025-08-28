package com.example.immunobubby;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QualitàAriaActivity extends BaseActivity {

    private static final String TAG = "QualitaAriaActivity";

    private TextView tvTemperature;
    private TextView tvLocation;
    private TextView tvPollenStatus;
    private RecyclerView recyclerPollens;
    private PollenAdapter pollenAdapter;
    private ImageView ivPollenState;
    private MaterialCardView containerRecycler;
    private ImageButton btnPercorsiArrow;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private final Map<Integer, Runnable> permissionCallbacks = new HashMap<>();

    private final ArrayList<PollenData> pollenList = new ArrayList<>();
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
        tvPollenStatus = findViewById(R.id.tvPollenStatus);
        ivPollenState = findViewById(R.id.ivPollenState);
        containerRecycler = findViewById(R.id.containerRecycler);
        btnPercorsiArrow = findViewById(R.id.btnPercorsiArrow);

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
        PollenManager pollenManager = new PollenManager(this, POLLEN_API_KEY);
        pollenManager.fetchPollens(lat, lon, new PollenManager.PollenCallback() {
            @Override
            public void onData(List<PollenData> pollens) {
                runOnUiThread(() -> {
                    pollenList.clear();
                    pollenList.addAll(pollens);
                    pollenAdapter.notifyDataSetChanged();
                    updatePollenStatus();
                });
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Errore recupero pollini", e);
                runOnUiThread(() -> {
                    recyclerPollens.setVisibility(View.GONE);
                    tvPollenStatus.setText("Nessun polline attivo nella zona");
                    ivPollenState.setImageResource(R.drawable.sentiment_excited_24px);
                });
            }
        });
    }

    private void updatePollenStatus() {
        int totalActive = pollenAdapter.getTotalActivePollens();

        // Aggiorna testo e visibilità
        if (totalActive > 0) {
            tvPollenStatus.setText(totalActive + " pollini attivi nella zona");
            recyclerPollens.setVisibility(View.VISIBLE);
            containerRecycler.setVisibility(View.VISIBLE);
        } else {
            tvPollenStatus.setText("Nessun polline attivo nella zona");
            recyclerPollens.setVisibility(View.GONE);
            containerRecycler.setVisibility(View.GONE);
        }

        // Aggiorna icona ivPollenState
        if (ivPollenState != null) {
            if (totalActive <= 4) {
                ivPollenState.setImageResource(R.drawable.sentiment_excited_24px);      // verde
            } else if (totalActive < 10) {
                ivPollenState.setImageResource(R.drawable.sentiment_neutral_24px);   // arancione
            } else {
                ivPollenState.setImageResource(R.drawable.sentiment_stressed_24px);     // rosso
            }
        }
    }
}
