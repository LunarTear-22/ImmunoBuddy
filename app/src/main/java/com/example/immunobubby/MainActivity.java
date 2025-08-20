package com.example.immunobubby;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private FabMenu fabMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fabMain = findViewById(R.id.btnFab);
        FloatingActionButton fab1 = findViewById(R.id.btnReazioni);
        FloatingActionButton fab2 = findViewById(R.id.btnPromemoria);
        FloatingActionButton fab3 = findViewById(R.id.btnSintomi);

        fabMenu = new FabMenu(fabMain, fab1, fab2, fab3);

        // esempio: azioni sui bottoni
        fab1.setOnClickListener(v -> {
            // Azione FAB1
        });

        fab2.setOnClickListener(v -> {
            // Azione FAB2
        });

        fab3.setOnClickListener(v -> {
            // Azione FAB3
        });
    }
}
