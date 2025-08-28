package com.example.immunobubby;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PollenManager {

    private final Context context;
    private final String apiKey;
    private final OkHttpClient client;

    public PollenManager(Context context, String apiKey) {
        this.context = context;
        this.apiKey = apiKey;
        this.client = new OkHttpClient();
    }

    public interface PollenCallback {
        void onData(List<PollenData> pollens);
        void onError(Exception e);
    }

    public void fetchPollens(double lat, double lon, PollenCallback callback) {
        // URL aggiornato secondo la tua richiesta
        String url = "https://pollen.googleapis.com/v1/forecast:lookup?key=" + apiKey +
                "&location.latitude=" + lat + "&location.longitude=" + lon + "&days=1";

        Request request = new Request.Builder().url(url).build();
        Handler mainHandler = new Handler(Looper.getMainLooper());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mainHandler.post(() -> callback.onError(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    mainHandler.post(() -> callback.onError(new Exception("HTTP error: " + response.code())));
                    return;
                }

                String json = response.body().string();
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

                                // Nome del polline: displayName > commonName > Unknown
                                String name = "Unknown";
                                if (plantObj.has("displayName") && !plantObj.get("displayName").isJsonNull()) {
                                    name = plantObj.get("displayName").getAsString();
                                } else if (plantDesc != null && plantDesc.has("displayName") && !plantDesc.get("displayName").isJsonNull()) {
                                    name = plantDesc.get("displayName").getAsString();
                                }

                                // Categoria/tipo: plantDescription.type > Unknown
                                String type = (plantDesc != null && plantDesc.has("type")) ? plantDesc.get("type").getAsString() : "Unknown";

                                // Colore: default grigio, oppure se presente in indexInfo.category
                                int color = 0xFF888888;
                                if (plantObj.has("indexInfo") && !plantObj.get("indexInfo").isJsonNull()) {
                                    JsonObject indexInfo = plantObj.getAsJsonObject("indexInfo");
                                    if (indexInfo.has("category")) {
                                        switch (indexInfo.get("category").getAsString()) {
                                            case "Very Low": color = 0xFF00FF00; break; // verde
                                            case "Low": color = 0xFFFFFF00; break; // giallo
                                            case "Medium": color = 0xFFFFA500; break; // arancione
                                            case "High": color = 0xFFFF0000; break; // rosso
                                        }
                                    }
                                }

                                pollenList.add(new PollenData(name, 0, type, color));
                            }
                        }
                    }

                    mainHandler.post(() -> callback.onData(pollenList));

                } catch (Exception e) {
                    mainHandler.post(() -> callback.onError(e));
                }
            }
        });
    }
}
