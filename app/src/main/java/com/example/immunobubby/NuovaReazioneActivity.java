package com.example.immunobubby;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;
import androidx.core.graphics.drawable.DrawableCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class NuovaReazioneActivity extends BaseActivity {

    private TextInputEditText dataEditText, oraEditText, allergeneEditText, sintomiEditText, farmacEditText, noteEditText;
    private AutoCompleteTextView gravitaDropdown;
    private TextInputLayout dataLayout, oraLayout, allergeneLayout, gravitaLayout, sintomiLayout, farmacLayout, noteLayout;
    private RadioGroup medicoRadioGroup;
    private RadioButton siRadioButton, noRadioButton;
    private Button aggiungiFotoButton;
    private FloatingActionButton saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuova_reazione);

        initializeViews();
        setupClickListeners();
        setupTextWatchers();
        setupGravitaDropdown();

        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.dropdown);

        String[] options = {"Lieve", "Moderato", "Significativo", "Grave"};
        AutoCompleteTextView dropdown = findViewById(R.id.dropdown);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.dropdown_gravita, options) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                return createCustomView(position, convertView, parent);
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                return createCustomView(position, convertView, parent);
            }

            private View createCustomView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    convertView = inflater.inflate(R.layout.dropdown_gravita, parent, false);
                }

                TextView label = convertView.findViewById(R.id.labelGravita);
                ImageView icon = convertView.findViewById(R.id.iconGravita);

                label.setText(options[position]);

                int drawableRes;
                switch (options[position]) {
                    case "Lieve":
                        drawableRes = R.drawable.cerchio_verde;
                        break;
                    case "Moderato":
                        drawableRes = R.drawable.cerchio_giallo;
                        break;
                    case "Significativo":
                        drawableRes = R.drawable.cerchio_arancione;
                        break;
                    case "Grave":
                        drawableRes = R.drawable.cerchio_rosso;
                        break;
                    default:
                        drawableRes = 0;
                }

                if (drawableRes != 0) {
                    icon.setImageResource(drawableRes);
                } else {
                    icon.setImageDrawable(null);
                }

                return convertView;
            }
        };


        dropdown.setAdapter(adapter);
        autoCompleteTextView.setAdapter(adapter);
    }

    private void initializeViews() {
        dataEditText = findViewById(R.id.data_edit_text);
        oraEditText = findViewById(R.id.ora_edit_text);
        allergeneEditText = findViewById(R.id.allergene_edit_text);
        gravitaDropdown = findViewById(R.id.dropdown);
        sintomiEditText = findViewById(R.id.sintomi_edit_text);
        farmacEditText = findViewById(R.id.farmaci_edit_text);
        noteEditText = findViewById(R.id.note_edit_text);

        dataLayout = findViewById(R.id.data_layout);
        oraLayout = findViewById(R.id.ora_layout);
        allergeneLayout = findViewById(R.id.allergene_layout);
        sintomiLayout = findViewById(R.id.sintomi_layout);
        farmacLayout = findViewById(R.id.farmaci_layout);
        noteLayout = findViewById(R.id.note_layout);

        medicoRadioGroup = findViewById(R.id.medico_radio_group);
        siRadioButton = findViewById(R.id.si_radio_button);
        noRadioButton = findViewById(R.id.no_radio_button);

        aggiungiFotoButton = findViewById(R.id.aggiungi_image_btn);
        saveButton = findViewById(R.id.btnCeck);
    }

    private void showDatePicker() {
        // Costruisci il MaterialDatePicker
        MaterialDatePicker<Long> datePicker =
                MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Seleziona una data")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds()) // selezione iniziale = oggi
                        .build();

        // Listener quando l'utente conferma la scelta
        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setTimeInMillis(selection);

            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String date = format.format(calendar.getTime());
            dataEditText.setText(date);
        });

        // Mostra il date picker
        datePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
    }


    private void showTimePicker() {
        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);

        MaterialTimePicker picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(hour)
                .setMinute(minute)
                .setTitleText("Scegli l'orario")
                .build();

        picker.addOnPositiveButtonClickListener(v -> {
            int selectedHour = picker.getHour();
            int selectedMinute = picker.getMinute();

            oraEditText.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
        });

        picker.show(getSupportFragmentManager(), "TIME_PICKER");
    }

    private static final int PICK_IMAGE_REQUEST = 1;

    private void selectPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                // Qui potresti mostrare la foto in un ImageView
                Toast.makeText(this, "Foto selezionata: " + selectedImageUri.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveReaction() {
        if (validateForm()) {
            // Qui puoi salvare i dati in un database locale o inviarli a un server
            Toast.makeText(this, "Reazione salvata con successo!", Toast.LENGTH_SHORT).show();

            // Vai alla pagina ReazioniAllergiche
            Intent intent = new Intent(NuovaReazioneActivity.this, ReazioniAllergicheActivity.class);
            startActivity(intent);

            finish(); // Chiudi l'activity corrente così non torni indietro con "back"
        } else {
            Toast.makeText(this, "Compila tutti i campi obbligatori!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupClickListeners() {
        dataEditText.setOnClickListener(v -> showDatePicker());
        oraEditText.setOnClickListener(v -> showTimePicker());

        aggiungiFotoButton.setOnClickListener(v -> selectPhoto());
        saveButton.setOnClickListener(v -> saveReaction());
    }

    private void setupTextWatchers() {
        setupFieldWatcher(dataEditText, dataLayout);
        setupFieldWatcher(oraEditText, oraLayout);
        setupFieldWatcher(allergeneEditText, allergeneLayout);
        setupFieldWatcher(sintomiEditText, sintomiLayout);
        setupFieldWatcher(farmacEditText, farmacLayout);
        setupFieldWatcher(noteEditText, noteLayout);

        // Setup gravity dropdown watcher
        gravitaDropdown.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                updateFieldBackground(gravitaLayout, !s.toString().trim().isEmpty());
            }
        });
    }

    private void setupFieldWatcher(TextInputEditText editText, TextInputLayout layout) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                updateFieldBackground(layout, !s.toString().trim().isEmpty());
            }
        });
    }

    private void updateFieldBackground(TextInputLayout layout, boolean isFilled) {
        if (isFilled) {
            layout.setBoxBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        } else {
            layout.setBoxBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        }
    }

    private void setupGravitaDropdown() {
        gravitaDropdown.setKeyListener(null);
        gravitaDropdown.setFocusable(false);
        gravitaDropdown.setClickable(true);
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }



    private boolean validateForm() {
        boolean isValid = true;

        if (dataEditText.getText().toString().trim().isEmpty()) {
            dataLayout.setError("Campo obbligatorio");
            isValid = false;
        } else {
            dataLayout.setError(null);
        }

        if (allergeneEditText.getText().toString().trim().isEmpty()) {
            allergeneLayout.setError("Campo obbligatorio");
            isValid = false;
        } else {
            allergeneLayout.setError(null);
        }

        if (sintomiEditText.getText().toString().trim().isEmpty()) {
            sintomiLayout.setError("Campo obbligatorio");
            isValid = false;
        } else {
            sintomiLayout.setError(null);
        }

        return isValid;
    }
}
