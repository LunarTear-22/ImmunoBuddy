package com.example.immunobubby;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReazioneAdapter extends RecyclerView.Adapter<ReazioneAdapter.ReactionViewHolder> {

    private List<Reazione> reactions;
    private Context context;
    private OnReactionClickListener listener;
    private boolean open = false;

    public interface OnReactionClickListener {
        void onReactionNameClick(Reazione reaction);
    }

    public ReazioneAdapter(Context context, List<Reazione> reactions) {
        this.context = context;
        this.reactions = reactions;
    }

    public void setOnReactionClickListener(OnReactionClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reaction, parent, false);
        return new ReactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReactionViewHolder holder, int position) {
        Reazione reaction = reactions.get(position);

        String reactionName = generateReactionName(reaction);
        holder.reactionName.setText(reactionName);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        holder.reactionDate.setText(reaction.getData() != null ? dateFormat.format(reaction.getData()) : "Non specificato");

        int colorRes = getSeverityColor(reaction.getGravita());
        Drawable background = holder.severityIndicator.getBackground();
        if (background != null) {
            background = DrawableCompat.wrap(background.mutate());
            DrawableCompat.setTint(background, ContextCompat.getColor(context, colorRes));
            holder.severityIndicator.setBackground(background);
        }

        // Click sul nome per mostrare la card
        holder.reactionName.setOnClickListener(v -> {
            if (listener != null) {
                listener.onReactionNameClick(reaction);
            }
        });
    }


    @Override
    public int getItemCount() {
        return reactions.size();
    }

    private String generateReactionName(Reazione reaction) {
        String allergen = reaction.getAllergene();
        List<String> symptoms = reaction.getSintomi();

        if (allergen != null && !allergen.isEmpty()) {
            if (symptoms != null && !symptoms.isEmpty()) {
                return symptoms.get(0) + " da " + allergen;
            } else {
                return "Reazione a " + allergen;
            }
        } else if (symptoms != null && !symptoms.isEmpty()) {
            return symptoms.get(0);
        } else {
            return "Reazione allergica";
        }
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


    public void updateReactions(List<Reazione> newReactions) {
        this.reactions = newReactions;
        notifyDataSetChanged();
    }

    private void populateDetailView(ReactionViewHolder holder, Reazione reaction) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        if(reaction.getOra()!=null){
            holder.detailAllergeni.setText(reaction.getAllergene() != null ? reaction.getAllergene() : "Non specificato");
            holder.detailOra.setText(reaction.getOra());
        }

        /*if(reaction.getFoto() != null) {
            holder.detailPhotos.setImageBitmap(reaction.getFoto());

        }*/

        // Data
        if (reaction.getData() != null) {
            holder.reactionDate.setText(dateFormat.format(reaction.getData()));
        } else {
            holder.reactionDate.setText("Non specificato");
        }

        // Ora (già String)
        holder.detailOra.setText(reaction.getOra() != null && !reaction.getOra().isEmpty()
                ? reaction.getOra()
                : "Non specificato");

        // Sintomi
        List<String> sintomi = reaction.getSintomi();
        holder.detailSintomi.setText(sintomi != null && !sintomi.isEmpty()
                ? String.join(", ", sintomi)
                : "Nessuno");

        // Medico contattato
        holder.detailMedico.setText(reaction.getContattoMedico() != null
                ? (reaction.getContattoMedico() ? "Sì" : "No")
                : "Non specificato");

        // Farmaci
        List<String> farmaci = reaction.getFarmaci();
        holder.detailFarmaci.setText(farmaci != null && !farmaci.isEmpty()
                ? String.join(", ", farmaci)
                : "Nessuno");

        // Note
        holder.detailNote.setText(reaction.getNote() != null && !reaction.getNote().isEmpty()
                ? reaction.getNote()
                : "Assenti");
    }


    static class ReactionViewHolder extends RecyclerView.ViewHolder {
        TextView reactionName;
        TextView reactionDate;
        View severityIndicator;
        MaterialCardView detailsCardContainer;
        TextView detailOra;
        TextView detailAllergeni;
        TextView detailSintomi;
        TextView detailMedico;
        TextView detailFarmaci;
        TextView detailNote;
        ImageView detailPhotos;

        public ReactionViewHolder(@NonNull View itemView) {
            super(itemView);
            reactionName = itemView.findViewById(R.id.reaction_name);
            reactionDate = itemView.findViewById(R.id.reaction_date);
            severityIndicator = itemView.findViewById(R.id.severity_indicator);
            detailsCardContainer = itemView.findViewById(R.id.reaction_detail_card);
            detailOra = itemView.findViewById(R.id.detail_ora);
            detailAllergeni = itemView.findViewById(R.id.detail_allergeni);
            detailSintomi = itemView.findViewById(R.id.detail_sintomi);
            detailMedico = itemView.findViewById(R.id.detail_medico);
            detailFarmaci = itemView.findViewById(R.id.detail_farmaci);
            detailNote = itemView.findViewById(R.id.detail_note);
            detailPhotos = itemView.findViewById(R.id.detail_photo);
        }
    }
}
