package com.example.immunobubby;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AllergeniSpecificiAdapter extends RecyclerView.Adapter<AllergeniSpecificiAdapter.AllergeneViewHolder> {

    private final Context context;
    private final List<String> allergeniList;

    public AllergeniSpecificiAdapter(Context context, List<String> allergeniList) {
        this.context = context;
        this.allergeniList = allergeniList;
    }

    @NonNull
    @Override
    public AllergeneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_allergeni_specifici, parent, false);
        return new AllergeneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllergeneViewHolder holder, int position) {
        String allergene = allergeniList.get(position);
        holder.txtNomeAllergene.setText(allergene);

        // Click listener per aprire la prossima activity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SalvaAllergeneActivity.class);
            intent.putExtra("allergene_nome", allergene);
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
