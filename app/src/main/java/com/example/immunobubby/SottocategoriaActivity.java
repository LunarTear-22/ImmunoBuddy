package com.example.immunobubby;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class SottocategoriaActivity extends BaseActivity {

    private TextView txtNomeSottocategoria;
    private ImageView btnChevron;
    private RecyclerView recyclerSottocategorie;
    private MaterialCardView cardSottocategoriaAllergene;

    private boolean expanded = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sottocategoria);

        FloatingActionButton btnCheck = findViewById(R.id.btnCeck);

        btnCheck.setEnabled(false);
        btnCheck.setAlpha(0.5f); // opzionale: per indicare visivamente che è disabilitato


        txtNomeSottocategoria = findViewById(R.id.txtNomeSottocategoria);
        btnChevron = findViewById(R.id.btnChevron);
        recyclerSottocategorie = findViewById(R.id.recyclerSottocategorie);
        cardSottocategoriaAllergene = findViewById(R.id.cardSottocategoriaAllergene);

        // Recupera dati dall'Intent
        String nomeSottocategoria = getIntent().getStringExtra("categoria_nome");
        ArrayList<String> allergeni = getIntent().getStringArrayListExtra("sottocategorie");

        txtNomeSottocategoria.setText(nomeSottocategoria);

        // Imposta RecyclerView
        recyclerSottocategorie.setLayoutManager(new LinearLayoutManager(this));
        AllergeniSpecificiAdapter adapter = new AllergeniSpecificiAdapter(this, allergeni);
        recyclerSottocategorie.setAdapter(adapter);

        // Click sulla card per espandere/chiudere
        cardSottocategoriaAllergene.setOnClickListener(v -> toggleExpansion());
    }

    private void toggleExpansion() {
        expanded = !expanded;

        // Animazione apertura/chiusura
        TransitionManager.beginDelayedTransition(cardSottocategoriaAllergene, new AutoTransition());
        recyclerSottocategorie.setVisibility(expanded ? View.VISIBLE : View.GONE);
        rotateChevron(btnChevron, expanded ? 0f : 180f, expanded ? 180f : 0f);
    }

    private void rotateChevron(ImageView chevron, float from, float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(chevron, "rotation", from, to);
        animator.setDuration(300);
        animator.start();
    }
}
