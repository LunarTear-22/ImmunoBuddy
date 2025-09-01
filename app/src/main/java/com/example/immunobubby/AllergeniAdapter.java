package com.example.immunobubby;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AllergeniAdapter extends RecyclerView.Adapter<AllergeniAdapter.AllergeneViewHolder> {

    private static final String TAG = "AllergeniAdapter";

    private List<Allergene> allergeni;

    public AllergeniAdapter(List<Allergene> allergeni) {
        this.allergeni = allergeni;
        Log.d(TAG, "Adapter creato con " + allergeni.size() + " allergeni salvati");
    }

    @NonNull
    @Override
    public AllergeneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder chiamato");
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_allergeni_salavti, parent, false);
        return new AllergeneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllergeneViewHolder holder, int position) {
        Allergene allergene = allergeni.get(position);
        holder.nome.setText(allergene.getNome());
        holder.categoria.setText(allergene.getCategoria());
        Log.d(TAG, "Bind allergene: " + allergene.getNome() + ", categoria: " + allergene.getCategoria() + " alla posizione: " + position);
    }

    @Override
    public int getItemCount() {
        return allergeni.size();
    }

    static class AllergeneViewHolder extends RecyclerView.ViewHolder {
        TextView nome, categoria;

        AllergeneViewHolder(@NonNull View itemView) {
            super(itemView);
            nome = itemView.findViewById(R.id.txtNomeAllergeneSalvato);
            categoria = itemView.findViewById(R.id.txtCategoriaAllergeneSalvato);
        }
    }
}
