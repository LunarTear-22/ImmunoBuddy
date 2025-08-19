package com.example.immunobubby;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fabMenu;
    private LinearLayout fabMenuLayout;
    private boolean isMenuOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fabMenu = findViewById(R.id.fabMenu);
        fabMenuLayout = findViewById(R.id.fabMenuLayout);

        fabMenu.setOnClickListener(v -> toggleFabMenu());

        findViewById(R.id.btnReazioni).setOnClickListener(v ->
                Toast.makeText(this, "Aggiungi Reazione", Toast.LENGTH_SHORT).show());

        findViewById(R.id.btnPromemoria).setOnClickListener(v ->
                Toast.makeText(this, "Aggiungi Promemoria Farmaci", Toast.LENGTH_SHORT).show());

        findViewById(R.id.btnSintomi).setOnClickListener(v ->
                Toast.makeText(this, "Aggiungi Sintomi", Toast.LENGTH_SHORT).show());
    }

    private void toggleFabMenu() {
        if (isMenuOpen) {
            fabMenuLayout.setVisibility(View.GONE);
        } else {
            fabMenuLayout.setVisibility(View.VISIBLE);
        }
        isMenuOpen = !isMenuOpen;
    }
}
