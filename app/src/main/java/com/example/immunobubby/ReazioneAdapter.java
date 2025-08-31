package com.example.immunobubby;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReazioneAdapter extends RecyclerView.Adapter<ReazioneAdapter.ReactionViewHolder> {

    private List<Reazione> reactions;
    private Context context;
    private OnReactionClickListener listener;
    private int expandedPosition = -1;

    public interface OnReactionClickListener {
        void onReactionClick(Reazione reaction);
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
        holder.reactionDate.setText(dateFormat.format(reaction.getData()));

        int colorRes = getSeverityColor(reaction.getGravita());
        Drawable background = holder.severityIndicator.getBackground();
        if (background != null) {
            background = DrawableCompat.wrap(background.mutate());
            DrawableCompat.setTint(background, ContextCompat.getColor(context, colorRes));
            holder.severityIndicator.setBackground(background);
        }

        boolean isExpanded = position == expandedPosition;
        holder.detailLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        if (isExpanded) {
            populateDetailView(holder, reaction);
        }

        holder.itemView.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) return;

            int previousExpandedPosition = expandedPosition;
            if (currentPosition == expandedPosition) {
                expandedPosition = -1;
            } else {
                expandedPosition = currentPosition;
            }

            if (previousExpandedPosition != -1) {
                notifyItemChanged(previousExpandedPosition);
            }
            notifyItemChanged(currentPosition);
        });

        holder.closeButton.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) return;

            expandedPosition = -1;
            notifyItemChanged(currentPosition);
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
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        holder.detailTitle.setText(generateReactionName(reaction));
        holder.detailData.setText(dateFormat.format(reaction.getData()));
        holder.detailOra.setText(reaction.getOra() != null ? timeFormat.format(reaction.getOra()) : "Non specificato");
        holder.detailAllergeni.setText(reaction.getAllergene() != null ? reaction.getAllergene() : "Non specificato");

        // Format symptoms list
        List<String> sintomi = reaction.getSintomi();
        if (sintomi != null && !sintomi.isEmpty()) {
            holder.detailSintomi.setText(String.join(", ", sintomi));
        } else {
            holder.detailSintomi.setText("Nessuno");
        }

        holder.detailMedico.setText(reaction.getContattoMedico() != null ? (reaction.getContattoMedico() ? "Sì" : "No") : "Non specificato");

        // Format medications list
        List<String> farmaci = reaction.getFarmaci();
        if (farmaci != null && !farmaci.isEmpty()) {
            holder.detailFarmaci.setText(String.join(", ", farmaci));
        } else {
            holder.detailFarmaci.setText("Nessuno");
        }

        holder.detailNote.setText(reaction.getNote() != null && !reaction.getNote().isEmpty() ? reaction.getNote() : "Assenti");
    }

    static class ReactionViewHolder extends RecyclerView.ViewHolder {
        TextView reactionName;
        TextView reactionDate;
        View severityIndicator;
        ViewGroup detailLayout;
        TextView closeButton;
        TextView detailTitle;
        TextView detailData;
        TextView detailOra;
        TextView detailAllergeni;
        TextView detailSintomi;
        TextView detailMedico;
        TextView detailFarmaci;
        TextView detailNote;
        RecyclerView detailPhotosRecycler;

        public ReactionViewHolder(@NonNull View itemView) {
            super(itemView);
            reactionName = itemView.findViewById(R.id.reaction_name);
            reactionDate = itemView.findViewById(R.id.reaction_date);
            severityIndicator = itemView.findViewById(R.id.severity_indicator);

            detailLayout = itemView.findViewById(R.id.detail_layout);
            closeButton = itemView.findViewById(R.id.close_button);
            detailTitle = itemView.findViewById(R.id.detail_title);
            detailData = itemView.findViewById(R.id.detail_data);
            detailOra = itemView.findViewById(R.id.detail_ora);
            detailAllergeni = itemView.findViewById(R.id.detail_allergeni);
            detailSintomi = itemView.findViewById(R.id.detail_sintomi);
            detailMedico = itemView.findViewById(R.id.detail_medico);
            detailFarmaci = itemView.findViewById(R.id.detail_farmaci);
            detailNote = itemView.findViewById(R.id.detail_note);
            detailPhotosRecycler = itemView.findViewById(R.id.detail_photos_recycler);
        }
    }
}
