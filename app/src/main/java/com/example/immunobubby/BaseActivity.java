package com.example.immunobubby;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(getResources().getColor(R.color.background_light));
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.background_light));
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        // Configura navbar e back button automaticamente
        setupNavbarButtons();
        setupBackButton(R.id.btnBack);
        // Cambia colore ai divider delle tendine
        setupDropdownDividerColor();


    }

    private void setupNavbarButtons() {
        MaterialButton btnHome = findViewById(R.id.nav_home);
        if (btnHome != null) {
            btnHome.setOnClickListener(v -> {
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(0, 0);
            });
        }

        MaterialButton btnAccount = findViewById(R.id.btnAccountNav);
        if (btnAccount != null) {
            btnAccount.setOnClickListener(v -> {
                startActivity(new Intent(this, ImpostazioniAccountActivity.class));
                overridePendingTransition(0, 0);
            });


        }

        MaterialButton btnAllergeni = findViewById(R.id.btnAllergeni);
        if (btnAllergeni != null) {
            btnAllergeni.setOnClickListener(v -> {
                startActivity(new Intent(this, AllergeniActivity.class));
                overridePendingTransition(0, 0);
            });


        }
    }
        private void setupBackButton ( int backButtonId){
            View btnBack = findViewById(backButtonId);
            if (btnBack != null) {
                btnBack.setOnClickListener(v -> onBackPressed());
            }
        }

    /**
     * Forza il colore dei divider dei dropdown (AutoCompleteTextView).
     */
    private void setupDropdownDividerColor() {
        // Trova tutte le AutoCompleteTextView che potrebbero essere presenti
        MaterialAutoCompleteTextView dropdown = findViewById(R.id.dropdownGender);
        if (dropdown != null) {
            dropdown.setDropDownBackgroundDrawable(
                    getResources().getDrawable(R.drawable.bg_dropdown) // sfondo personalizzato senza divider
            );
        }
    }
}
