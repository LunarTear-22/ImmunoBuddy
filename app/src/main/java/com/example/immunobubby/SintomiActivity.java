package com.example.immunobubby;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SintomiActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private SintomiAdapter adapter;
    private List<Sintomi> sintomiList;
    private List<Sintomi> backupList;

    private Button btnAggiorna, btnAnnulla, btnSalva, BtnAddSintomo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sintomi);

        recyclerView = findViewById(R.id.sintomi_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Lista di esempio (usa String per frequenza e gravita come nel tuo Sintomi.java)
        sintomiList = new ArrayList<>();
        sintomiList.add(new Sintomi("Mal di testa", "Frequente", "Moderato"));
        sintomiList.add(new Sintomi("Nausea", "Raro", "Lieve"));
        sintomiList.add(new Sintomi("Febbre", "Molto raro", "Significativo"));

        adapter = new SintomiAdapter(this, sintomiList);
        recyclerView.setAdapter(adapter);

        btnAggiorna = findViewById(R.id.btnAggiorna);
        btnAnnulla = findViewById(R.id.btnAnnulla);
        btnSalva = findViewById(R.id.btnSalva);

        btnAggiorna.setOnClickListener(v -> {
            // Salvo una copia per poter annullare
            backupList = new ArrayList<>();
            for (Sintomi s : sintomiList) {
                backupList.add(new Sintomi(s.getNome(), s.getFrequenza(), s.getGravita()));
            }

            adapter.setEditingMode(true);
            btnAggiorna.setVisibility(View.GONE);
            btnAnnulla.setVisibility(View.VISIBLE);
            btnSalva.setVisibility(View.VISIBLE);
        });

        btnAnnulla.setOnClickListener(v -> {
            // Ripristina la copia
            if (backupList != null) {
                sintomiList.clear();
                for (Sintomi b : backupList) {
                    sintomiList.add(new Sintomi(b.getNome(), b.getFrequenza(), b.getGravita()));
                }
                adapter.notifyDataSetChanged();
            }
            adapter.setEditingMode(false);
            btnAggiorna.setVisibility(View.VISIBLE);
            btnAnnulla.setVisibility(View.GONE);
            btnSalva.setVisibility(View.GONE);
            Toast.makeText(this, "Modifiche annullate", Toast.LENGTH_SHORT).show();
        });

        btnSalva.setOnClickListener(v -> {
            // Qui salva realmente (DB, SharedPreferences o chiamata a backend)
            adapter.setEditingMode(false);
            btnAggiorna.setVisibility(View.VISIBLE);
            btnAnnulla.setVisibility(View.GONE);
            btnSalva.setVisibility(View.GONE);

            // Esempio: conferma con Toast
            Toast.makeText(this, "Frequenze aggiornate", Toast.LENGTH_SHORT).show();

            // TODO: salva sintomiList in persistenza se necessario
        });

        /* BtnAddSintomo = findViewById(R.id.btnAddSintomo);

        BtnAddSintomo.setOnClickListener(v -> {
            Intent intent = new Intent(SintomiActivity.this, NuovoSintomoActivity.class);
            startActivity(intent);
        }); */

    }
}
