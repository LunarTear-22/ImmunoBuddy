package com.example.immunobubby;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
public class AccountDataActivity extends BaseActivity {

    private TextInputEditText inputNome, inputCognome, inputDob;
    private AutoCompleteTextView dropdownGender;
    private MaterialCheckBox checkCaregiver;
    private FloatingActionButton btnFab;

    private SharedPreferences prefs;

    // Banner views
    private ConstraintLayout background;
    private MaterialCardView customBanner;
    private ImageView bannerIcon;
    private TextView bannerMessage;
    private MaterialButton bannerAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_data);

        boolean hideNavbar = getIntent().getBooleanExtra("hideNavbar", false);

        if (hideNavbar) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().hide(); // nasconde la ActionBar
            }

            // Se usi anche una BottomNavigationView
            View bottomNav = findViewById(R.id.navbar);
            if (bottomNav != null) {
                bottomNav.setVisibility(View.GONE);
            }
        }

        // Intercetta il back con OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isFormDirty()) {
                    new androidx.appcompat.app.AlertDialog.Builder(AccountDataActivity.this)
                            .setTitle("Attenzione")
                            .setMessage("Stai compilando il form. Vuoi davvero uscire senza salvare?")
                            .setPositiveButton("Esci", (dialog, which) -> {
                                // Disabilito il callback per permettere la chiusura
                                setEnabled(false);
                                getOnBackPressedDispatcher().onBackPressed();
                            })
                            .setNegativeButton("Annulla", (dialog, which) -> dialog.dismiss())
                            .show();
                } else {
                    // Nessun dato inserito → chiusura normale
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });

        MaterialButton backButton = findViewById(R.id.btnBack);
        backButton.setOnClickListener(v -> {
            if (isFormDirty()) {
                new androidx.appcompat.app.AlertDialog.Builder(AccountDataActivity.this)
                        .setTitle("Attenzione")
                        .setMessage("Stai compilando il form. Vuoi davvero uscire senza salvare?")
                        .setPositiveButton("Esci", (dialog, which) -> {
                            finish(); // chiude l'activity
                        })
                        .setNegativeButton("Annulla", (dialog, which) -> dialog.dismiss())
                        .show();
            } else {
                finish(); // Nessun dato → chiusura diretta
            }
        });



        prefs = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        // Trova la view dal layout

        // campi form
        inputNome = findViewById(R.id.input_nome);
        inputCognome = findViewById(R.id.input_cognome);
        inputDob = findViewById(R.id.input_dob);
        dropdownGender = findViewById(R.id.dropdown);
        checkCaregiver = findViewById(R.id.check_caregiver);
        btnFab = findViewById(R.id.btnFab);

        setupGenderDropdown();
        setupDropdownDividerColor(dropdownGender);

        // banner
        background = findViewById(R.id.background);
        customBanner = findViewById(R.id.customBanner);
        bannerIcon = customBanner.findViewById(R.id.save_icon);
        bannerMessage = customBanner.findViewById(R.id.bannerMessage);
        bannerAction = customBanner.findViewById(R.id.bannerAction);

        // gender dropdown
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.dropdown_item,
                getResources().getStringArray(R.array.gender_options)
        );
        dropdownGender.setAdapter(adapter);

        // date picker
        inputDob.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker =
                    MaterialDatePicker.Builder.datePicker()
                            .setTitleText("Seleziona la data di nascita")
                            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                            .build();

            datePicker.addOnPositiveButtonClickListener(selection -> {
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                calendar.setTimeInMillis(selection);

                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                inputDob.setText(format.format(calendar.getTime()));
            });

            datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
        });

        loadUserData();

        btnFab.setOnClickListener(v -> saveUserData());
        bannerAction.setOnClickListener(v -> closeBanner());
    }

    private boolean isFormDirty() {
        return (inputNome.getText() != null &&
                !inputNome.getText().toString().trim().isEmpty())
                ||
                (inputCognome.getText() != null &&
                        !inputCognome.getText().toString().trim().isEmpty())
                ||
                (inputDob.getText() != null &&
                        !inputDob.getText().toString().trim().isEmpty())
                ||
                (dropdownGender.getText() != null &&
                        !dropdownGender.getText().toString().trim().isEmpty())
                ||
                checkCaregiver.isChecked();
    }


    private void setupGenderDropdown() {
        dropdownGender.setKeyListener(null);
        dropdownGender.setFocusable(false);
        dropdownGender.setClickable(true);
    }

    private void saveUserData() {
        String nome = inputNome.getText().toString().trim();

        if (nome.isEmpty()) {
            showBannerError("Per completare il profilo, inserisci almeno il nome");
            customBanner.postDelayed(this::closeBanner, 3000);
            return;
        }

        try {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("nome", nome);
            editor.putString("cognome", inputCognome.getText().toString().trim());
            editor.putString("dob", inputDob.getText().toString().trim());
            editor.putString("gender", dropdownGender.getText().toString().trim());
            editor.putBoolean("caregiver", checkCaregiver.isChecked());

            // Flag per profilo completato
            editor.putBoolean("profile_completed", true);

            editor.apply();

            showBannerSave("Dati salvati con successo");

            // Chiude banner e torna alla home dopo 3 secondi
            customBanner.postDelayed(() -> {
                closeBanner();
                startActivity(new Intent(AccountDataActivity.this, MainActivity.class));
                finish();
            }, 3000);

        } catch (Exception e) {
            showBannerError("Errore durante il salvataggio");
            customBanner.postDelayed(this::closeBanner, 3000);
        }
    }



    private void loadUserData() {
        inputNome.setText(prefs.getString("nome", ""));
        inputCognome.setText(prefs.getString("cognome", ""));
        inputDob.setText(prefs.getString("dob", ""));
        dropdownGender.setText(prefs.getString("gender", ""), false);
        checkCaregiver.setChecked(prefs.getBoolean("caregiver", false));
    }

    // === BANNER LOGIC ===
    private void showBannerSave(String messaggio) {
        setupBanner(messaggio, R.color.text_dark, R.drawable.bookmark_check_24px,
                R.color.primary_dark, R.color.text_dark);
    }

    private void showBannerError(String messaggio) {
        setupBanner(messaggio, R.color.text_danger, R.drawable.dangerous_24px,
                R.color.danger, R.color.text_danger);
    }

    private void setupBanner(String messaggio, int textColor, int iconRes,
                             int backgroundColor, int strokeColor) {
        background.setVisibility(View.VISIBLE);
        bannerMessage.setText(messaggio);
        bannerMessage.setTextColor(getColor(textColor));
        bannerIcon.setImageResource(iconRes);
        customBanner.setCardBackgroundColor(getColor(backgroundColor));
        customBanner.setStrokeColor(getColor(strokeColor));
        customBanner.setStrokeWidth(2);
        customBanner.setVisibility(View.VISIBLE);
    }

    private void closeBanner() {
        customBanner.setVisibility(View.GONE);
        background.setVisibility(View.GONE);
    }
}
