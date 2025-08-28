package com.example.immunobubby;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.checkbox.MaterialCheckBox;

public class InterfacciaActivity extends BaseActivity {

    private MaterialCheckBox checkUltime;
    private MaterialCheckBox checkPollini;
    private MaterialCheckBox checkKit;
    private MaterialCheckBox checkConsiglio;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impostazioni_interfaccia); // Assicurati che questo sia il tuo file XML

        // Inizializza SharedPreferences
        prefs = getSharedPreferences("HOME_PREFS", MODE_PRIVATE);

        // Trova checkbox
        checkUltime = findViewById(R.id.check_ultime_reazioni);
        checkPollini = findViewById(R.id.check_dati_pollini);
        checkKit = findViewById(R.id.check_kit_emergenza);
        checkConsiglio = findViewById(R.id.check_consiglio_giorno);

        // Carica stato iniziale dalle preferenze
        checkUltime.setChecked(prefs.getBoolean("ultime_reazioni", true));
        checkPollini.setChecked(prefs.getBoolean("dati_pollini", true));
        checkKit.setChecked(prefs.getBoolean("kit_emergenza", true));
        checkConsiglio.setChecked(prefs.getBoolean("consiglio_giorno", true));

        // Imposta listener per salvare automaticamente ogni cambiamento
        checkUltime.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean("ultime_reazioni", isChecked).apply());

        checkPollini.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean("dati_pollini", isChecked).apply());

        checkKit.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean("kit_emergenza", isChecked).apply());

        checkConsiglio.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean("consiglio_giorno", isChecked).apply());
    }
}
