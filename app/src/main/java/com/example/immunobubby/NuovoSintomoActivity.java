package com.example.immunobubby;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class NuovoSintomoActivity extends BaseActivity {



    private static class SintomoOption {
        String label;
        int colorRes;

        SintomoOption(String label, int colorRes) {
            this.label = label;
            this.colorRes = colorRes;
        }
    }
    private TextInputLayout inputNomeSintomo;
    private TextInputLayout inputGravita;
    private TextInputLayout inputFrequenza;

    private TextInputEditText editNomeSintomo;
    private AutoCompleteTextView autoCompleteTextView;
    private AutoCompleteTextView autoCompleteFrequenza;
    private FloatingActionButton saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuovo_sintomo);
        autoCompleteTextView = findViewById(R.id.dropdown);
        setupDropdown(autoCompleteTextView);
        setupDropdownDividerColor(autoCompleteTextView);
        editNomeSintomo = findViewById(R.id.inputSintomo);

        saveBtn = findViewById(R.id.btnFab);
        saveBtn.setOnClickListener(v -> salvaSintomo());

        inputNomeSintomo = findViewById(R.id.inputNomeSintomo);
        inputGravita = findViewById(R.id.inputGravita);
        inputFrequenza = findViewById(R.id.inputFrequenza);

        // Trova la view dal layout


        String[] options = {"Lieve", "Moderato", "Significativo", "Grave"};

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



        autoCompleteTextView.setAdapter(adapter);

        // Dropdown frequenza
        autoCompleteFrequenza = findViewById(R.id.dropdownFrequenza);
        setupDropdownDividerColor(autoCompleteFrequenza);
        setupDropdown(autoCompleteFrequenza);
        String[] optionsFrequenza = {"Frequente", "Molto frequente", "Raro", "Molto raro"};

        ArrayAdapter<String> frequenzaAdapter = new ArrayAdapter<String>(
                this,
                R.layout.dropdown_frequenza,
                optionsFrequenza
        ) {
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
                    convertView = inflater.inflate(R.layout.dropdown_frequenza, parent, false);
                }

                TextView label = convertView.findViewById(R.id.labelFrequenza);
                label.setText(optionsFrequenza[position]);

                return convertView;
            }
        };

        autoCompleteFrequenza.setAdapter(frequenzaAdapter);

    }

    private void setupDropdown(AutoCompleteTextView autoCompleteTextView) {
        autoCompleteTextView.setKeyListener(null);
        autoCompleteTextView.setFocusable(false);
        autoCompleteTextView.setClickable(true);
    }

    private boolean validateSintomoForm() {
        boolean isValid = true;

        if (Objects.requireNonNull(editNomeSintomo.getText()).toString().trim().isEmpty()) {
            inputNomeSintomo.setError("Campo obbligatorio");
            isValid = false;
        } else {
            inputNomeSintomo.setError(null);
        }

        if (autoCompleteTextView.getText().toString().trim().isEmpty()) {
            inputGravita.setError("Seleziona la gravità");
            isValid = false;
        } else {
            inputGravita.setError(null);
        }

        if (autoCompleteFrequenza.getText().toString().trim().isEmpty()) {
            inputFrequenza.setError("Seleziona la frequenza");
            isValid = false;
        } else {
            inputFrequenza.setError(null);
        }

        return isValid;
    }

    private void salvaSintomo() {
        if (!validateSintomoForm()) {
            Toast.makeText(this, "Compila tutti i campi obbligatori", Toast.LENGTH_SHORT).show();
            return;
        }

        String nomeSintomo = Objects.requireNonNull(editNomeSintomo.getText()).toString();
        String gravita = autoCompleteTextView.getText().toString();
        String frequenza = autoCompleteFrequenza.getText().toString();

        Sintomi nuovoSintomo = new Sintomi(nomeSintomo, frequenza, gravita);

        // Recupera lista salvata
        List<Sintomi> sintomiSalvati = SintomiStorage.loadSintomi(this);
        if (sintomiSalvati == null) {
            sintomiSalvati = new ArrayList<>();
        }

        sintomiSalvati.add(nuovoSintomo);

        // Salva lista aggiornata
        SintomiStorage.saveSintomi(this, sintomiSalvati);

        Toast.makeText(this, "Sintomo salvato!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, SintomiActivity.class);
        startActivity(intent);
    }


}


