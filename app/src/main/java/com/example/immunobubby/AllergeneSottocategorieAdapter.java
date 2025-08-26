package com.example.immunobubby;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AllergeneSottocategorieAdapter extends RecyclerView.Adapter<AllergeneSottocategorieAdapter.AllergeneViewHolder> {

    private final Context context;
    private final List<Allergene> allergeni;


    public AllergeneSottocategorieAdapter(Context context, List<Allergene> allergeni) {
        this.context = context;
        this.allergeni = allergeni;
    }

    @NonNull
    @Override
    public AllergeneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sottocategoria_allergene, parent, false);
        return new AllergeneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllergeneViewHolder holder, int position) {
        Allergene allergene = allergeni.get(position);
        ImageView btnChevron = holder.itemView.findViewById(R.id.btnChevron);

        holder.txtNome.setText(allergene.getNome());

        if (allergene.hasSottocategorie()) {
            btnChevron.setVisibility(View.VISIBLE);
        } else {
            btnChevron.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (allergene.hasSottocategorie()) {
                Intent intent = new Intent(context, SottocategoriaActivity.class);
                intent.putExtra("categoria_nome", allergene.getNome());

                // Lista dei nomi delle sottocategorie
                ArrayList<String> sottocategorieNomi = new ArrayList<>();
                for (Allergene a : allergene.getSottocategorie()) {
                    sottocategorieNomi.add(a.getNome());
                }
                intent.putStringArrayListExtra("sottocategorie", sottocategorieNomi);

                context.startActivity(intent);
            } else {
                // Caso allergene semplice: apri activity per salvare
                Intent intent = new Intent(context, SalvaAllergeneActivity.class);
                intent.putExtra("allergene_nome", allergene.getNome());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return allergeni.size();
    }

    static class AllergeneViewHolder extends RecyclerView.ViewHolder {
        TextView txtNome;
        ImageView btnChevron;
        public AllergeneViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNome = itemView.findViewById(R.id.txtNomeAllergene);
            btnChevron = itemView.findViewById(R.id.btnChevron);

        }
    }
}
