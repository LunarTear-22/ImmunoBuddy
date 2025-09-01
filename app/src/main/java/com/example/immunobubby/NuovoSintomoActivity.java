package com.example.immunobubby;

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
import com.google.android.material.textfield.TextInputEditText;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuovo_sintomo);

        // Trova la view dal layout
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

        // Campo per la data
        TextInputEditText inputDataSintomo = findViewById(R.id.inputDataSintomo);
        if (inputDataSintomo != null) {
            inputDataSintomo.setOnClickListener(v -> {
                MaterialDatePicker<Long> datePicker =
                        MaterialDatePicker.Builder.datePicker()
                                .setTitleText("Seleziona la data del sintomo")
                                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                                .build();

                datePicker.addOnPositiveButtonClickListener(selection -> {
                    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                    calendar.setTimeInMillis(selection);

                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    inputDataSintomo.setText(format.format(calendar.getTime()));
                });

                datePicker.show(getSupportFragmentManager(), "DATE_PICKER_SINTOMO");
            });
        }
    }

}
