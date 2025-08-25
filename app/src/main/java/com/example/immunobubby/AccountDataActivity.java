package com.example.immunobubby;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.immunobubby.R;
import com.google.android.material.button.MaterialButton;

public class AccountDataActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_data);

        // Trova la view dal layout
        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.dropdownGender);

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

    }
}