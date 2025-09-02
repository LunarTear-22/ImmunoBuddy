package com.example.immunobubby;

import android.content.ClipData;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
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
    private AutoCompleteTextView autoCompleteTextView;
    private AutoCompleteTextView autoCompleteFrequenza;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuovo_sintomo);
        autoCompleteTextView = findViewById(R.id.dropdown);
        setupDropdown(autoCompleteTextView);
        setupDropdownDividerColor(autoCompleteTextView);

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

    }


