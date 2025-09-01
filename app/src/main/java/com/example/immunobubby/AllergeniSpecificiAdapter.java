package com.example.immunobubby;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AllergeniSpecificiAdapter extends RecyclerView.Adapter<AllergeniSpecificiAdapter.AllergeneViewHolder> {

    private static final String TAG = "AllergeniSpecificiAdapter";

    private final Context context;
    private final List<String> allergeniList;
    private final String categoriaPadre;

    public AllergeniSpecificiAdapter(Context context, List<String> allergeniList, String categoriaPadre) {
        this.context = context;
        this.allergeniList = allergeniList;
        this.categoriaPadre = categoriaPadre;
        Log.d(TAG, "Adapter creato con " + allergeniList.size() + " allergeni specifici");
    }

    @NonNull
    @Override
    public AllergeneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder chiamato");
        View view = LayoutInflater.from(context).inflate(R.layout.item_allergeni_specifici, parent, false);
        return new AllergeneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllergeneViewHolder holder, int position) {
        String allergene = allergeniList.get(position);
        holder.txtNomeAllergene.setText(allergene);
        Log.d(TAG, "Bind allergene: " + allergene + " alla posizione: " + position);

        holder.itemView.setOnClickListener(v -> {
            Log.d(TAG, "Click sull'allergene specifico: " + allergene);
            Intent intent = new Intent(context, SalvaAllergeneActivity.class);
            intent.putExtra("allergene_nome", allergene);


            Log.d(TAG, "Categoria inviata: " + categoriaPadre);
            intent.putExtra("categoria_nome", categoriaPadre);

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return allergeniList.size();
    }

    static class AllergeneViewHolder extends RecyclerView.ViewHolder {
        TextView txtNomeAllergene;

        public AllergeneViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNomeAllergene = itemView.findViewById(R.id.txtNomeAllergene);
        }
    }
}
