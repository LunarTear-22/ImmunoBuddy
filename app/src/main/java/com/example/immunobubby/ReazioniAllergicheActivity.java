package com.example.immunobubby;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ReazioniAllergicheActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private ReazioneAdapter adapter;
    private List<Reazione> reactionsList;
    private FloatingActionButton fabAddReaction;
    private boolean isDateAscending = true;
    private boolean isGravitaAscending = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reazioni_allergiche);

        initViews();
        setupRecyclerView();
        loadReactions();
        sortByDateDesc(); // ordinamento di default

        TextView tvSortByDate = findViewById(R.id.header_data);
        TextView tvSortByGravita = findViewById(R.id.header_gravita);

        tvSortByDate.setOnClickListener(v -> {
            sortByDate(isDateAscending);
            isDateAscending = !isDateAscending; // toggle
            adapter.notifyDataSetChanged();
        });

        tvSortByGravita.setOnClickListener(v -> {
            sortByGravita(isGravitaAscending);
            isGravitaAscending = !isGravitaAscending; // toggle
            adapter.notifyDataSetChanged();
        });

        setupClickListeners();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.reactions_recycler);
        fabAddReaction = findViewById(R.id.btnAddReaction);
    }

    private void setupRecyclerView() {
        reactionsList = new ArrayList<>();
        adapter = new ReazioneAdapter(this, reactionsList);
        adapter.setOnReactionClickListener(this::onReactionClick);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadReactions() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        reactionsList.clear();

        try {
            Reazione reaction1 = new Reazione();
            reaction1.setData(dateFormat.parse("17/08/2025"));
            reaction1.setAllergene("pistacchi");
            reaction1.setGravita("Moderato"); // cambiato da "Media"
            reaction1.addSintomo("Gonfiore");
            reactionsList.add(reaction1);

            Reazione reaction2 = new Reazione();
            reaction2.setData(dateFormat.parse("05/04/2025"));
            reaction2.setAllergene("pollini");
            reaction2.setGravita("Lieve");
            reaction2.addSintomo("Rinite");
            reactionsList.add(reaction2);

            Reazione reaction3 = new Reazione();
            reaction3.setData(dateFormat.parse("18/04/2025"));
            reaction3.setAllergene("acari");
            reaction3.setGravita("Grave");
            reaction3.addSintomo("Asma");
            reactionsList.add(reaction3);

            Reazione reaction4 = new Reazione();
            reaction4.setData(dateFormat.parse("27/04/2025"));
            reaction4.setAllergene("allergeni vari");
            reaction4.setGravita("Lieve");
            reaction4.addSintomo("Congiuntivite");
            reactionsList.add(reaction4);

            Reazione reaction5 = new Reazione();
            reaction5.setData(dateFormat.parse("06/05/2025"));
            reaction5.setAllergene("frutta secca");
            reaction5.setGravita("Moderato"); // cambiato da "Media"
            reaction5.addSintomo("Prurito orale");
            reactionsList.add(reaction5);

            Reazione reaction6 = new Reazione();
            reaction6.setData(dateFormat.parse("20/05/2025"));
            reaction6.setAllergene("antibiotico");
            reaction6.setGravita("Grave");
            reaction6.addSintomo("Anafilassi");
            reactionsList.add(reaction6);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        adapter.notifyDataSetChanged();
    }

    // ORDINAMENTO PER DATA
    private void sortByDate(boolean ascending) {
        reactionsList.sort((r1, r2) -> {
            if (r1.getData() == null || r2.getData() == null) return 0;
            int cmp = r1.getData().compareTo(r2.getData());
            return ascending ? cmp : -cmp;
        });
    }

    private void sortByDateDesc() {
        Collections.sort(reactionsList, (r1, r2) -> r2.getData().compareTo(r1.getData()));
    }

    private void sortByDateAsc() {
        Collections.sort(reactionsList, Comparator.comparing(Reazione::getData));
    }

    // ORDINAMENTO PER GRAVITÀ
    private void sortByGravita(boolean ascending) {
        java.util.Map<String, Integer> gravitaMap = new java.util.HashMap<>();
        gravitaMap.put("Lieve", 1);
        gravitaMap.put("Moderato", 2);
        gravitaMap.put("Significativo", 3);
        gravitaMap.put("Grave", 4);

        reactionsList.sort((r1, r2) -> {
            int g1 = gravitaMap.getOrDefault(r1.getGravita(), 0);
            int g2 = gravitaMap.getOrDefault(r2.getGravita(), 0);
            int cmp = Integer.compare(g1, g2);
            return ascending ? cmp : -cmp;
        });
    }

    private void setupClickListeners() {
        fabAddReaction.setOnClickListener(v -> {
            Intent intent = new Intent(this, NuovaReazioneActivity.class);
            startActivityForResult(intent, 1001);
        });
    }

    private void onReactionClick(Reazione reaction) {
        Intent intent = new Intent(this, NuovaReazioneActivity.class);
        intent.putExtra("edit_mode", true);
        intent.putExtra("reaction_data", reaction);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            loadReactions();
            sortByDateDesc();
            adapter.notifyDataSetChanged();
        }
    }
}
