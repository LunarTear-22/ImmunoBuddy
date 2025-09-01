package com.example.immunobubby;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.immunobubby.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class AccountDataActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_data);

        // Trova la view dal layout
        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.dropdown);

        // Prende l'array di opzioni definito in strings.xml
        String[] items = getResources().getStringArray(R.array.gender_options);

        // Crea l'adapter con il layout personalizzato
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.dropdown_item,   // tuo layout per la tendina
                getResources().getStringArray(R.array.gender_options) // definito in strings.xml
        );

        // Collega l'adapter al campo
        autoCompleteTextView.setAdapter(adapter);

        TextInputEditText inputDob = findViewById(R.id.input_dob);

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


    }
}