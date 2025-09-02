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

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {

    private static final String TAG = "KitEmergenzaContactsAD";
    private static final String PREFS_NAME = "KitEmergenzaPrefs";
    private static final String CONTACTS_KEY = "contacts";

    private List<Contatto> items;
    private boolean editing = false;
    private Context context;
    private Contatto lastContact = null; // nuovo

    public ContactsAdapter(Context context, List<Contatto> items, boolean editing) {
        this.context = context;
        this.items = items != null ? items : loadContacts();
        this.editing = editing;
        Log.d(TAG, "Caricati " + this.items.size() + " contatti da SharedPreferences");
    }

    public void setEditing(boolean editing) {
        this.editing = editing;
        notifyDataSetChanged();
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact_card, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        boolean isNewRow = position >= items.size();

        if (!isNewRow) {
            Contatto c = items.get(position);
            holder.etName.setText(c.getNome());
            holder.etNumber.setText(c.getNumero());
            holder.btnRemove.setImageResource(R.drawable.delete_24px);
            holder.btnRemove.setColorFilter(null);
        } else {
            holder.etName.setText("");
            holder.etNumber.setText("");
            holder.btnRemove.setImageResource(R.drawable.ic_add);
            holder.btnRemove.setColorFilter(holder.itemView.getContext().getResources().getColor(R.color.text_dark));
        }

        holder.etName.setEnabled(editing);
        holder.etNumber.setEnabled(editing);
        holder.btnRemove.setVisibility(editing ? View.VISIBLE : View.GONE);

        // Rimuove watcher vecchi
        if (holder.etName.getTag() instanceof LoggingTextWatcher) {
            holder.etName.removeTextChangedListener((LoggingTextWatcher) holder.etName.getTag());
        }
        if (holder.etNumber.getTag() instanceof LoggingTextWatcher) {
            holder.etNumber.removeTextChangedListener((LoggingTextWatcher) holder.etNumber.getTag());
        }

        // Nuovi watcher
        LoggingTextWatcher watcherName = new LoggingTextWatcher(TAG, position, text -> {
            int adapterPos = holder.getAdapterPosition();
            if (adapterPos != RecyclerView.NO_POSITION && !isNewRow) {
                items.get(adapterPos).setNome(text);
            } else {
                if (lastContact == null) lastContact = new Contatto("", "");
                lastContact.setNome(text);
            }
        });
        holder.etName.addTextChangedListener(watcherName);
        holder.etName.setTag(watcherName);

        LoggingTextWatcher watcherNumber = new LoggingTextWatcher(TAG, position, text -> {
            int adapterPos = holder.getAdapterPosition();
            if (adapterPos != RecyclerView.NO_POSITION && !isNewRow) {
                items.get(adapterPos).setNumero(text);
            } else {
                if (lastContact == null) lastContact = new Contatto("", "");
                lastContact.setNumero(text);
            }
        });
        holder.etNumber.addTextChangedListener(watcherNumber);
        holder.etNumber.setTag(watcherNumber);

        // Click pulsante
        holder.btnRemove.setOnClickListener(v -> {
            int adapterPos = holder.getAdapterPosition();
            if (adapterPos == RecyclerView.NO_POSITION) return;

            if (!isNewRow) {
                Log.d(TAG, "Rimosso contatto: " + items.get(adapterPos).getNome());
                items.remove(adapterPos);
                notifyItemRemoved(adapterPos);
            } else {
                Contatto nuovo = new Contatto(holder.etName.getText().toString().trim(),
                        holder.etNumber.getText().toString().trim());
                items.add(nuovo);
                Log.d(TAG, "Aggiunto nuovo contatto: " + nuovo.getNome());
                holder.etName.setText("");
                holder.etNumber.setText("");
                notifyItemInserted(items.size() - 1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size() + (editing ? 1 : 0);
    }

    class ContactViewHolder extends RecyclerView.ViewHolder {
        EditText etName, etNumber;
        ImageButton btnRemove;

        ContactViewHolder(View itemView) {
            super(itemView);
            etName = itemView.findViewById(R.id.contactName);
            etNumber = itemView.findViewById(R.id.contactPhone);
            btnRemove = itemView.findViewById(R.id.btnAction);
        }
    }

    /** Salva tutta la lista filtrando eventuali elementi vuoti e trimmando i campi */
    public void saveAll() {
        List<Contatto> cleaned = new ArrayList<>();

        // Aggiungi lastContact se valido e non già presente
        if (lastContact != null && (!lastContact.getNome().trim().isEmpty() || !lastContact.getNumero().trim().isEmpty())) {
            lastContact.setNome(lastContact.getNome().trim());
            lastContact.setNumero(lastContact.getNumero().trim());

            boolean alreadyPresent = false;
            for (Contatto c : items) {
                if (c.getNome().equals(lastContact.getNome()) && c.getNumero().equals(lastContact.getNumero())) {
                    alreadyPresent = true;
                    break;
                }
            }

            if (!alreadyPresent) {
                items.add(lastContact);
                Log.d(TAG, "Aggiunto lastContact: " + lastContact.getNome() + " - " + lastContact.getNumero());
            } else {
                Log.d(TAG, "lastContact già presente, non aggiunto");
            }

            lastContact = null; // azzera dopo l'uso
        }

        Log.d(TAG, "Salvataggio lista (" + items.size() + " elementi)");
        for (Contatto c : items) {
            Log.d(TAG, "Elemento: " + (c != null ? c.getNome() + " - " + c.getNumero() : "null"));
        }

        // Filtra contatti nulli o con entrambi i campi vuoti
        for (Contatto c : items) {
            if (c != null && (!c.getNome().isEmpty() || !c.getNumero().isEmpty())) {
                cleaned.add(c);
            }
        }

        items = cleaned;
        saveContacts();
    }



    private void saveContacts() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(items);
        editor.putString(CONTACTS_KEY, json);
        editor.apply();
        Log.d(TAG, "Lista contatti salvata (" + items.size() + " elementi)");
    }

    public List<Contatto> loadContacts() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(CONTACTS_KEY, null);
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Contatto>>() {}.getType();
            return gson.fromJson(json, type);
        }
        return new ArrayList<>();
    }

    public List<Contatto> getItems() {
        return items;
    }
}
