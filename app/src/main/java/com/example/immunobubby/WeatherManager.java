package com.example.immunobubby;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Call;
import okhttp3.Callback;


public class WeatherManager {

    private final Context context;
    private final String apiKey;
    private final FusedLocationProviderClient fusedLocationClient;

    public interface WeatherCallback {
        void onWeatherUpdated(String temperature, int iconResId, String cityName);
        void onError(Exception e);
    }


    public WeatherManager(Context context, String apiKey) {
        this.context = context;
        this.apiKey = apiKey;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public void fetchWeather(WeatherCallback callback) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            callback.onError(new SecurityException("Permesso posizione negato"));
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                getWeatherFromApi(location.getLatitude(), location.getLongitude(), callback);

            } else {
                callback.onError(new Exception("Impossibile ottenere la posizione"));
            }
        });
    }

    private void getWeatherFromApi(double lat, double lon, WeatherCallback callback) {
        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&units=metric&appid=" + apiKey;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError(new IOException("Errore API"));
                    return;
                }

                try {
                    String json = response.body().string();
                    JSONObject obj = new JSONObject(json);

                    final double temp = obj.getJSONObject("main").getDouble("temp");
                    final String icon = obj.getJSONArray("weather").getJSONObject(0).getString("icon");
                    final String cityName = obj.getString("name"); // nome città
                    final int iconResId = getIconResource(icon);

                    ((Activity)context).runOnUiThread(() -> {
                        callback.onWeatherUpdated((int) temp + "°C", iconResId, cityName); //TODO modifica dopo aver implementato la personalizzazione dell'interfaccia
                    });

                } catch (JSONException e) {
                    callback.onError(e);
                }
            }
        });
    }


    private int getIconResource(String iconCode) {
        switch (iconCode) {
            case "01d": return R.drawable.ic_sun;
            case "01n": return R.drawable.moon_stars_24px;
            case "02d": return R.drawable.partly_cloudy_day_24px;
            case "02n": return R.drawable.partly_cloudy_night_24px;
            case "03d": case "03n": return R.drawable.cloud_24px;
            case "04d": case "04n": return R.drawable.filter_drama_24px;
            case "09d": case "09n": return R.drawable.rainy_24px;
            case "10d": case "10n": return R.drawable.sunny_snowing_24px;
            case "11d": case "11n": return R.drawable.thunderstorm_24px;
            case "13d": case "13n": return R.drawable.weather_snowy_24px;
            case "50d": case "50n": return R.drawable.foggy_24px;
            default: return R.drawable.ic_sun;
        }
    }
}
