package com.example.immunobubby;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class SintomiActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private SintomiAdapter adapter;
    private List<Sintomi> sintomiList;
    private List<Sintomi> backupList;
    private Button btnAggiorna, btnAnnulla, btnSalva;
    private FloatingActionButton BtnAddSintomo;
    private ImageButton icGravitaorder;
    private LinearLayout frequenzaOrder;
    private ImageButton icFrequenzaorder;
    private TextView tvSortFrequenza;

    private boolean gravitaAsc = true;
    private boolean frequenzaAsc = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sintomi);

        icGravitaorder = findViewById(R.id.header_gravita);
        frequenzaOrder = findViewById(R.id.header_Frequenza_container);

        tvSortFrequenza = findViewById(R.id.header_Frequenza);
        icFrequenzaorder = findViewById(R.id.header_Frequenza_icon);

        initViews();
        setupRecyclerView();
        loadSintomi();
        setupClickListeners();
    }

    /** Inizializza le view */
    private void initViews() {
        recyclerView = findViewById(R.id.sintomi_recycler);

        btnAggiorna = findViewById(R.id.btnAggiorna);
        btnAnnulla = findViewById(R.id.btnAnnulla);
        btnSalva = findViewById(R.id.btnSalva);
        BtnAddSintomo = findViewById(R.id.btnAddSintomo);
    }

    /** Imposta il RecyclerView con adapter e layout manager */
    private void setupRecyclerView() {
        sintomiList = new ArrayList<>();
        adapter = new SintomiAdapter(this, sintomiList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadSintomi() {
        sintomiList.clear();
        sintomiList.addAll(SintomiStorage.loadSintomi(this));
        adapter.notifyDataSetChanged();
    }

    /** Ordina per gravità */
    private void sortByGravita() {
        // ruoto l'icona in base al valore attuale
        rotateIcon(icGravitaorder, gravitaAsc);

        java.util.Map<String, Integer> gravitaMap = new java.util.HashMap<>();
        gravitaMap.put("Lieve", 1);
        gravitaMap.put("Moderato", 2);
        gravitaMap.put("Significativo", 3);
        gravitaMap.put("Grave", 4);

        sintomiList.sort((r1, r2) -> {
            int g1 = gravitaMap.getOrDefault(r1.getGravita(), 0);
            int g2 = gravitaMap.getOrDefault(r2.getGravita(), 0);
            int cmp = Integer.compare(g1, g2);
            return gravitaAsc ? cmp : -cmp;
        });

        // inverti il flag per il prossimo click
        gravitaAsc = !gravitaAsc;

        // aggiorna la lista
        adapter.notifyDataSetChanged();
    }

    /** Ordina per frequenza */
    private void sortByFrequenza() {
        rotateIcon(icFrequenzaorder, frequenzaAsc);
        java.util.Map<String, Integer> frequenzaMap = new java.util.HashMap<>();
        frequenzaMap.put("Molto Raro", 1);
        frequenzaMap.put("Raro", 2);
        frequenzaMap.put("Frequente", 3);
        frequenzaMap.put("Molto Frequente", 4);

        sintomiList.sort( (s1, s2) -> {
            int f1 = frequenzaMap.getOrDefault(s1.getFrequenza(), 0);
            int f2 = frequenzaMap.getOrDefault(s2.getFrequenza(), 0);
            int cmp = Integer.compare(f1, f2);
            return frequenzaAsc ? cmp : -cmp;});
        frequenzaAsc = !frequenzaAsc;
        adapter.notifyDataSetChanged();
    }

    // ANIMAZIONI ROTAZIONE
    private void rotateIcon(View view, boolean ascending) {
        float toDegree = ascending ? 180f : 0f;
        view.animate()
                .rotation(toDegree)
                .setDuration(300)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    /** Imposta i click listener dei bottoni */
    private void setupClickListeners() {
        btnAggiorna.setOnClickListener(v -> {
            backupList = new ArrayList<>();
            for (Sintomi s : sintomiList) {
                backupList.add(new Sintomi(s.getNome(), s.getFrequenza(), s.getGravita()));
            }

            adapter.setEditingMode(true);
            btnAggiorna.setVisibility(Button.GONE);
            btnAnnulla.setVisibility(Button.VISIBLE);
            btnSalva.setVisibility(Button.VISIBLE);
        });

        btnAnnulla.setOnClickListener(v -> {
            if (backupList != null) {
                sintomiList.clear();
                sintomiList.addAll(backupList);
                adapter.notifyDataSetChanged();
            }
            adapter.setEditingMode(false);
            btnAggiorna.setVisibility(Button.VISIBLE);
            btnAnnulla.setVisibility(Button.GONE);
            btnSalva.setVisibility(Button.GONE);
            Toast.makeText(this, "Modifiche annullate", Toast.LENGTH_SHORT).show();
        });

        btnSalva.setOnClickListener(v -> {
            adapter.setEditingMode(false);
            btnAggiorna.setVisibility(Button.VISIBLE);
            btnAnnulla.setVisibility(Button.GONE);
            btnSalva.setVisibility(Button.GONE);

            Toast.makeText(this, "Frequenze aggiornate", Toast.LENGTH_SHORT).show();
            SintomiStorage.saveSintomi(this,sintomiList);
        });

        BtnAddSintomo.setOnClickListener(v -> {
            Intent intent = new Intent(SintomiActivity.this, NuovoSintomoActivity.class);
            startActivity(intent);
        });

        // Ordinamenti
        icGravitaorder.setOnClickListener(v -> sortByGravita());

        frequenzaOrder.setOnClickListener(v -> sortByFrequenza());
        icFrequenzaorder.setOnClickListener(v -> sortByFrequenza());
        tvSortFrequenza.setOnClickListener(v -> sortByFrequenza());
    }
}
