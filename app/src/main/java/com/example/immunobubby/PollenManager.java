package com.example.immunobubby;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PollenManager {

    private static final String TAG = "PollenManager";

    private final Context context;
    private final String apiKey;
    private final OkHttpClient client;

    private final HashMap<String, List<PollenData>> pollenCache = new HashMap<>();

    public PollenManager(Context context, String apiKey) {
        this.context = context;
        this.apiKey = apiKey;
        this.client = new OkHttpClient();
        Log.d(TAG, "PollenManager inizializzato con API Key");
    }

    public interface PollenCallback {
        void onData(List<PollenData> pollens);
        void onError(Exception e);
    }

    public void fetchPollens(double lat, double lon, PollenCallback callback) {
        Log.d(TAG, "Fetch pollini per posizione: " + lat + ", " + lon);

        String url = "https://pollen.googleapis.com/v1/forecast:lookup?key=" + apiKey +
                "&location.latitude=" + lat + "&location.longitude=" + lon + "&days=1";

        Request request = new Request.Builder().url(url).build();
        Handler mainHandler = new Handler(Looper.getMainLooper());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Errore fetch pollini", e);
                mainHandler.post(() -> callback.onError(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "HTTP error fetch pollini: " + response.code());
                    mainHandler.post(() -> callback.onError(new Exception("HTTP error: " + response.code())));
                    return;
                }

                String json = response.body().string();
                Log.d(TAG, "Risposta fetch pollini: " + json);

                Gson gson = new Gson();
                JsonObject root = gson.fromJson(json, JsonObject.class);
                List<PollenData> pollenList = new ArrayList<>();

                try {
                    JsonArray dailyInfo = root.getAsJsonArray("dailyInfo");
                    if (dailyInfo != null && dailyInfo.size() > 0) {
                        JsonObject firstDay = dailyInfo.get(0).getAsJsonObject();
                        JsonArray plantInfo = firstDay.getAsJsonArray("plantInfo");

                        if (plantInfo != null) {
                            for (int i = 0; i < plantInfo.size(); i++) {
                                JsonObject plantObj = plantInfo.get(i).getAsJsonObject();
                                JsonObject plantDesc = plantObj.has("plantDescription") && !plantObj.get("plantDescription").isJsonNull()
                                        ? plantObj.getAsJsonObject("plantDescription") : null;

                                String name = "Unknown";
                                if (plantObj.has("displayName") && !plantObj.get("displayName").isJsonNull()) {
                                    name = plantObj.get("displayName").getAsString();
                                } else if (plantDesc != null && plantDesc.has("displayName") && !plantDesc.get("displayName").isJsonNull()) {
                                    name = plantDesc.get("displayName").getAsString();
                                }

                                String type = (plantDesc != null && plantDesc.has("type")) ? plantDesc.get("type").getAsString() : "Unknown";

                                int color = 0xFF888888;
                                if (plantObj.has("indexInfo") && !plantObj.get("indexInfo").isJsonNull()) {
                                    JsonObject indexInfo = plantObj.getAsJsonObject("indexInfo");
                                    if (indexInfo.has("category")) {
                                        switch (indexInfo.get("category").getAsString()) {
                                            case "Very Low": color = 0xFF00FF00; break;
                                            case "Low": color = 0xFFFFFF00; break;
                                            case "Medium": color = 0xFFFFA500; break;
                                            case "High": color = 0xFFFF0000; break;
                                        }
                                    }
                                }

                                pollenList.add(new PollenData(name, 0, type, color));
                                Log.d(TAG, "Polline aggiunto: " + name + ", colore: " + color);
                            }
                        }
                    }

                    String key = lat + "," + lon;
                    pollenCache.put(key, pollenList);
                    Log.d(TAG, "Pollini salvati in cache per chiave: " + key);

                    mainHandler.post(() -> callback.onData(pollenList));

                } catch (Exception e) {
                    Log.e(TAG, "Errore parsing JSON pollini", e);
                    mainHandler.post(() -> callback.onError(e));
                }
            }
        });
    }

    public int getPenalty(double lat, double lon) {
        String key = lat + "," + lon;
        List<PollenData> pollens = pollenCache.getOrDefault(key, new ArrayList<>());
        int penalty = 0;

        for (PollenData p : pollens) {
            switch (p.getColor()) {
                case 0xFF00FF00: penalty += 10; break;
                case 0xFFFFFF00: penalty += 50; break;
                case 0xFFFFA500: penalty += 100; break;
                case 0xFFFF0000: penalty += 300; break;
                default: penalty += 50; break;
            }
        }

        Log.d(TAG, "Penalty calcolata per posizione " + key + ": " + penalty);
        return penalty;
    }
}
