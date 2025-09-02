package com.example.immunobubby;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
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
    private MaterialCardView reactionDetailCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reazioni_allergiche);
        reactionDetailCard = findViewById(R.id.reaction_detail_card);

        initViews();
        setupRecyclerView();
        loadReactions();
        sortByDateDesc(); // ordinamento di default

        TextView tvSortByDate = findViewById(R.id.header_data);
        ImageButton icSortByGravita = findViewById(R.id.header_gravita);
        ImageButton icSortDate = findViewById(R.id.header_data_icon);
        LinearLayout headerData = findViewById(R.id.header_data_container);

        // CLICK PER ORDINAMENTO DATA
        tvSortByDate.setOnClickListener(v -> {
            sortByDate(isDateAscending);
            adapter.notifyDataSetChanged();
            rotateIcon(icSortDate, isDateAscending);
            isDateAscending = !isDateAscending;
        });
        icSortDate.setOnClickListener(v -> {
            sortByDate(isDateAscending);
            adapter.notifyDataSetChanged();
            rotateIcon(icSortDate, isDateAscending);
            isDateAscending = !isDateAscending;
        });
        headerData.setOnClickListener(v -> {
            sortByDate(isDateAscending);
            adapter.notifyDataSetChanged();
            rotateIcon(icSortDate, isDateAscending);
            isDateAscending = !isDateAscending;
        });

        // CLICK PER ORDINAMENTO GRAVITÀ
        icSortByGravita.setOnClickListener(v -> {
            sortByGravita(isGravitaAscending);
            adapter.notifyDataSetChanged();

            // Animazione rotazione ImageButton
            rotateIcon(icSortByGravita, isGravitaAscending);

            isGravitaAscending = !isGravitaAscending;
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
        adapter.setOnReactionClickListener(this::onReactionNameClick);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadReactions() {
        reactionsList.clear();
        reactionsList.addAll(ReazioneStorage.loadReactions(this));
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

    private void onReactionNameClick(Reazione reaction) {
        if (reaction == null) return;

        // Mostra la card
        reactionDetailCard.setVisibility(View.VISIBLE);

        // Popola i campi della card
        TextView tvDetailNome = reactionDetailCard.findViewById(R.id.detail_title);
        TextView tvDetailData = reactionDetailCard.findViewById(R.id.detail_data);
        TextView tvDetailOra = reactionDetailCard.findViewById(R.id.detail_ora);
        TextView tvDetailAllergene = reactionDetailCard.findViewById(R.id.detail_allergeni);
        TextView tvDetailSintomi = reactionDetailCard.findViewById(R.id.detail_sintomi);
        TextView tvDetailFarmaci = reactionDetailCard.findViewById(R.id.detail_farmaci);
        TextView tvDetailMedico = reactionDetailCard.findViewById(R.id.detail_medico);
        TextView tvDetailNote = reactionDetailCard.findViewById(R.id.detail_note);
        ImageView ivDetailFoto = reactionDetailCard.findViewById(R.id.detail_photo);

        tvDetailNome.setText(generateReactionName(reaction));

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        tvDetailData.setText(reaction.getData() != null ? sdf.format(reaction.getData()) : "Non specificato");
        tvDetailOra.setText(reaction.getOra() != null ? reaction.getOra() : "Non specificato");
        tvDetailAllergene.setText(reaction.getAllergene() != null ? reaction.getAllergene() : "Non specificato");
        tvDetailSintomi.setText(reaction.getSintomi() != null && !reaction.getSintomi().isEmpty()
                ? String.join(", ", reaction.getSintomi())
                : "Nessuno");
        tvDetailFarmaci.setText(reaction.getFarmaci() != null && !reaction.getFarmaci().isEmpty()
                ? String.join(", ", reaction.getFarmaci())
                : "Nessuno");
        tvDetailMedico.setText(reaction.getContattoMedico() != null
                ? (reaction.getContattoMedico() ? "Sì" : "No")
                : "Non specificato");
        tvDetailNote.setText(reaction.getNote() != null ? reaction.getNote() : "Assenti");

        if (reaction.getFoto() != null && !reaction.getFoto().isEmpty()) {
            File imgFile = new File(reaction.getFoto().get(0));
            if (imgFile.exists()) {
                ivDetailFoto.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
                ivDetailFoto.setVisibility(View.VISIBLE);
            } else {
                ivDetailFoto.setVisibility(View.GONE);
            }
        } else {
            ivDetailFoto.setVisibility(View.GONE);
        }

        // Bottone chiudi
        ImageButton closeButton = reactionDetailCard.findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> reactionDetailCard.setVisibility(View.GONE));
    }

    private String generateReactionName(Reazione reaction) {
        if (reaction == null) return "Reazione allergica";
        String allergen = reaction.getAllergene();
        List<String> symptoms = reaction.getSintomi();
        if (allergen != null && !allergen.isEmpty()) {
            return (symptoms != null && !symptoms.isEmpty())
                    ? symptoms.get(0) + " da " + allergen
                    : "Reazione a " + allergen;
        } else if (symptoms != null && !symptoms.isEmpty()) {
            return symptoms.get(0);
        } else {
            return "Reazione allergica";
        }
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
