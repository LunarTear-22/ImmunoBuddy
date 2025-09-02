package com.example.immunobubby;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class SintomiActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private SintomiAdapter adapter;
    private List<Sintomi> sintomiList;
    private List<Sintomi> backupList;
    private Button btnAggiorna, btnAnnulla, btnSalva;
    private FloatingActionButton BtnAddSintomo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sintomi);

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

    /** Carica i dati di esempio */
    private void loadSintomi() {

        sintomiList.clear();

        Sintomi sintomo1 = new Sintomi("Mal di testa", "Frequente", "Moderato");
        sintomiList.add(sintomo1);

        Sintomi sintomo2 = new Sintomi("Nausea", "Raro", "Lieve");
        sintomiList.add(sintomo2);

        Sintomi sintomo3 = new Sintomi("Febbre", "Molto raro", "Significativo");
        sintomiList.add(sintomo3);

        adapter.notifyDataSetChanged();
    }

    /** Imposta i click listener dei bottoni */
    private void setupClickListeners() {
        btnAggiorna.setOnClickListener(v -> {
            // Salvo una copia per poter annullare
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
            // Ripristina la copia
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
            // TODO: salva sintomiList in persistenza se necessario
        });

        BtnAddSintomo.setOnClickListener(v -> {
            Intent intent = new Intent(SintomiActivity.this, NuovoSintomoActivity.class);
            startActivity(intent);
        });

    }

}
