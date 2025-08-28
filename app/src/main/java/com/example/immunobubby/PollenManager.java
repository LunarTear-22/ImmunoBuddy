package com.example.immunobubby;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

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
        String url = "https://api.breezometer.com/pollen/v2/current-conditions" +
                "?lat=" + lat +
                "&lon=" + lon +
                "&key=" + apiKey +
                "&features=breezometer_pollen_level,types";

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            Handler mainHandler = new Handler(Looper.getMainLooper());

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
                    JsonObject data = root.getAsJsonObject("data").getAsJsonObject("types");

                    for (String type : new String[]{"tree", "grass", "weed", "mold"}) {
                        if (data.has(type)) {
                            JsonObject obj = data.getAsJsonObject(type);
                            PollenData pd = new PollenData();
                            pd.name = type;
                            pd.level = obj.get("index").getAsInt(); // livello pollini
                            pollenList.add(pd);
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
