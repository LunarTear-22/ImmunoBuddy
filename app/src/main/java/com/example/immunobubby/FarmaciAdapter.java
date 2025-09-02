package com.example.immunobubby;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FarmaciAdapter extends RecyclerView.Adapter<FarmaciAdapter.MedicineViewHolder> {

    private static final String TAG = "KitEmergenzaFarmaciAD";
    private static final String PREFS_NAME = "KitEmergenzaPrefs";
    private static final String MEDICINES_KEY = "medicines";

    private List<Farmaco> items;
    private boolean editing = false;
    private Context context;
    private Farmaco lastItem = null;

    public FarmaciAdapter(Context context, List<Farmaco> items, boolean editing) {
        this.context = context;
        this.items = items != null ? items : loadMedicines();
        Log.d(TAG, "Caricati " + items.size() + " farmaci da SharedPreferences");
    }

    public void setEditing(boolean editing) {
        this.editing = editing;
        notifyDataSetChanged();
    }

    @Override
    public MedicineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_medicine_card_kit, parent, false);
        return new MedicineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MedicineViewHolder holder, int position) {
        boolean isNewRow = position >= items.size();

        if (!isNewRow) {
            Farmaco f = items.get(position);
            holder.etName.setText(f.getNome());
            holder.etType.setText(f.getTipologia());
            holder.etDosaggio.setText(f.getDosaggio());
            holder.btnAction.setImageResource(R.drawable.delete_24px);
            holder.btnAction.setColorFilter(null);
        } else {
            holder.etName.setText("");
            holder.etType.setText("");
            holder.etDosaggio.setText("");
            holder.btnAction.setImageResource(R.drawable.ic_add);
            holder.btnAction.setColorFilter(holder.itemView.getResources().getColor(R.color.text_dark));
        }

        holder.etName.setEnabled(editing);
        holder.etType.setEnabled(editing);
        holder.etDosaggio.setEnabled(editing);
        holder.btnAction.setVisibility(editing ? View.VISIBLE : View.GONE);

        // Rimuove TextWatcher precedente
        if (holder.etName.getTag() instanceof LoggingTextWatcher) {
            holder.etName.removeTextChangedListener((LoggingTextWatcher) holder.etName.getTag());
            holder.etType.removeTextChangedListener((LoggingTextWatcher) holder.etType.getTag());
            holder.etDosaggio.removeTextChangedListener((LoggingTextWatcher) holder.etDosaggio.getTag());
        }

        // Aggiunge nuovi TextWatcher
        LoggingTextWatcher watcherName = new LoggingTextWatcher(TAG, position, text -> {
            int adapterPos = holder.getAdapterPosition();
            if (adapterPos != RecyclerView.NO_POSITION && !isNewRow) {
                items.get(adapterPos).setNome(text);
            } else {
                if (lastItem == null) lastItem = new Farmaco("", "", "");
                lastItem.setNome(text);
            }
        });
        holder.etName.addTextChangedListener(watcherName);
        holder.etName.setTag(watcherName);

        LoggingTextWatcher watcherType = new LoggingTextWatcher(TAG, position, text -> {
            int adapterPos = holder.getAdapterPosition();
            if (adapterPos != RecyclerView.NO_POSITION && !isNewRow) {
                items.get(adapterPos).setTipologia(text);
            } else {
                if (lastItem == null) lastItem = new Farmaco("", "", "");
                lastItem.setTipologia(text);
            }
        });
        holder.etType.addTextChangedListener(watcherType);
        holder.etType.setTag(watcherType);

        LoggingTextWatcher watcherDosage = new LoggingTextWatcher(TAG, position, text -> {
            int adapterPos = holder.getAdapterPosition();
            if (adapterPos != RecyclerView.NO_POSITION && !isNewRow) {
                items.get(adapterPos).setDosaggio(text);
            } else {
                if (lastItem == null) lastItem = new Farmaco("", "", "");
                lastItem.setDosaggio(text);
            }
        });
        holder.etDosaggio.addTextChangedListener(watcherDosage);
        holder.etDosaggio.setTag(watcherDosage);

        // Gestione click pulsante
        holder.btnAction.setOnClickListener(v -> {
            int adapterPos = holder.getAdapterPosition();
            if (adapterPos == RecyclerView.NO_POSITION) return;

            if (!isNewRow) {
                Log.d(TAG, "Rimosso farmaco: " + items.get(adapterPos).getNome());
                items.remove(adapterPos);
                notifyItemRemoved(adapterPos);
            } else {
                Farmaco nuovo = new Farmaco(holder.etName.getText().toString().trim(), holder.etType.getText().toString().trim(), holder.etDosaggio.getText().toString().trim());
                items.add(nuovo);
                Log.d(TAG, "Aggiunto nuovo farmaco: " + nuovo.getNome());
                holder.etName.setText("");
                holder.etType.setText("");
                holder.etDosaggio.setText("");
                notifyItemInserted(items.size() - 1);
            }

        });
    }

    @Override
    public int getItemCount() {
        return items.size() + (editing ? 1 : 0);
    }

    class MedicineViewHolder extends RecyclerView.ViewHolder {
        EditText etName, etType, etDosaggio;
        ImageButton btnAction;

        MedicineViewHolder(View itemView) {
            super(itemView);
            etName = itemView.findViewById(R.id.tvMedicineName);
            etType = itemView.findViewById(R.id.tvMedicineType);
            etDosaggio = itemView.findViewById(R.id.tvMedicineDosage);
            btnAction = itemView.findViewById(R.id.btnAction);
        }
    }

    /** Salva tutti gli elementi, includendo eventuale lastItem non ancora aggiunto */
    public void saveAll() {
        List<Farmaco> cleaned = new ArrayList<>();

        // Aggiungi lastItem se valido e non già presente
        if (lastItem != null && (!lastItem.getNome().trim().isEmpty() ||
                !lastItem.getTipologia().trim().isEmpty() ||
                !lastItem.getDosaggio().trim().isEmpty())) {

            lastItem.setNome(lastItem.getNome().trim());
            lastItem.setTipologia(lastItem.getTipologia().trim());
            lastItem.setDosaggio(lastItem.getDosaggio().trim());

            boolean alreadyPresent = false;
            for (Farmaco f : items) {
                if (f.getNome().equals(lastItem.getNome()) &&
                        f.getTipologia().equals(lastItem.getTipologia()) &&
                        f.getDosaggio().equals(lastItem.getDosaggio())) {
                    alreadyPresent = true;
                    break;
                }
            }

            if (!alreadyPresent) {
                items.add(lastItem);
                Log.d(TAG, "Aggiunto lastItem: " + lastItem.getNome() + " - " + lastItem.getTipologia() + " - " + lastItem.getDosaggio());
            } else {
                Log.d(TAG, "lastItem già presente, non aggiunto");
            }

            lastItem = null; // azzera dopo l'uso
        }

        Log.d(TAG, "Salvataggio lista (" + items.size() + " elementi)");
        for (Farmaco f : items) {
            Log.d(TAG, "Elemento: " + f.getNome() + " - " + f.getTipologia() + " - " + f.getDosaggio());
        }

        // Filtra elementi nulli o completamente vuoti
        for (Farmaco f : items) {
            if (f != null && (!f.getNome().isEmpty() || !f.getTipologia().isEmpty() || !f.getDosaggio().isEmpty())) {
                cleaned.add(f);
            }
        }

        items = cleaned;
        saveMedicines();
    }


    /** Salva la lista aggiornata in SharedPreferences */
    private void saveMedicines() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        editor.putString(MEDICINES_KEY, gson.toJson(items));
        editor.apply();
        Log.d(TAG, "Lista farmaci salvata (" + items.size() + " elementi)");
    }

    /** Carica la lista dei farmaci da SharedPreferences */
    public List<Farmaco> loadMedicines() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(MEDICINES_KEY, null);
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Farmaco>>() {}.getType();
            return gson.fromJson(json, type);
        }
        return new ArrayList<>();
    }

    public List<Farmaco> getItems() {
        return items;
    }
}
