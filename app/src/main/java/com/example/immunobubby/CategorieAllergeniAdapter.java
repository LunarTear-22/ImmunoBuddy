package com.example.immunobubby;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class CategorieAllergeniAdapter extends RecyclerView.Adapter<CategorieAllergeniAdapter.AllergeneViewHolder> {

    private final Context context;
    private final List<CategoriaAllergene> categorieList;

    public CategorieAllergeniAdapter(Context context, List<CategoriaAllergene> categorieList) {
        this.context = context;
        this.categorieList = categorieList;
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

        // Crea lista di allergeni come oggetti Allergene
        List<Allergene> allergeni = categoria.getAllergeni();

        // Imposta l'adapter per le sottocategorie
        AllergeneSottocategorieAdapter adapter = new AllergeneSottocategorieAdapter(context, allergeni);
        holder.recyclerSottocategorie.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerSottocategorie.setAdapter(adapter);

        // Espansione/collasso
        holder.cardAllergene.setOnClickListener(v -> {
            if (holder.recyclerSottocategorie.getVisibility() == View.GONE) {
                holder.recyclerSottocategorie.setVisibility(View.VISIBLE);
                holder.btnChevron.animate().rotation(180f).setDuration(200).start();
            } else {
                holder.recyclerSottocategorie.setVisibility(View.GONE);
                holder.btnChevron.animate().rotation(0f).setDuration(200).start();
            }
        });
    }

    @Override
    public int getItemCount() {
        return categorieList.size();
    }

    static class AllergeneViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardAllergene;
        TextView txtNomeAllergene;
        ImageView btnChevron;
        RecyclerView recyclerSottocategorie;

        public AllergeneViewHolder(@NonNull View itemView) {
            super(itemView);
            cardAllergene = itemView.findViewById(R.id.cardAllergene);
            txtNomeAllergene = itemView.findViewById(R.id.txtCategoriaAllergene);
            btnChevron = itemView.findViewById(R.id.btnChevron);
            recyclerSottocategorie = itemView.findViewById(R.id.recyclerSottocategorie);
        }
    }
}
