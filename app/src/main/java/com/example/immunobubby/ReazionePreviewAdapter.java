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

    private List<Reazione> reactions;
    private Context context;

    public ReazionePreviewAdapter(Context context, List<Reazione> reactions) {
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
        Reazione reaction = reactions.get(position);

        // Mostriamo sintomo principale oppure "Reazione"
        if (reaction.getSintomi() != null && !reaction.getSintomi().isEmpty()) {
            holder.reactionName.setText(reaction.getSintomi().get(0));
        } else {
            holder.reactionName.setText("Reazione");
        }

        // Formattazione data
        if (reaction.getData() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            holder.reactionDate.setText(dateFormat.format(reaction.getData()));
        } else {
            holder.reactionDate.setText("-");
        }
    }

    @Override
    public int getItemCount() {
        return reactions.size();
    }

    static class PreviewViewHolder extends RecyclerView.ViewHolder {
        TextView reactionName, reactionDate;

        public PreviewViewHolder(@NonNull View itemView) {
            super(itemView);
            reactionName = itemView.findViewById(R.id.preview_reaction_name);
            reactionDate = itemView.findViewById(R.id.preview_reaction_date);
        }
    }
}
