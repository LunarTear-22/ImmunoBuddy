package com.example.immunobubby;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private boolean isFabMenuOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fabMain = findViewById(R.id.btnFab);
        LinearLayout fabMenuLayout = findViewById(R.id.fabMenuLayout);

        ExtendedFloatingActionButton fab1 = findViewById(R.id.btnReazioni);
        ExtendedFloatingActionButton fab2 = findViewById(R.id.btnPromemoria);
        ExtendedFloatingActionButton fab3 = findViewById(R.id.btnSintomi);

        // Toggle menu al click sul FAB principale
        fabMain.setOnClickListener(v -> {
            if (isFabMenuOpen) {
                fabMenuLayout.setVisibility(View.GONE);
                isFabMenuOpen = false;
            } else {
                fabMenuLayout.setVisibility(View.VISIBLE);
                isFabMenuOpen = true;
            }
        });

        // Azioni sui FAB secondari
        fab1.setOnClickListener(v -> {
            // TODO: Azione FAB1
        });

        fab2.setOnClickListener(v -> {
            // TODO: Azione FAB2
        });

        fab3.setOnClickListener(v -> {
            // TODO: Azione FAB3
        });
    }
}
