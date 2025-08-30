package com.example.immunobubby;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class PercorsiAlternativiActivity extends BaseActivity {

    private static final String TAG = "PercorsiAltActivity";

    private AutoCompleteTextView etFromLocation, etToLocation;
    private ImageButton btnWalking, btnCar, btnPublicTransport;
    private ImageButton btnCeck;

    private final String POLLEN_API_KEY = "AIzaSyAdeZWce_XP6K9Iq5GuTz6-bGHZ8XcWMsU";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_percorsi_alternativi);

        final boolean[] walking = {false};
        final boolean[] car = {false};
        final boolean[] publicTransport = {false};

        etFromLocation = findViewById(R.id.etFromLocation);
        etToLocation = findViewById(R.id.etToLocation);

        btnCeck = findViewById(R.id.btnCeck);
        btnWalking = findViewById(R.id.btnWalking);
        btnCar = findViewById(R.id.btnCar);
        btnPublicTransport = findViewById(R.id.btnPublicTransport);

        setupAutocomplete(etFromLocation);
        setupAutocomplete(etToLocation);

        btnWalking.setOnClickListener(v -> {
            walking[0] = !walking[0];
            car[0] = false;
            publicTransport[0] = false;
            btnWalking.setBackgroundResource(walking[0] ? R.drawable.rounded_button_bg_selected : R.drawable.rounded_button_bg);
            btnCar.setBackgroundResource(R.drawable.rounded_button_bg);
            btnPublicTransport.setBackgroundResource(R.drawable.rounded_button_bg);
        });
        btnCar.setOnClickListener(v -> {
            car[0] = !car[0];
            walking[0] = false;
            publicTransport[0] = false;
            btnCar.setBackgroundResource(car[0] ? R.drawable.rounded_button_bg_selected : R.drawable.rounded_button_bg);
            btnWalking.setBackgroundResource(R.drawable.rounded_button_bg);
            btnPublicTransport.setBackgroundResource(R.drawable.rounded_button_bg);
        });
        btnPublicTransport.setOnClickListener(v -> {
            publicTransport[0] = !publicTransport[0];
            walking[0] = false;
            car[0] = false;
            btnPublicTransport.setBackgroundResource(publicTransport[0] ? R.drawable.rounded_button_bg_selected : R.drawable.rounded_button_bg);
            btnWalking.setBackgroundResource(R.drawable.rounded_button_bg);
            btnCar.setBackgroundResource(R.drawable.rounded_button_bg);
        });

        btnCeck.setOnClickListener(v -> {
            String from = etFromLocation.getText().toString();
            String to = etToLocation.getText().toString();

            if (from.isEmpty() || to.isEmpty()) {
                Toast.makeText(this, "Inserisci partenza e destinazione", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(PercorsiAlternativiActivity.this, PercorsoMappaActivity.class);
            intent.putExtra("from_address", from);
            intent.putExtra("to_address", to);

            String mezzo = null;
            if (walking[0]) mezzo = "PIEDI";
            else if (car[0]) mezzo = "AUTO";
            else if (publicTransport[0]) mezzo = "BUS";

            if (mezzo == null) {
                Toast.makeText(this, "Seleziona un mezzo di trasporto", Toast.LENGTH_SHORT).show();
                return;
            }

            intent.putExtra("mezzo", mezzo);

            startActivity(intent);
        });

        etFromLocation.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // larghezza del drawableEnd
                int drawableEndWidth = 0;
                if (etFromLocation.getCompoundDrawables()[2] != null) {
                    drawableEndWidth = etFromLocation.getCompoundDrawables()[2].getBounds().width();
                }

                // se il tocco è dentro l'area dell'icona
                if (event.getX() >= (etFromLocation.getWidth() - etFromLocation.getPaddingEnd() - drawableEndWidth)) {
                    // qui gestisci il click sull'icona
                    etFromLocation.setText(""); // esempio: svuota il testo
                    return true;
                }
            }
            return false;
        });

        etToLocation.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // larghezza del drawableEnd
                int drawableEndWidth = 0;
                if (etToLocation.getCompoundDrawables()[2] != null) {
                    drawableEndWidth = etToLocation.getCompoundDrawables()[2].getBounds().width();
                }

                // se il tocco è dentro l'area dell'icona
                if (event.getX() >= (etToLocation.getWidth() - etToLocation.getPaddingEnd() - drawableEndWidth)) {
                    // qui gestisci il click sull'icona
                    etToLocation.setText(""); // esempio: svuota il testo
                    return true;
                }
            }
            return false;
        });


    }

    private void setupAutocomplete(AutoCompleteTextView editText) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.dropdown_item_dark, new ArrayList<>());
        editText.setAdapter(adapter);
        editText.setThreshold(3);

        editText.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(android.text.Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 2) {
                    cercaStrade(s.toString(), results -> runOnUiThread(() -> {
                        adapter.clear();
                        adapter.addAll(results);
                        adapter.notifyDataSetChanged();
                        editText.showDropDown();
                    }));
                }
            }
        });

        editText.setOnItemClickListener((parent, view, position, id) -> {
            String selected = adapter.getItem(position);
            if (selected != null) {
                editText.setText(selected);
                editText.setSelection(selected.length());
            }
        });
    }

    private void cercaStrade(String query, CallbackResults callback) {
        OkHttpClient client = new OkHttpClient();

        // Endpoint aggiornato con types=geocode e components=country:IT per risultati in Italia
        String url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?" +
                "input=" + query +
                "&types=geocode" +
                "&components=country:IT" +
                "&key=" + POLLEN_API_KEY;

        Log.d(TAG, "Chiamata API Places: " + url);

        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "API failure", e);
                callback.onResult(new ArrayList<>());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                List<String> results = new ArrayList<>();
                if (!response.isSuccessful()) {
                    Log.e(TAG, "HTTP error " + response.code());
                    callback.onResult(results);
                    return;
                }

                String json = response.body().string();
                Log.d(TAG, "JSON ricevuto: " + json); // log per debug

                Gson gson = new Gson();
                JsonObject root = gson.fromJson(json, JsonObject.class);
                JsonArray predictions = root.getAsJsonArray("predictions");

                for (int i = 0; i < predictions.size(); i++) {
                    String description = predictions.get(i).getAsJsonObject()
                            .get("description").getAsString();
                    results.add(description);
                }

                Log.d(TAG, "Autocomplete results: " + results); // mostra lista predizioni
                callback.onResult(results);
            }
        });
    }

    interface CallbackResults { void onResult(List<String> results); }
}
