package com.example.immunobubby;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class SintomiAdapter extends RecyclerView.Adapter<SintomiAdapter.SintomiViewHolder> {

    private final List<Sintomi> sintomiList;
    private final Context context;
    private boolean isEditingMode = false;

    public SintomiAdapter(Context context, List<Sintomi> sintomiList) {
        this.context = context;
        this.sintomiList = sintomiList;
    }

    @NonNull
    @Override
    public SintomiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sintomo, parent, false);
        return new SintomiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SintomiViewHolder holder, int position) {
        Sintomi sintomo = sintomiList.get(position);

        holder.txtNome.setText(sintomo.getNome());
        holder.txtFrequenza.setText(sintomo.getFrequenza() != null ? sintomo.getFrequenza() : "");

        // Colore del pallino in base alla gravità (string compare, case-insensitive)
        String grav = sintomo.getGravita();
        int colorRes = getSeverityColor(sintomo.getGravita());
        Drawable background = holder.severityIndicator.getBackground();
        if (background != null) {
            background = DrawableCompat.wrap(background.mutate());
            DrawableCompat.setTint(background, ContextCompat.getColor(context, colorRes));
            holder.severityIndicator.setBackground(background);
        }

        // Assicurati che statusDot abbia un background (es. drawable dot_circle)
        if (holder.statusDot.getBackground() != null) {
            holder.statusDot.getBackground().setTint(ContextCompat.getColor(context, colorRes));
        } else {
            holder.statusDot.setBackgroundColor(ContextCompat.getColor(context, colorRes));
        }

        // Click sulla frequenza SOLO se siamo in modalità modifica
        holder.frequenzaTextView.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Seleziona frequenza");

            String[] opzioni = {"Molto frequente", "Frequente", "Raro", "Molto raro"};

            builder.setItems(opzioni, (dialog, which) -> {
                String nuovaFrequenza = opzioni[which];
                sintomo.setFrequenza(nuovaFrequenza);
                notifyItemChanged(holder.getAdapterPosition());
            });

            builder.show();
        });

    }

    private int getSeverityColor(String gravita) {
        if (gravita == null) return R.color.gravity_mild;

        String g = gravita.trim().toLowerCase(Locale.ROOT);

        switch (g) {
            case "lieve":
                return R.color.gravity_mild;
            case "moderato":
            case "moderata":
            case "media":
                return R.color.gravity_moderate;
            case "significativo":
            case "significativa":
                return R.color.gravity_significant;
            case "grave":
                return R.color.gravity_severe;
            default:
                return R.color.gravity_mild;
        }
    }


    @Override
    public int getItemCount() {
        return sintomiList.size();
    }

    public void setEditingMode(boolean editing) {
        this.isEditingMode = editing;
        notifyDataSetChanged();
    }

    public List<Sintomi> getSintomiList() {
        return sintomiList;
    }

    static class SintomiViewHolder extends RecyclerView.ViewHolder {
        View frequenzaTextView;
        TextView txtNome, txtFrequenza;
        View severityIndicator;
        View statusDot;

        SintomiViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNome = itemView.findViewById(R.id.txtSintomoNome);
            txtFrequenza = itemView.findViewById(R.id.txtSintomoFrequenza);
            statusDot = itemView.findViewById(R.id.gravita);
        }
    }
}
