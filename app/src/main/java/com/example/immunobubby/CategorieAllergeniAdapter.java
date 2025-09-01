package com.example.immunobubby;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CategorieAllergeniAdapter extends RecyclerView.Adapter<CategorieAllergeniAdapter.AllergeneViewHolder> {

    private static final String TAG = "CategorieAdapter";

    private final Context context;
    private final List<CategoriaAllergene> originalList; // lista completa
    private List<CategoriaAllergene> filteredList;       // lista filtrata
    private final Set<Integer> expandedPositions = new HashSet<>();
    private RecyclerView parentRecyclerView;

    public CategorieAllergeniAdapter(Context context, List<CategoriaAllergene> categorieList, RecyclerView recyclerView) {
        this.context = context;
        this.parentRecyclerView = recyclerView;
        this.originalList = categorieList;
        this.filteredList = new ArrayList<>(categorieList);
        Log.d(TAG, "Adapter creato con " + categorieList.size() + " categorie");
    }

    @NonNull
    @Override
    public AllergeneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder chiamato");
        View view = LayoutInflater.from(context).inflate(R.layout.item_categoria_allergene, parent, false);
        return new AllergeneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllergeneViewHolder holder, int position) {
        CategoriaAllergene categoria = filteredList.get(position);
        holder.txtNomeAllergene.setText(categoria.getNome());
        Log.d(TAG, "Bind categoria: " + categoria.getNome() + " alla posizione: " + position);

        AllergeneSottocategorieAdapter allergeniAdapter =
                new AllergeneSottocategorieAdapter(
                        context,
                        new ArrayList<>(categoria.getAllergeni()), // converti in ArrayList
                        categoria.getNome()                       // categoriaPadre
                );
        holder.recyclerAllergeni.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerAllergeni.setAdapter(allergeniAdapter);

        holder.recyclerAllergeni.setVisibility(expandedPositions.contains(position) ? View.VISIBLE : View.GONE);
        holder.btnChevron.setRotation(expandedPositions.contains(position) ? 180f : 0f);

        holder.cardAllergene.setOnClickListener(v -> {
            Log.d(TAG, "Click sulla categoria: " + categoria.getNome());
            toggleExpansion(holder, position);
        });
    }

    private void toggleExpansion(AllergeneViewHolder holder, int position) {
        TransitionManager.beginDelayedTransition(parentRecyclerView, new AutoTransition());

        boolean expanded = expandedPositions.contains(position);
        if (expanded) {
            expandedPositions.remove(position);
            holder.recyclerAllergeni.setVisibility(View.GONE);
            rotateChevron(holder.btnChevron, 180f, 0f);
            Log.d(TAG, "Categoria chiusa alla posizione: " + position);
        } else {
            expandedPositions.add(position);
            holder.recyclerAllergeni.setVisibility(View.VISIBLE);
            rotateChevron(holder.btnChevron, 0f, 180f);
            Log.d(TAG, "Categoria aperta alla posizione: " + position);
        }
    }

    private void rotateChevron(ImageView chevron, float from, float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(chevron, "rotation", from, to);
        animator.setDuration(300);
        animator.start();
        Log.d(TAG, "Chevron ruotato da " + from + " a " + to);
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    // --- Metodo per filtrare allergeni e sottocategorie ---
    public void filter(String query) {
        Log.d(TAG, "Filter chiamato con query: " + query);
        query = query.toLowerCase();
        filteredList.clear();

        for (CategoriaAllergene categoria : originalList) {
            List<Allergene> matchedAllergeni = new ArrayList<>();
            for (Allergene allergene : categoria.getAllergeni()) {
                if (allergene.getNome().toLowerCase().contains(query)) {
                    matchedAllergeni.add(allergene);
                    Log.d(TAG, "Allergene principale match: " + allergene.getNome());
                } else if (allergene.getSottocategorie() != null) {
                    List<Allergene> matchedSub = new ArrayList<>();
                    for (Allergene sub : allergene.getSottocategorie()) {
                        if (sub.getNome().toLowerCase().contains(query)) {
                            matchedSub.add(sub);
                            Log.d(TAG, "Sottocategoria match: " + sub.getNome());
                        }
                    }
                    if (!matchedSub.isEmpty()) {
                        matchedAllergeni.add(new Allergene(allergene.getNome(), matchedSub));
                    }
                }
            }
            if (!matchedAllergeni.isEmpty()) {
                filteredList.add(new CategoriaAllergene(categoria.getNome(), matchedAllergeni));
            }
        }

        Log.d(TAG, "Filtraggio completato, " + filteredList.size() + " categorie risultanti");
        notifyDataSetChanged();
    }

    // --- ViewHolder ---
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
