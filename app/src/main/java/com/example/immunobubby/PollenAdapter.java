package com.example.immunobubby;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PollenAdapter extends RecyclerView.Adapter<PollenAdapter.PollenViewHolder> {

    private final List<PollenData> pollenList;

    // Mappa dei pollini per categoria
    private final Map<String, String> pollenCategory = new HashMap<String, String>() {{
        // Alberi
        put("alder", "tree");
        put("hazel", "tree");
        put("ash", "tree");
        put("fraxinus", "tree");
        put("birch", "tree");
        put("poplar", "tree");
        put("willow", "tree");
        put("oak", "tree");
        put("olive", "tree");
        put("pine", "tree");
        put("cedar", "tree");
        put("japanese_cedar", "tree");
        put("japanese_cypress", "tree");
        put("maple", "tree");
        put("elm", "tree");
        put("juniper", "tree");
        put("mulberry", "tree");

        // Erbe
        put("grass", "herb");
        put("rye", "herb");
        put("timothy", "herb");
        put("plantain", "herb");
        put("herbs", "herb");

        // Infestanti / Weed
        put("ragweed", "weed");
        put("mugwort", "weed");
        put("chamomile", "weed");
        put("nettle", "weed");
        put("dock", "weed");
        put("sagebrush", "weed");
        put("thistle", "weed");
        put("ambrosia", "weed");
        put("artemisia", "weed");
    }};

    public PollenAdapter(List<PollenData> pollenList) {
        this.pollenList = pollenList;
    }

    @NonNull
    @Override
    public PollenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pollen_card, parent, false);
        return new PollenViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PollenViewHolder holder, int position) {
        PollenData item = pollenList.get(position);

        holder.tvPollenName.setText(item.name);

        // Imposta il livello come testo (Basso, Medio, Alto)
        String levelText;
        switch (item.level) {
            case 0:
            case 1:
                levelText = "Basso";
                break;
            case 2:
            case 3:
                levelText = "Medio";
                break;
            case 4:
            case 5:
                levelText = "Alto";
                break;
            default:
                levelText = "N/D";
        }
        holder.tvPollenLevel.setText(levelText);

        // Imposta icona in base alla categoria
        String category = pollenCategory.get(item.name.toLowerCase());
        int iconRes;
        if ("tree".equals(category)) {
            iconRes = R.drawable.nature_24px;
        } else if ("herb".equals(category)) {
            iconRes = R.drawable.temp_preferences_eco_24px;
        } else if ("weed".equals(category)) {
            iconRes = R.drawable.local_florist_24px;
        } else {
            iconRes = R.drawable.allergy_24px; // default
        }

        holder.pollenIcon.setImageResource(iconRes);
    }

    @Override
    public int getItemCount() {
        return pollenList.size();
    }

    // ViewHolder per i pollini
    static class PollenViewHolder extends RecyclerView.ViewHolder {
        ImageView pollenIcon;
        TextView tvPollenName, tvPollenLevel;

        PollenViewHolder(@NonNull View itemView) {
            super(itemView);
            pollenIcon = itemView.findViewById(R.id.pollenIcon);
            tvPollenName = itemView.findViewById(R.id.tvPollenName);
            tvPollenLevel = itemView.findViewById(R.id.tvPollenLevel);
        }
    }
}
