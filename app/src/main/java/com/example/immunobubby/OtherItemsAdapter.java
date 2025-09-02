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

public class OtherItemsAdapter extends RecyclerView.Adapter<OtherItemsAdapter.OtherViewHolder> {

    private static final String TAG = "KitEmergenzaOtherItemsAD";
    private static final String PREFS_NAME = "KitEmergenzaPrefs";
    private static final String OTHER_KEY = "otherItems";

    private List<String> items;
    private boolean editing = false;
    private Context context;
    private String lastItemName = null;

    public OtherItemsAdapter(Context context, List<String> items, boolean editing) {
        this.context = context;
        this.items = items != null ? items : new ArrayList<>();
        this.editing = editing;
        Log.d(TAG, "Caricati " + items.size() + " elementi da SharedPreferences");
    }

    public void setEditing(boolean editing) {
        this.editing = editing;
        notifyDataSetChanged();
    }

    @Override
    public OtherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_other_card, parent, false);
        return new OtherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OtherViewHolder holder, int position) {
        boolean isNewRow = position >= items.size();

        // Imposta testo e icona
        if (!isNewRow) {
            holder.etName.setText(items.get(position));
            holder.btnAction.setImageResource(R.drawable.delete_24px);
            holder.btnAction.setColorFilter(null);
        } else {
            holder.etName.setText("");
            holder.btnAction.setImageResource(R.drawable.ic_add);
            holder.btnAction.setColorFilter(holder.itemView.getResources().getColor(R.color.text_dark));
        }

        holder.etName.setEnabled(editing);
        holder.btnAction.setVisibility(editing ? View.VISIBLE : View.GONE);

        // Rimuove TextWatcher precedente per evitare duplicati
        if (holder.etName.getTag() instanceof LoggingTextWatcher) {
            holder.etName.removeTextChangedListener((LoggingTextWatcher) holder.etName.getTag());
        }

        // Aggiunge nuovo TextWatcher
        LoggingTextWatcher watcher = new LoggingTextWatcher(TAG, position, text -> {
            int adapterPos = holder.getAdapterPosition();
            if (adapterPos != RecyclerView.NO_POSITION && !isNewRow) {
                items.set(adapterPos, text);
            }else{
               lastItemName = holder.etName.getText().toString().trim();
            }
        });
        holder.etName.addTextChangedListener(watcher);
        holder.etName.setTag(watcher);

        // Gestione click pulsante
        holder.btnAction.setOnClickListener(v -> {
            int adapterPos = holder.getAdapterPosition();
            if (adapterPos == RecyclerView.NO_POSITION) return;

            if (!isNewRow) {
                items.remove(adapterPos);
                notifyItemRemoved(adapterPos);
            } else {
                items.add(holder.etName.getText().toString().trim());
                holder.etName.setText("");
                notifyItemInserted(items.size() - 1);
            }

        });

    }

    @Override
    public int getItemCount() {
        return items.size() + (editing ? 1 : 0);
    }

    class OtherViewHolder extends RecyclerView.ViewHolder {
        EditText etName;
        ImageButton btnAction;

        OtherViewHolder(View itemView) {
            super(itemView);
            etName = itemView.findViewById(R.id.tvOtherName);
            btnAction = itemView.findViewById(R.id.btnAction);
        }
    }

    /** Salva tutta la lista filtrando eventuali elementi vuoti */
    public void saveAll() {
        List<String> cleaned = new ArrayList<>();

        // Aggiungi lastItemName solo se valido e non già presente
        if (lastItemName != null && !lastItemName.trim().isEmpty()) {
            if (!items.contains(lastItemName.trim())) {
                items.add(lastItemName.trim());
                Log.d(TAG, "Aggiunto lastItemName: " + lastItemName);
            } else {
                Log.d(TAG, "lastItemName già presente, non aggiunto: " + lastItemName);
            }
            lastItemName = null; // azzera dopo l'uso
        }

        Log.d(TAG, "Salvataggio lista (" + items.size() + " elementi)");
        for (String item : items) {
            Log.d(TAG, "Elemento: " + item);
        }

        // Filtra eventuali stringhe vuote
        for (String item : items) {
            if (item != null && !item.trim().isEmpty()) {
                cleaned.add(item.trim());
            }
        }

        items = cleaned;
        saveItems();
    }


    /** Salva la lista corrente in SharedPreferences */
    private void saveItems() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(items);
        editor.putString(OTHER_KEY, json);
        editor.apply();
    }

    /** Carica la lista salvata */
    public List<String> loadItems() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(OTHER_KEY, null);
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<String>>() {}.getType();
            return gson.fromJson(json, type);
        }
        return new ArrayList<>();
    }

    public List<String> getItems() {
        return items;
    }
}
