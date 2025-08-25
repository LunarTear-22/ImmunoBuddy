package com.example.immunobubby;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AllergeniActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allergeni);

        // Trova il FAB e aggiungi il click listener
        FloatingActionButton fabAdd = findViewById(R.id.btnAdd);
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(AllergeniActivity.this, CategorieAllergeniActivity.class);
            startActivity(intent);
        });
    }
}
