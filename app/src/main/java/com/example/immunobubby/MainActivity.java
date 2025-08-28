package com.example.immunobubby;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity {

    private boolean isFabMenuOpen = false;
    private TextView tvTemperature;
    private TextView tvLocation;
    private MaterialButton btnMostraKit;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private static final int CALL_PERMISSION_REQUEST_CODE = 200;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 300;



    // Mappa per gestire i callback dei permessi
    private final Map<Integer, Runnable> permissionCallbacks = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fabMain = findViewById(R.id.btnFab);
        LinearLayout fabMenuLayout = findViewById(R.id.fabMenuLayout);

        ExtendedFloatingActionButton fab1 = findViewById(R.id.btnReazioni);
        ExtendedFloatingActionButton fab2 = findViewById(R.id.btnPromemoria);
        ExtendedFloatingActionButton fab3 = findViewById(R.id.btnSintomi);

        tvTemperature = findViewById(R.id.tvTemperature);
        tvLocation = findViewById(R.id.tvLocation);

        // Toggle menu al click sul FAB principale
        fabMain.setOnClickListener(v -> {
            if (isFabMenuOpen) {
                fabMain.animate().rotation(0f).setDuration(300).start();
                closeFab(fab1, 0);
                closeFab(fab2, 50);
                closeFab(fab3, 100);

                fabMenuLayout.setVisibility(View.GONE);
                fabMain.setImageResource(R.drawable.ic_add);
                fabMain.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.primary_medium));
                fabMain.setColorFilter(null);
                isFabMenuOpen = false;
            } else {
                fabMain.animate().rotation(45f).setDuration(300).start();
                openFab(fab1, 0);
                openFab(fab2, 50);
                openFab(fab3, 100);

                fabMenuLayout.setVisibility(View.VISIBLE);
                fabMain.setImageResource(R.drawable.ic_add);
                fabMain.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.text_dark));
                fabMain.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.primary_medium));
                isFabMenuOpen = true;
            }

            SharedPreferences prefs = getSharedPreferences("HOME_PREFS", MODE_PRIVATE);

            MaterialCardView cardUltime = findViewById(R.id.cardUltimeReazioni); // assegna ID a MaterialCardView XML
            //MaterialCardView cardPollini = findViewById(R.id.cardDatiPollini);   // crea ID
            MaterialCardView cardKit = findViewById(R.id.cardKit);               // crea ID
            MaterialCardView cardConsiglio = findViewById(R.id.cardConsiglioGiorno); // crea ID

            cardUltime.setVisibility(prefs.getBoolean("ultime_reazioni", true) ? View.VISIBLE : View.GONE);
            //cardPollini.setVisibility(prefs.getBoolean("dati_pollini", true) ? View.VISIBLE : View.GONE);
            cardKit.setVisibility(prefs.getBoolean("kit_emergenza", true) ? View.VISIBLE : View.GONE);
            cardConsiglio.setVisibility(prefs.getBoolean("consiglio_giorno", true) ? View.VISIBLE : View.GONE);

        });

        // Azioni sui FAB secondari
        fab1.setOnClickListener(v -> {
            Intent i = new Intent(this, QualitàAriaActivity.class);
            startActivity(i);
        });
        fab2.setOnClickListener(v -> {
            Intent i = new Intent(this, FarmaciActivity.class);
            startActivity(i);
        });
        fab3.setOnClickListener(v -> { /* TODO: Azione FAB3 */ });

        // Controlla permessi posizione all’avvio
        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_PERMISSION_REQUEST_CODE, this::fetchLocationAndWeather);

        // Bottone SOS
        MaterialButton btnSOS = findViewById(R.id.btnSOS);
        btnSOS.setOnClickListener(v -> {
            checkPermission(Manifest.permission.CALL_PHONE, CALL_PERMISSION_REQUEST_CODE, this::makeSOSCall);
        });

        // Bottone fotocamera / scanner
        FloatingActionButton fabCamera = findViewById(R.id.btnCamera);
        fabCamera.setOnClickListener(v -> {
            checkPermission(Manifest.permission.CAMERA,
                    CAMERA_PERMISSION_REQUEST_CODE,
                    () -> {
                        // Apri la nuova activity per lo scanner
                        Intent i = new Intent(this, ScannerActivity.class);
                        startActivity(i);
                    });
        });

        btnMostraKit = findViewById(R.id.btnMostraKit);
        btnMostraKit.setOnClickListener(v -> {
            Intent i = new Intent(this, KitEmergenzaActivity.class);
            startActivity(i);
        });

    }

    private void openFab(ExtendedFloatingActionButton fab, int delay) {
        fab.setVisibility(View.VISIBLE);
        fab.setAlpha(0f);
        fab.setTranslationY(100f);
        fab.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(delay)
                .setDuration(300)
                .start();
    }

    private void closeFab(ExtendedFloatingActionButton fab, int delay) {
        fab.animate()
                .alpha(0f)
                .translationY(100f)
                .setStartDelay(delay)
                .setDuration(300)
                .withEndAction(() -> fab.setVisibility(View.GONE))
                .start();
    }

    // Metodo universale per i permessi
    private void checkPermission(String permission, int requestCode, Runnable onGranted) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionCallbacks.put(requestCode, onGranted);
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        } else {
            onGranted.run();
        }
    }

    // Gestione della risposta ai permessi
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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

    // Chiamata SOS
    private void makeSOSCall() {
        String phoneNumber = "118";
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    // Recupero posizione + meteo
    private void fetchLocationAndWeather() {
        WeatherManager weatherManager = new WeatherManager(this, "7ad36df0013e9b8963d7d47bcca7cfec");
        weatherManager.fetchWeather(new WeatherManager.WeatherCallback() {
            @Override
            public void onWeatherUpdated(String temperature, int iconResId, String Location) {
                tvTemperature.setText(temperature);
                Drawable weatherIcon = ContextCompat.getDrawable(MainActivity.this, iconResId);
                tvTemperature.setCompoundDrawablesWithIntrinsicBounds(null, null, weatherIcon, null);
                tvLocation.setText(Location);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Impossibile recuperare il meteo", Toast.LENGTH_SHORT).show();
            }
        });
    }


}