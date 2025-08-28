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

    // Mappa dei pollini per categoria (per icone)
    private final Map<String, String> pollenCategory = new HashMap<String, String>() {{
        // Alberi
        put("alder", "tree"); put("hazel", "tree"); put("ash", "tree"); put("fraxinus", "tree");
        put("birch", "tree"); put("poplar", "tree"); put("willow", "tree"); put("oak", "tree");
        put("olive", "tree"); put("pine", "tree"); put("cedar", "tree"); put("japanese_cedar", "tree");
        put("japanese_cypress", "tree"); put("maple", "tree"); put("elm", "tree");
        put("juniper", "tree"); put("mulberry", "tree"); put("cottonwood", "tree"); put("hornbeam", "tree");
        put("beech", "tree"); put("cypress_pine", "tree");

        // Erbe (Graminacee)
        put("grass", "herb"); put("rye", "herb"); put("timothy", "herb"); put("plantain", "herb");
        put("herbs", "herb"); put("bluegrass", "herb"); put("fescue", "herb"); put("bentgrass", "herb");
        put("oat", "herb"); put("ryegrass", "herb"); put("meadowgrass", "herb"); put("sweet_vernal_grass", "herb");
        put("redtop", "herb"); put("creeping_bentgrass", "herb"); put("timothy_hay", "herb");

        // Infestanti / Weed
        put("ragweed", "weed"); put("ambrosia", "weed"); put("mugwort", "weed"); put("artemisia", "weed");
        put("chamomile", "weed"); put("nettle", "weed"); put("dock", "weed"); put("sagebrush", "weed");
        put("thistle", "weed"); put("pigweed", "weed"); put("lamb's_quarters", "weed"); put("goosefoot", "weed");
        put("sowthistle", "weed"); put("dandelion", "weed"); put("plantain_weed", "weed");
        put("common_ragweed", "weed"); put("giant_ragweed", "weed"); put("common_mugwort", "weed"); put("giant_hogweed", "weed");

        // Muffe
        put("mold", "mold"); put("alternaria", "mold"); put("cladosporium", "mold");
        put("aspergillus", "mold"); put("penicillium", "mold");
    }};

    // Mappa dei nomi tradotti in italiano
    private final Map<String, String> pollenNamesIt = new HashMap<String, String>() {{
        put("alder", "Ontano"); put("hazel", "Nocciolo"); put("ash", "Frassino"); put("fraxinus", "Frassino");
        put("birch", "Betulla"); put("poplar", "Pioppo"); put("willow", "Salice"); put("oak", "Quercia");
        put("olive", "Olivo"); put("pine", "Pino"); put("cedar", "Cedro"); put("japanese_cedar", "Cedro giapponese");
        put("japanese_cypress", "Cipresso giapponese"); put("maple", "Acero"); put("elm", "Olmo");
        put("juniper", "Ginepro"); put("mulberry", "Gelso"); put("cottonwood", "Pioppo da cotone"); put("hornbeam", "Carpino");
        put("beech", "Faggio"); put("cypress_pine", "Pino cipresso");

        put("grass", "Graminacee"); put("rye", "Segale"); put("timothy", "Timoteo"); put("plantain", "Piantaggine");
        put("herbs", "Erbe"); put("bluegrass", "Festuca"); put("fescue", "Fescua"); put("bentgrass", "Poacea");
        put("oat", "Avena"); put("ryegrass", "Lolium"); put("meadowgrass", "Erba medica"); put("sweet_vernal_grass", "Erba dolce");
        put("redtop", "Erba rossa"); put("creeping_bentgrass", "Poa strisciante"); put("timothy_hay", "Fieno di timoteo");

        put("ragweed", "Ambrosia"); put("ambrosia", "Ambrosia"); put("mugwort", "Artemisia"); put("artemisia", "Artemisia");
        put("chamomile", "Camomilla"); put("nettle", "Ortica"); put("dock", "Acetosella"); put("sagebrush", "Artemisia");
        put("thistle", "Cardo"); put("pigweed", "Poligono"); put("lamb's_quarters", "Chenopodio"); put("goosefoot", "Chenopodio");
        put("sowthistle", "Sonchus"); put("dandelion", "Dente di leone"); put("plantain_weed", "Piantaggine");
        put("common_ragweed", "Ambrosia comune"); put("giant_ragweed", "Ambrosia gigante"); put("common_mugwort", "Artemisia comune"); put("giant_hogweed", "Erba del diavolo");

        put("mold", "Muffa"); put("alternaria", "Alternaria"); put("cladosporium", "Cladosporium");
        put("aspergillus", "Aspergillus"); put("penicillium", "Penicillium");
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

        // Mostra il nome tradotto in italiano
        String displayName = pollenNamesIt.getOrDefault(item.getName().toLowerCase(), item.getName());
        holder.tvPollenName.setText(displayName);


        String category = pollenCategory.get(item.getName().toLowerCase());
        if (category == null) category = item.getCategory().toLowerCase();

        int iconRes;
        switch (category) {
            case "tree": iconRes = R.drawable.nature_24px; break;
            case "herb": iconRes = R.drawable.temp_preferences_eco_24px; break;
            case "weed": iconRes = R.drawable.local_florist_24px; break;
            case "mold": iconRes = R.drawable.grain_24px; break;
            default: iconRes = R.drawable.allergy_24px; break;
        }

        holder.pollenIcon.setImageResource(iconRes);
    }

    @Override
    public int getItemCount() {
        return pollenList.size();
    }

    public int getTotalActivePollens() {
        return pollenList.size();
    }

    static class PollenViewHolder extends RecyclerView.ViewHolder {
        ImageView pollenIcon;
        TextView tvPollenName;

        PollenViewHolder(@NonNull View itemView) {
            super(itemView);
            pollenIcon = itemView.findViewById(R.id.pollenIcon);
            tvPollenName = itemView.findViewById(R.id.tvPollenName);

        }
    }
}
