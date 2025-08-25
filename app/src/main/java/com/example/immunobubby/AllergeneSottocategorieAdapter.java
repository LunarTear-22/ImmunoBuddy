package com.example.immunobubby;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AllergeneSottocategorieAdapter extends RecyclerView.Adapter<AllergeneSottocategorieAdapter.SottocategoriaViewHolder> {

    private final Context context;
    private final List<Allergene> allergeni;

    public AllergeneSottocategorieAdapter(Context context, List<Allergene> allergeni) {
        this.context = context;
        this.allergeni = allergeni;
    }

    @NonNull
    @Override
    public SottocategoriaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sottocategoria_allergene, parent, false);
        return new SottocategoriaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SottocategoriaViewHolder holder, int position) {
        Allergene allergene = allergeni.get(position);
        holder.txtNome.setText(allergene.getNome());

        if (allergene.hasSottocategorie()) {
            holder.recyclerSottocategorie.setVisibility(View.VISIBLE);
            AllergeneSottocategorieAdapter subAdapter = new AllergeneSottocategorieAdapter(context, allergene.getSottocategorie());
            holder.recyclerSottocategorie.setLayoutManager(new LinearLayoutManager(context));
            holder.recyclerSottocategorie.setAdapter(subAdapter);
        } else {
            holder.recyclerSottocategorie.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return allergeni.size();
    }

    static class SottocategoriaViewHolder extends RecyclerView.ViewHolder {
        TextView txtNome;
        RecyclerView recyclerSottocategorie;

        public SottocategoriaViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNome = itemView.findViewById(R.id.txtNomeSottocategoria);
            recyclerSottocategorie = itemView.findViewById(R.id.recyclerSottocategorie);
        }
    }
}
