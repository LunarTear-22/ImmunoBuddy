package com.example.immunobubby;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReazionePreviewAdapter extends RecyclerView.Adapter<ReazionePreviewAdapter.PreviewViewHolder> {

    private List<ReazioniPreview> reactions;
    private Context context;

    public ReazionePreviewAdapter(Context context, List<ReazioniPreview> reactions) {
        this.context = context;
        this.reactions = reactions;
    }

    @NonNull
    @Override
    public PreviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reaction_preview, parent, false);
        return new PreviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PreviewViewHolder holder, int position) {
        ReazioniPreview reaction = reactions.get(position);

        // Mostriamo sintomo principale oppure "Reazione"
        if (reaction.getName() != null && !reaction.getName().isEmpty()) {
            holder.reactionName.setText(reaction.getName());
        } else {
            holder.reactionName.setText("Reazione");
        }

        // Formattazione data
        if (reaction.getData() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            holder.reactionDate.setText(dateFormat.format(reaction.getData()));
        }

        // Se l'elemento è il "kit vuoto", nascondi il trattino
        if ("Non sono presenti reazioni recenti".equals(reaction.getName())) {
            holder.trattino.setVisibility(View.GONE);
        } else {
            holder.trattino.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return reactions.size();
    }

    static class PreviewViewHolder extends RecyclerView.ViewHolder {
        TextView reactionName, reactionDate, trattino;

        public PreviewViewHolder(@NonNull View itemView) {
            super(itemView);
            reactionName = itemView.findViewById(R.id.preview_reaction_name);
            reactionDate = itemView.findViewById(R.id.preview_reaction_date);
            trattino = itemView.findViewById(R.id.trattino2);
        }
    }
}
