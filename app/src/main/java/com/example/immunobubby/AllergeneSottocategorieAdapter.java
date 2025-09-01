package com.example.immunobubby;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AllergeneSottocategorieAdapter extends RecyclerView.Adapter<AllergeneSottocategorieAdapter.AllergeneViewHolder> {

    private static final String TAG = "SottocategorieAdapter";
    private final Context context;
    private final ArrayList<Allergene> allergeni;
    private final String categoriaPadre; // Categoria della categoria padre

    public AllergeneSottocategorieAdapter(Context context, ArrayList<Allergene> allergeni, String categoriaPadre) {
        this.context = context;
        this.allergeni = allergeni;
        this.categoriaPadre = categoriaPadre;
        Log.d(TAG, "Adapter creato con " + allergeni.size() + " allergeni, categoria padre: " + categoriaPadre);
    }

    @NonNull
    @Override
    public AllergeneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sottocategoria_allergene, parent, false);
        Log.d(TAG, "onCreateViewHolder chiamato");
        return new AllergeneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllergeneViewHolder holder, int position) {
        Allergene allergene = allergeni.get(position);
        holder.txtNome.setText(allergene.getNome());
        Log.d(TAG, "Bind allergene: " + allergene.getNome() + " alla posizione: " + position);

        if (allergene.hasSottocategorie()) {
            holder.btnChevron.setVisibility(View.VISIBLE);
        } else {
            holder.btnChevron.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (allergene.hasSottocategorie()) {
                Intent intent = new Intent(context, SottocategoriaActivity.class);
                intent.putExtra("categoria_nome", allergene.getNome());
                intent.putExtra("categoria_padre", categoriaPadre);

                ArrayList<String> sottocategorieNomi = new ArrayList<>();
                for (Allergene sub : allergene.getSottocategorie()) {
                    sottocategorieNomi.add(sub.getNome());
                    // Imposta la categoria della sottocategoria
                    sub.setCategoria(categoriaPadre);
                }
                intent.putStringArrayListExtra("sottocategorie", sottocategorieNomi);

                Log.d(TAG, "Click sull'allergene: " + allergene.getNome() +
                        ", aperta SottocategoriaActivity con " + sottocategorieNomi.size() + " sottocategorie");
                context.startActivity(intent);
            } else {
                Intent intent = new Intent(context, SalvaAllergeneActivity.class);
                intent.putExtra("allergene_nome", allergene.getNome());
                intent.putExtra("categoria_nome", categoriaPadre);
                Log.d(TAG, "Click sull'allergene: " + allergene.getNome() +
                        ", aperta SalvaAllergeneActivity con categoria: " + categoriaPadre);
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
