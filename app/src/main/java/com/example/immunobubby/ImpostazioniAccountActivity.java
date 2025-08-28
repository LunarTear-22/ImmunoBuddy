package com.example.immunobubby;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.immunobubby.R;
import com.google.android.material.button.MaterialButton;

public class ImpostazioniAccountActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.impostazioni_account);

        MaterialButton btnAccount = findViewById(R.id.btnAccount);
        if (btnAccount != null) {
            btnAccount.setOnClickListener(v -> {
                startActivity(new Intent(this, AccountDataActivity.class));
                overridePendingTransition(0, 0);
            });
        }

        MaterialButton btnSettings = findViewById(R.id.btnSettings);
        if (btnSettings != null) {
            btnSettings.setOnClickListener(v -> {
                startActivity(new Intent(this, InterfacciaActivity.class));
                overridePendingTransition(0, 0);
            });
        }
    }
}

