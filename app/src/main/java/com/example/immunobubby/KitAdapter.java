package com.example.immunobubby;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class KitAdapter extends RecyclerView.Adapter<KitAdapter.KitViewHolder> {
    private List<Kit> kitList;
    private Context context;

    public KitAdapter(Context context, List<Kit> kitList) {
        this.context = context;
        this.kitList = kitList;
    }

    @NonNull
    @Override
    public KitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_kit, parent, false);
        return new KitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KitViewHolder holder, int position) {
        Kit kit = kitList.get(position);
        holder.name.setText(kit.getName());
        holder.description.setText(kit.getDescription());
    }

    @Override
    public int getItemCount() {
        return kitList.size();
    }

    public static class KitViewHolder extends RecyclerView.ViewHolder {
        TextView name, description, date;

        public KitViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nome_farmaco);
            description = itemView.findViewById(R.id.descrizione_farmaco);
        }
    }
}
