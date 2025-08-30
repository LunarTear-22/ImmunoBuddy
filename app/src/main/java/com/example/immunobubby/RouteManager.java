package com.example.immunobubby;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.example.immunobubby.pathfinding.Arco;
import com.example.immunobubby.pathfinding.Nodo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.*;

public class RouteManager {
    private static final String TAG = "RouteManager";

    private final OkHttpClient client = new OkHttpClient();
    private final String orsApiKey;

    public RouteManager(String orsApiKey) {
        this.orsApiKey = orsApiKey;
        Log.d(TAG, "RouteManager inizializzato con ORS API Key");
    }

    // ----------------- GEOCODING -----------------
    public interface GeocodeCallback {
        void onResult(LatLng latLon);
    }

    public void geocodeAddress(String address, GeocodeCallback callback) {
        Log.d(TAG, "Geocoding per indirizzo: " + address);
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" +
                address.replace(" ", "+") + "&key=AIzaSyAdeZWce_XP6K9Iq5GuTz6-bGHZ8XcWMsU";

        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Errore geocoding: " + e.getMessage(), e);
                callback.onResult(null);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Errore HTTP geocoding: " + response.code());
                    callback.onResult(null);
                    return;
                }

                String json = response.body().string();
                try {
                    JsonObject root = new Gson().fromJson(json, JsonObject.class);
                    JsonArray results = root.getAsJsonArray("results");
                    if (results.size() > 0) {
                        JsonObject loc = results.get(0).getAsJsonObject()
                                .getAsJsonObject("geometry")
                                .getAsJsonObject("location");
                        double lat = loc.get("lat").getAsDouble();
                        double lon = loc.get("lng").getAsDouble();
                        callback.onResult(new LatLng(lat, lon));
                    } else {
                        callback.onResult(null);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Errore parsing JSON geocoding", e);
                    callback.onResult(null);
                }
            }
        });
    }

    // ----------------- COSTRUZIONE GRAFO -----------------
    public interface GraphCallback {
        void onGraphReady(Map<Nodo, List<Arco>> grafo);
    }

    public void buildGraph(LatLng start, LatLng goal, GraphCallback callback) {
        try {
            String url = "https://api.openrouteservice.org/v2/directions/driving-car/geojson";

            // Costruzione body JSON
            JsonObject body = new JsonObject();

            JsonArray startCoord = new JsonArray();
            startCoord.add(start.longitude);
            startCoord.add(start.latitude);

            JsonArray goalCoord = new JsonArray();
            goalCoord.add(goal.longitude);
            goalCoord.add(goal.latitude);

            JsonArray coordinates = new JsonArray();
            coordinates.add(startCoord);
            coordinates.add(goalCoord);

            body.add("coordinates", coordinates);

            RequestBody requestBody = RequestBody.create(body.toString(), MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", orsApiKey)
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Errore ORS directions: " + e.getMessage(), e);
                    callback.onGraphReady(new HashMap<>()); // ritorna grafo vuoto
                }

                @Override public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.e(TAG, "Errore HTTP ORS directions: " + response.code());
                        callback.onGraphReady(new HashMap<>());
                        return;
                    }

                    String json = response.body().string();
                    try {
                        JsonObject root = new Gson().fromJson(json, JsonObject.class);
                        JsonArray coordsArray = root.getAsJsonArray("features")
                                .get(0).getAsJsonObject()
                                .getAsJsonObject("geometry")
                                .getAsJsonArray("coordinates");

                        Map<Nodo, List<Arco>> grafo = new HashMap<>();
                        Nodo prevNodo = null;

                        for (int i = 0; i < coordsArray.size(); i++) {
                            JsonArray coord = coordsArray.get(i).getAsJsonArray();
                            double lon = coord.get(0).getAsDouble();
                            double lat = coord.get(1).getAsDouble();
                            Nodo nodo = new Nodo("nodo" + i, lat, lon);

                            if (prevNodo != null) {
                                double distanza = calcolaDistanza(prevNodo.getLat(), prevNodo.getLon(), lat, lon);
                                Arco arco = new Arco(prevNodo, nodo, distanza);
                                grafo.computeIfAbsent(prevNodo, k -> new ArrayList<>()).add(arco);
                            }
                            prevNodo = nodo;
                        }

                        Log.d(TAG, "Grafo creato con " + grafo.size() + " nodi");
                        callback.onGraphReady(grafo);

                    } catch (Exception e) {
                        Log.e(TAG, "Errore parsing ORS directions", e);
                        callback.onGraphReady(new HashMap<>());
                    }
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Errore costruzione grafo", e);
            callback.onGraphReady(new HashMap<>());
        }
    }

    // ----------------- OVERLOAD PER NODI -----------------
    public void buildGraph(Nodo start, Nodo goal, GraphCallback callback) {
        LatLng startLatLng = new LatLng(start.getLat(), start.getLon());
        LatLng goalLatLng = new LatLng(goal.getLat(), goal.getLon());
        buildGraph(startLatLng, goalLatLng, callback);
    }

    // ----------------- CALCOLO DISTANZA -----------------
    private double calcolaDistanza(double lat1, double lon1, double lat2, double lon2) {
        // Formula Haversine per distanza in km
        double R = 6371; // raggio della Terra in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
