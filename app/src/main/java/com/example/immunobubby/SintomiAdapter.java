package com.example.immunobubby;

import android.content.Context;
import androidx.appcompat.app.AlertDialog;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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

        // Nome e frequenza
        holder.txtNome.setText(sintomo.getNome());
        holder.txtFrequenza.setText(sintomo.getFrequenza() != null ? sintomo.getFrequenza() : "");

        // Colore del pallino gravità
        int colorRes = getSeverityColor(sintomo.getGravita());
        holder.statusDot.getBackground().setTint(ContextCompat.getColor(context, colorRes));

        if (isEditingMode) {
            holder.txtFrequenza.setOnClickListener(v -> {
                String[] opzioni = {"Molto frequente", "Frequente", "Raro", "Molto raro"};

                // Adapter personalizzato per testo
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        context,
                        android.R.layout.simple_list_item_1,
                        opzioni
                ) {
                    @NonNull
                    @Override
                    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView textView = view.findViewById(android.R.id.text1);

                        // Colore del testo
                        textView.setTextColor(Color.parseColor("#001229"));

                        return view;
                    }
                };

                View customTitle = LayoutInflater.from(context).inflate(R.layout.dialog_title, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCustomTitle(customTitle); // usa il tuo titolo
                //builder.setTitle("Seleziona frequenza");
                builder.setAdapter(adapter, (dialog, which) -> {
                    String nuovaFrequenza = opzioni[which];
                    sintomo.setFrequenza(nuovaFrequenza);
                    notifyItemChanged(holder.getAdapterPosition());
                });

                // Creazione dialog
                AlertDialog dialog = builder.create();

                // Sfondo finestra
                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_button_bg);
                }

                dialog.setOnShowListener(d -> {
                    int titleId = context.getResources().getIdentifier("alertTitle", "id", "android");
                    TextView titleView = dialog.findViewById(titleId);
                    if (titleView != null) {
                        titleView.setTextColor(Color.parseColor("#001229")); // tuo colore personalizzato
                        titleView.setTypeface(null, Typeface.BOLD);          // opzionale: grassetto
                    }
                });

                dialog.show();
            });
        } else {
            holder.txtFrequenza.setOnClickListener(null);
        }


    }

    @Override
    public int getItemCount() {
        return sintomiList.size();
    }

    /** Cambia modalità modifica */
    public void setEditingMode(boolean editing) {
        this.isEditingMode = editing;
        notifyDataSetChanged();
    }

    /** Mappa gravità → colore */
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

    /** ViewHolder */
    static class SintomiViewHolder extends RecyclerView.ViewHolder {
        TextView txtNome, txtFrequenza;
        View statusDot;

        SintomiViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNome = itemView.findViewById(R.id.txtSintomoNome);
            txtFrequenza = itemView.findViewById(R.id.txtSintomoFrequenza);
            statusDot = itemView.findViewById(R.id.gravita);
        }
    }
}

