package com.example.immunobubby;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CategorieAllergeniAdapter extends RecyclerView.Adapter<CategorieAllergeniAdapter.AllergeneViewHolder> {

    private final Context context;
    private final List<CategoriaAllergene> categorieList;
    private final Set<Integer> expandedPositions = new HashSet<>();
    private RecyclerView parentRecyclerView; // RecyclerView principale per le transizioni

    public CategorieAllergeniAdapter(Context context, List<CategoriaAllergene> categorieList, RecyclerView recyclerView) {
        this.context = context;
        this.categorieList = categorieList;
        this.parentRecyclerView = recyclerView;
    }

    @NonNull
    @Override
    public AllergeneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_categoria_allergene, parent, false);
        return new AllergeneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllergeneViewHolder holder, int position) {
        CategoriaAllergene categoria = categorieList.get(position);
        holder.txtNomeAllergene.setText(categoria.getNome());

        // Adapter per sottocategorie
        AllergeneSottocategorieAdapter allergeniAdapter = new AllergeneSottocategorieAdapter(context, categoria.getAllergeni());
        holder.recyclerAllergeni.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerAllergeni.setAdapter(allergeniAdapter);

        // Imposta visibilità iniziale
        holder.recyclerAllergeni.setVisibility(expandedPositions.contains(position) ? View.VISIBLE : View.GONE);
        holder.btnChevron.setRotation(expandedPositions.contains(position) ? 180f : 0f);

        // Click listener sulla card intera
        holder.cardAllergene.setOnClickListener(v -> toggleExpansion(holder, position));
    }

    private void toggleExpansion(AllergeneViewHolder holder, int position) {
        // Animazione per tutte le card
        TransitionManager.beginDelayedTransition(parentRecyclerView, new AutoTransition());

        boolean expanded = expandedPositions.contains(position);
        if (expanded) {
            expandedPositions.remove(position);
            holder.recyclerAllergeni.setVisibility(View.GONE);
            rotateChevron(holder.btnChevron, 180f, 0f);
        } else {
            expandedPositions.add(position);
            holder.recyclerAllergeni.setVisibility(View.VISIBLE);
            rotateChevron(holder.btnChevron, 0f, 180f);
        }
    }

    private void rotateChevron(ImageView chevron, float from, float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(chevron, "rotation", from, to);
        animator.setDuration(300); // durata animazione
        animator.start();
    }

    @Override
    public int getItemCount() {
        return categorieList.size();
    }

    static class AllergeneViewHolder extends RecyclerView.ViewHolder {
        public RecyclerView recyclerAllergeni;
        MaterialCardView cardAllergene;
        TextView txtNomeAllergene;
        ImageView btnChevron;

        public AllergeneViewHolder(@NonNull View itemView) {
            super(itemView);
            cardAllergene = itemView.findViewById(R.id.cardAllergene);
            txtNomeAllergene = itemView.findViewById(R.id.txtCategoriaAllergene);
            btnChevron = itemView.findViewById(R.id.btnChevron);
            recyclerAllergeni = itemView.findViewById(R.id.recyclerSottocategorie);
        }
    }
}
