package com.example.immunobubby;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.immunobubby.pathfinding.AStarPathFinder;
import com.example.immunobubby.pathfinding.Arco;
import com.example.immunobubby.pathfinding.Nodo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PercorsoMappaActivity extends BaseActivity implements OnMapReadyCallback {

    private static final String TAG = "PercorsoMappaActivity";
    private GoogleMap mMap;

    private LatLng fromLatLng, toLatLng;
    private ImageButton btnWalking, btnCar, btnPublicTransport;
    private TextInputEditText etFromLocation, etToLocation;

    private final String ORS_API_KEY = "eyJvcmciOiI1YjNjZTM1OTc4NTExMTAwMDFjZjYyNDgiLCJpZCI6IjcwOWU1OWM0OTI0ZDRhM2RhZGQwNGE4MjE5NTcxZTk3IiwiaCI6Im11cm11cjY0In0=";
    private final String POLLEN_API_KEY = "AIzaSyAdeZWce_XP6K9Iq5GuTz6-bGHZ8XcWMsU";

    private PollenManager pollenManager;

    String from, to, mezzo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizza_percorso);

        from = getIntent().getStringExtra("from_address");
        to = getIntent().getStringExtra("to_address");
        mezzo = getIntent().getStringExtra("mezzo");

        etFromLocation = findViewById(R.id.etFromLocation);
        etToLocation = findViewById(R.id.etToLocation);
        etFromLocation.setText(from);
        etToLocation.setText(to);


        btnWalking = findViewById(R.id.btnWalking);
        btnCar = findViewById(R.id.btnCar);
        btnPublicTransport = findViewById(R.id.btnPublicTransport);

        if (mezzo.equals("PIEDI")) btnWalking.setBackgroundResource(R.drawable.rounded_button_bg_selected);
        else if (mezzo.equals("AUTO")) btnCar.setBackgroundResource(R.drawable.rounded_button_bg_selected);
        else if (mezzo.equals("BUS")) btnPublicTransport.setBackgroundResource(R.drawable.rounded_button_bg_selected);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        if (mapFragment != null) mapFragment.getMapAsync(this);

        pollenManager = new PollenManager(this, POLLEN_API_KEY);

        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> fromList = geocoder.getFromLocationName(from, 1);
            List<Address> toList = geocoder.getFromLocationName(to, 1);
            if (!fromList.isEmpty() && !toList.isEmpty()) {
                fromLatLng = new LatLng(fromList.get(0).getLatitude(), fromList.get(0).getLongitude());
                toLatLng = new LatLng(toList.get(0).getLatitude(), toList.get(0).getLongitude());
                calcolaPercorsoORS();
            } else {
                Toast.makeText(this, "Indirizzi non validi", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void calcolaPercorsoORS() {
        if (fromLatLng == null || toLatLng == null) return;

        String profile = mezzo.equals("PIEDI") ? "foot-walking" :
                mezzo.equals("AUTO") ? "driving-car" :
                        "driving-car"; // BUS trattato come auto

        String url = "https://api.openrouteservice.org/v2/directions/" + profile +
                "?api_key=" + ORS_API_KEY +
                "&start=" + fromLatLng.longitude + "," + fromLatLng.latitude +
                "&end=" + toLatLng.longitude + "," + toLatLng.latitude;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "ORS request failed", e);
                runOnUiThread(() -> Toast.makeText(PercorsoMappaActivity.this,
                        "Errore calcolo percorso", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) return;

                try {
                    JSONObject json = new JSONObject(response.body().string());
                    JSONArray coords = json.getJSONArray("features")
                            .getJSONObject(0)
                            .getJSONObject("geometry")
                            .getJSONArray("coordinates");

                    // Genera grafo ORS come nodi collegati tra loro
                    Map<Nodo, List<Arco>> grafoORS = new HashMap<>();
                    List<Nodo> nodi = new ArrayList<>();
                    for (int i = 0; i < coords.length(); i++) {
                        JSONArray point = coords.getJSONArray(i);
                        Nodo nodo = new Nodo("nodo_" + i, point.getDouble(1), point.getDouble(0));
                        nodi.add(nodo);
                        grafoORS.put(nodo, new ArrayList<>());
                        if (i > 0) {
                            Nodo prev = nodi.get(i - 1);
                            double distanza = calcolaDistanza(prev.getLat(), prev.getLon(),
                                    nodo.getLat(), nodo.getLon());
                            // Applica penalità pollini
                            distanza *= 1 + pollenManager.getPenalty(prev.getLat(), prev.getLon())/1000f;
                            grafoORS.get(prev).add(new Arco(prev, nodo, distanza));
                            grafoORS.get(nodo).add(new Arco(nodo, prev, distanza));
                        }
                    }

                    Nodo start = nodi.get(0);
                    Nodo goal = nodi.get(nodi.size() - 1);

                    AStarPathFinder pathFinder = new AStarPathFinder(grafoORS, pollenManager);
                    List<Nodo> percorsoOttimale = pathFinder.calcolaPercorsoOttimale(start, goal);

                    List<LatLng> percorsoLatLng = new ArrayList<>();
                    for (Nodo n : percorsoOttimale) {
                        percorsoLatLng.add(new LatLng(n.getLat(), n.getLon()));
                    }

                    runOnUiThread(() -> mostraPercorsoSuMappa(percorsoLatLng));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private double calcolaDistanza(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000; // metri
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }

    private void mostraPercorsoSuMappa(List<LatLng> percorso) {
        mMap.clear();
        mMap.addPolyline(new PolylineOptions().addAll(percorso).color(0xFF00AA00).width(10f));
        if (!percorso.isEmpty())
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(percorso.get(0), 12));
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
    }
}
