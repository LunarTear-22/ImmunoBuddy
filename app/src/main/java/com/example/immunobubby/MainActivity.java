package com.example.immunobubby;

import static java.security.AccessController.getContext;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity {

    private boolean isFabMenuOpen = false;
    private TextView tvTemperature, tvLocation, tvConsiglio;
    private TextView tvPolline1, tvPolline2, tvPolline3;
    private MaterialButton btnMostraKit, btnMostraPollini, btnMostraReazioni;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private static final int CALL_PERMISSION_REQUEST_CODE = 200;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 300;

    private final String[] consigli = {
            "Arieggia la casa al mattino presto, quando i pollini sono meno presenti.",
            "Lava i capelli la sera per non portare pollini a letto.",
            "Usa occhiali da sole grandi per proteggere gli occhi dai pollini.",
            "Preferisci le uscite dopo la pioggia: l’aria è più pulita.",
            "Tieni le finestre chiuse nei giorni di vento forte.",
            "Evita di stendere i panni all’aperto in primavera.",
            "Porta sempre con te un fazzoletto di cotone pulito.",
            "Usa filtri antipolline in auto e climatizzatori.",
            "Lava spesso le mani e il viso quando torni a casa.",
            "Controlla il bollettino pollinico della tua zona.",
            "Indossa una mascherina leggera nei periodi di alta concentrazione.",
            "Evita di tagliare il prato se sei allergico alle graminacee.",
            "Mantieni puliti tappeti e tende per ridurre gli acari.",
            "Preferisci pavimenti lavabili invece della moquette.",
            "Riduci i peluche in camera da letto se sei allergico alla polvere.",
            "Viaggia con farmaci d’emergenza prescritti dal medico.",
            "Usa federe e coprimaterassi antiacaro.",
            "Non toccarti il viso con le mani sporche.",
            "Bevi acqua frequentemente per mantenere idratate le mucose.",
            "Consulta regolarmente il tuo allergologo per aggiornare la terapia."
    };

    private final Map<Integer, Runnable> permissionCallbacks = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences userPrefs = getSharedPreferences("UserData", MODE_PRIVATE);
        boolean profileCompleted = userPrefs.getBoolean("profile_completed", false);
        boolean firstLaunchDone = userPrefs.getBoolean("first_launch_done", false);

        if (!firstLaunchDone) {
            // Segna che la prima apertura è stata gestita
            userPrefs.edit().putBoolean("first_launch_done", true).apply();

            // Mostra welcome screen
            startActivity(new Intent(this, WelcomeActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return; // interrompe l'esecuzione di onCreate
        }

        setContentView(R.layout.activity_main);

        // Se il profilo non è ancora completato, manda l'utente a completarlo
        if (!profileCompleted) {
            startActivity(new Intent(this, AccountDataActivity.class));
            finish();
            return;
        }

        TextView tvWelcome = findViewById(R.id.tvWelcome);

        tvWelcome.setText("Ciao, " + userPrefs.getString("nome", "Utente"));

        // FAB principale e secondari
        FloatingActionButton fabMain = findViewById(R.id.btnFab);
        LinearLayout fabMenuLayout = findViewById(R.id.fabMenuLayout);
        ExtendedFloatingActionButton fab1 = findViewById(R.id.btnReazioni);
        ExtendedFloatingActionButton fab2 = findViewById(R.id.btnPromemoria);
        ExtendedFloatingActionButton fab3 = findViewById(R.id.btnSintomi);

        // TextView principali
        tvTemperature = findViewById(R.id.tvTemperature);
        tvLocation = findViewById(R.id.tvLocation);
        tvConsiglio = findViewById(R.id.tvConsiglio);

        // TextView anteprima pollini
        tvPolline1 = findViewById(R.id.tvPolline1);
        tvPolline2 = findViewById(R.id.tvPolline2);
        tvPolline3 = findViewById(R.id.tvPolline3);

        // Bottoni
        btnMostraKit = findViewById(R.id.btnMostraKit);
        btnMostraPollini = findViewById(R.id.btnMostraPollini);
        btnMostraReazioni = findViewById(R.id.btnMostraReazioni);

        // Mostra consiglio del giorno
        mostraConsiglioGiornaliero();

        // Mostra visibilità card
        aggiornaVisibilitaCard();

        // Toggle FAB menu
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
        });

        // Azioni sui FAB secondari
        fab1.setOnClickListener(v -> startActivity(new Intent(this, NuovaReazioneActivity.class)));
        fab2.setOnClickListener(v -> startActivity(new Intent(this, FarmaciActivity.class)));
        fab3.setOnClickListener(v -> startActivity(new Intent(this, NuovoSintomoActivity.class)));

        // Permessi posizione → richiesta unica
        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_PERMISSION_REQUEST_CODE, () -> {
            fetchLocationAndWeather();
            caricaPollini();
        });

        // Bottone SOS
        findViewById(R.id.btnSOS).setOnClickListener(v ->
                checkPermission(Manifest.permission.CALL_PHONE, CALL_PERMISSION_REQUEST_CODE, this::makeSOSCall));

        // Bottone fotocamera / scanner
        findViewById(R.id.btnCamera).setOnClickListener(v ->
                checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_REQUEST_CODE,
                        () -> startActivity(new Intent(this, ScannerActivity.class))));

        // Bottoni kit e pollini
        btnMostraKit.setOnClickListener(v -> startActivity(new Intent(this, KitEmergenzaActivity.class)));
        btnMostraPollini.setOnClickListener(v -> startActivity(new Intent(this, QualitàAriaActivity.class)));
        btnMostraReazioni.setOnClickListener(v -> startActivity(new Intent(this, ReazioniAllergicheActivity.class)));

        //kit

        SharedPreferences sharedPreferences = getSharedPreferences("KitEmergenzaPrefs", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("medicines", null);
        Type type = new TypeToken<List<Farmaco>>() {}.getType();
        List<Farmaco> farmacoList = gson.fromJson(json, type);
        List<Kit> kitList = new ArrayList<>();
        if (farmacoList == null || farmacoList.isEmpty()) {
            farmacoList = new ArrayList<>();
        }else{

            if (!farmacoList.isEmpty()) {
                int max = Math.min(farmacoList.size(), 2);
                for (int i = 0; i < max; i++) {
                    Kit k = new Kit();
                    k.setName(farmacoList.get(i).getNome() + " " + farmacoList.get(i).getDosaggio());
                    k.setDescription(farmacoList.get(i).getTipologia());
                    kitList.add(k);
                }

            }

        }

       if (kitList.isEmpty()) {

           kitList.add(new Kit("Il tuo kit è vuoto", "", ""));
       }

        RecyclerView recyclerView = findViewById(R.id.kit_preview_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        KitAdapter adapter = new KitAdapter(this, kitList);
        recyclerView.setAdapter(adapter);

        //reazioni


        List<Reazione> reazioniList = ReazioneStorage.loadReactions(this);
        for(Reazione r : reazioniList){
            System.out.println(r.getAllergene());
        }
        List<ReazioniPreview> ReazioniPreviewList = new ArrayList<>();
        if (reazioniList == null || reazioniList.isEmpty()) {
            reazioniList = new ArrayList<>();
        }else{

            if (!reazioniList.isEmpty()) {
                int max = Math.min(reazioniList.size(), 3);
                for (int i = 0; i < max; i++) {
                    ReazioniPreview r = new ReazioniPreview();
                    if (reazioniList.get(i) == null) r.setName ("Reazione allergica");
                    String allergen = reazioniList.get(i).getAllergene();
                    List<String> symptoms = reazioniList.get(i).getSintomi();
                    if (allergen != null && !allergen.isEmpty()) {
                        r.setName(symptoms != null && !symptoms.isEmpty()
                                ? symptoms.get(0) + " da " + allergen
                                : "Reazione a " + allergen);
                    } else if (symptoms != null && !symptoms.isEmpty()) {
                        r.setName (symptoms.get(0));
                    } else {
                        r.setName ("Reazione allergica");
                    }

                    r.setData(reazioniList.get(i).getData());
                    ReazioniPreviewList.add(r);
                    for(ReazioniPreview q : ReazioniPreviewList){
                        System.out.println(q.toString());
                    }
                }

            }

        }

        if (ReazioniPreviewList.isEmpty()) {

            ReazioniPreviewList.add(new ReazioniPreview("Non sono presenti reazioni recenti", null));
        }

        RecyclerView recyclerViewReazioni = findViewById(R.id.reazioni_preview_recycler);
        recyclerViewReazioni.setLayoutManager(new LinearLayoutManager(this));
        ReazionePreviewAdapter adapterReazioni = new ReazionePreviewAdapter(this, ReazioniPreviewList);
        recyclerViewReazioni.setAdapter(adapterReazioni);



    }

    private void aggiornaVisibilitaCard() {
        SharedPreferences prefs = getSharedPreferences("HOME_PREFS", MODE_PRIVATE);

        MaterialCardView cardUltime = findViewById(R.id.cardUltimeReazioni);
        MaterialCardView cardPollini = findViewById(R.id.cardPollini);
        MaterialCardView cardKit = findViewById(R.id.cardKit);
        MaterialCardView cardConsiglio = findViewById(R.id.cardConsiglioGiorno);

        cardUltime.setVisibility(prefs.getBoolean("ultime_reazioni", true) ? View.VISIBLE : View.GONE);
        cardPollini.setVisibility(prefs.getBoolean("dati_pollini", true) ? View.VISIBLE : View.GONE);
        cardKit.setVisibility(prefs.getBoolean("kit_emergenza", true) ? View.VISIBLE : View.GONE);
        cardConsiglio.setVisibility(prefs.getBoolean("consiglio_giorno", true) ? View.VISIBLE : View.GONE);
    }

    private void mostraConsiglioGiornaliero() {
        SharedPreferences prefs = getSharedPreferences("CONSILGIO_PREFS", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        long lastSavedDay = prefs.getLong("last_day", -1);
        int savedIndex = prefs.getInt("last_index", -1);
        long today = System.currentTimeMillis() / (1000 * 60 * 60 * 24);

        int indexToShow;
        if (today == lastSavedDay && savedIndex != -1) {
            indexToShow = savedIndex;
        } else {
            indexToShow = (int) (Math.random() * consigli.length);
            editor.putLong("last_day", today);
            editor.putInt("last_index", indexToShow);
            editor.apply();
        }
        tvConsiglio.setText(consigli[indexToShow]);
    }



    @SuppressLint("MissingPermission")
    private void caricaPollini() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        double lat = location.getLatitude();
                        double lon = location.getLongitude();

                        PollenManager pollenManager = new PollenManager(this, "AIzaSyAdeZWce_XP6K9Iq5GuTz6-bGHZ8XcWMsU");
                        pollenManager.fetchPollens(lat, lon, new PollenManager.PollenCallback() {
                            @Override
                            public void onData(List<PollenData> pollens) {
                                runOnUiThread(() -> {
                                    Map<String, String> pollenNamesIt = new HashMap<String, String>() {{
                                        put("alder", "Ontano"); put("hazel", "Nocciolo"); put("ash", "Frassino"); put("fraxinus", "Frassino");
                                        put("birch", "Betulla"); put("poplar", "Pioppo"); put("willow", "Salice"); put("oak", "Quercia");
                                        put("olive", "Olivo"); put("pine", "Pino"); put("cedar", "Cedro"); put("japanese_cedar", "Cedro giapponese");
                                        put("japanese_cypress", "Cipresso giapponese"); put("maple", "Acero"); put("elm", "Olmo");
                                        put("juniper", "Ginepro"); put("mulberry", "Gelso"); put("cottonwood", "Pioppo da cotone"); put("hornbeam", "Carpino");
                                        put("beech", "Faggio"); put("cypress_pine", "Pino cipresso");

                                        put("grass", "Graminacee"); put("rye", "Segale"); put("timothy", "Timoteo"); put("plantain", "Piantaggine");
                                        put("herbs", "Erbe"); put("bluegrass", "Festuca"); put("fescue", "Fescua"); put("bentgrass", "Poacea");
                                        put("oat", "Avena"); put("ryegrass", "Lolium"); put("meadowgrass", "Erba medica"); put("sweet_vernal_grass", "Erba dolce");
                                        put("redtop", "Erba rossa"); put("creeping_bentgrass", "Poa strisciante"); put("timothy_hay", "Fieno di timoteo");

                                        put("ragweed", "Ambrosia"); put("ambrosia", "Ambrosia"); put("mugwort", "Artemisia"); put("artemisia", "Artemisia");
                                        put("chamomile", "Camomilla"); put("nettle", "Ortica"); put("dock", "Acetosella"); put("sagebrush", "Artemisia");
                                        put("thistle", "Cardo"); put("pigweed", "Poligono"); put("lamb's_quarters", "Chenopodio"); put("goosefoot", "Chenopodio");
                                        put("sowthistle", "Sonchus"); put("dandelion", "Dente di leone"); put("plantain_weed", "Piantaggine");
                                        put("common_ragweed", "Ambrosia comune"); put("giant_ragweed", "Ambrosia gigante"); put("common_mugwort", "Artemisia comune"); put("giant_hogweed", "Erba del diavolo");

                                        put("mold", "Muffa"); put("alternaria", "Alternaria"); put("cladosporium", "Cladosporium");
                                        put("aspergillus", "Aspergillus"); put("penicillium", "Penicillium");
                                    }};

                                    if (pollens.size() > 0) tvPolline1.setText(pollenNamesIt.getOrDefault(pollens.get(0).getName().toLowerCase(), pollens.get(0).getName()));
                                    if (pollens.size() > 1) tvPolline2.setText(pollenNamesIt.getOrDefault(pollens.get(1).getName().toLowerCase(), pollens.get(1).getName()));
                                    if (pollens.size() > 2) tvPolline3.setText(pollenNamesIt.getOrDefault(pollens.get(2).getName().toLowerCase(), pollens.get(2).getName()));
                                });
                            }

                            @Override
                            public void onError(Exception e) {
                                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Errore caricamento pollini", Toast.LENGTH_SHORT).show());
                            }
                        });
                    } else {
                        Toast.makeText(this, "Posizione non disponibile", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openFab(ExtendedFloatingActionButton fab, int delay) {
        fab.setVisibility(View.VISIBLE);
        fab.setAlpha(0f);
        fab.setTranslationY(100f);
        fab.animate().alpha(1f).translationY(0f).setStartDelay(delay).setDuration(300).start();
    }

    private void closeFab(ExtendedFloatingActionButton fab, int delay) {
        fab.animate().alpha(0f).translationY(100f).setStartDelay(delay).setDuration(300)
                .withEndAction(() -> fab.setVisibility(View.GONE)).start();
    }

    private void checkPermission(String permission, int requestCode, Runnable onGranted) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionCallbacks.put(requestCode, onGranted);
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        } else {
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

    private void makeSOSCall() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:118"));
        startActivity(intent);
    }

    private void fetchLocationAndWeather() {
        WeatherManager weatherManager = new WeatherManager(this, "7ad36df0013e9b8963d7d47bcca7cfec");
        weatherManager.fetchWeather(new WeatherManager.WeatherCallback() {
            @Override
            public void onWeatherUpdated(String temperature, int iconResId, String Location) {
                SharedPreferences prefs = getSharedPreferences("HOME_PREFS", MODE_PRIVATE);
                String tempUnit = prefs.getString("temp_unit", "C");

                String displayTemp = temperature;
                if ("F".equals(tempUnit)) {
                    try {
                        float tempC = Float.parseFloat(temperature.replace("°C", ""));
                        float tempF = tempC * 9 / 5 + 32;
                        displayTemp = Math.round(tempF) + "°F";
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }

                tvTemperature.setText(displayTemp);
                Drawable weatherIcon = ContextCompat.getDrawable(MainActivity.this, iconResId);
                tvTemperature.setCompoundDrawablesWithIntrinsicBounds(null, null, weatherIcon, null);
                tvLocation.setText(Location);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this, "Impossibile recuperare il meteo", Toast.LENGTH_SHORT).show()
                );
            }

        });
    }

}
