package com.example.immunobubby;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class KitEmergenzaActivity extends BaseActivity {

    private boolean isEditing = false;
    private FloatingActionButton fab;

    private RecyclerView recyclerMedicines, recyclerOther, recyclerContacts;

    private List<Farmaco> medicinesList = new ArrayList<>();
    private List<String> otherItemsList = new ArrayList<>();
    private List<Contatto> contactsList = new ArrayList<>();

    private MedicinesAdapter medicinesAdapter;
    private OtherItemsAdapter otherAdapter;
    private ContactsAdapter contactsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kit_emergenza);

        fab = findViewById(R.id.fab_edit_save);
        recyclerMedicines = findViewById(R.id.recycler_medicines);
        recyclerOther = findViewById(R.id.recycler_other_items);
        recyclerContacts = findViewById(R.id.recycler_contacts);

        // Esempi iniziali
        medicinesList.add(new Farmaco("Jext®", "Antistaminico", "0,3mg"));
        medicinesList.add(new Farmaco("Xyzal®", "Antistaminico", "5mg"));
        otherItemsList.add("Mascherina FFP2");
        contactsList.add(new Contatto("Casa", "+39 081 234 5678"));

        // Configura RecyclerView
        recyclerMedicines.setLayoutManager(new LinearLayoutManager(this));
        recyclerOther.setLayoutManager(new LinearLayoutManager(this));
        recyclerContacts.setLayoutManager(new LinearLayoutManager(this));

        medicinesAdapter = new MedicinesAdapter(medicinesList);
        otherAdapter = new OtherItemsAdapter(otherItemsList);
        contactsAdapter = new ContactsAdapter(contactsList);

        recyclerMedicines.setAdapter(medicinesAdapter);
        recyclerOther.setAdapter(otherAdapter);
        recyclerContacts.setAdapter(contactsAdapter);

        fab.setOnClickListener(view -> {
            if (isEditing) {
                medicinesAdapter.saveAll();
                contactsAdapter.saveAll();
                otherAdapter.saveAll();
            }
            isEditing = !isEditing;
            updateFabIcon();
            toggleEditMode(isEditing);
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();
        if (v instanceof EditText) {
            int[] scrcoords = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (ev.getAction() == MotionEvent.ACTION_DOWN
                    && (x < v.getLeft() || x >= v.getRight()
                    || y < v.getTop() || y > v.getBottom())) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                v.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void updateFabIcon() {
        fab.setImageResource(isEditing ? R.drawable.check_24px : R.drawable.edit_24px);
    }

    private void toggleEditMode(boolean editMode) {
        medicinesAdapter.setEditing(editMode);
        otherAdapter.setEditing(editMode);
        contactsAdapter.setEditing(editMode);
    }

    // ------------------- ADAPTER MEDICINES -------------------
    private class MedicinesAdapter extends RecyclerView.Adapter<MedicinesAdapter.MedicineViewHolder> {

        private List<Farmaco> items;
        private boolean editing = false;

        public MedicinesAdapter(List<Farmaco> items) {
            this.items = items != null ? items : new ArrayList<>();
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
                holder.btnRemove.setImageResource(R.drawable.delete_24px);
                holder.btnRemove.setColorFilter(null);
            } else {
                holder.btnRemove.setImageResource(R.drawable.ic_add);
                holder.btnRemove.setColorFilter(getResources().getColor(R.color.text_dark));
            }

            holder.etName.setEnabled(editing);
            holder.etType.setEnabled(editing);
            holder.etDosaggio.setEnabled(editing);
            holder.btnRemove.setVisibility(editing ? View.VISIBLE : View.GONE);

            holder.btnRemove.setOnClickListener(v -> {
                if (!isNewRow) {
                    items.remove(position);
                    notifyItemRemoved(position);
                } else {
                    items.add(new Farmaco("", "", ""));
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
            ImageButton btnRemove;

            MedicineViewHolder(View itemView) {
                super(itemView);
                etName = itemView.findViewById(R.id.tvMedicineName);
                etType = itemView.findViewById(R.id.tvMedicineType);
                etDosaggio = itemView.findViewById(R.id.tvMedicineDosage);
                btnRemove = itemView.findViewById(R.id.btnRemove);
            }
        }

        public void saveAll() {
            for (int i = 0; i < items.size(); i++) {
                MedicineViewHolder holder = (MedicineViewHolder) recyclerMedicines.findViewHolderForAdapterPosition(i);
                if (holder != null) {
                    items.get(i).setNome(holder.etName.getText().toString().trim());
                    items.get(i).setTipologia(holder.etType.getText().toString().trim());
                    items.get(i).setDosaggio(holder.etDosaggio.getText().toString().trim());
                }
            }
        }
    }

    // ------------------- ADAPTER CONTACTS -------------------
    private class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {

        private List<Contatto> items;
        private boolean editing = false;

        public ContactsAdapter(List<Contatto> items) {
            this.items = items != null ? items : new ArrayList<>();
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
                holder.btnRemove.setImageResource(R.drawable.ic_add);
                holder.btnRemove.setColorFilter(getResources().getColor(R.color.text_dark));
            }

            holder.etName.setEnabled(editing);
            holder.etNumber.setEnabled(editing);
            holder.btnRemove.setVisibility(editing ? View.VISIBLE : View.GONE);

            holder.btnRemove.setOnClickListener(v -> {
                if (!isNewRow) {
                    items.remove(position);
                    notifyItemRemoved(position);
                } else {
                    items.add(new Contatto("", ""));
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
                btnRemove = itemView.findViewById(R.id.contactAction);
            }
        }

        public void saveAll() {
            for (int i = 0; i < items.size(); i++) {
                ContactViewHolder holder = (ContactViewHolder) recyclerContacts.findViewHolderForAdapterPosition(i);
                if (holder != null) {
                    items.get(i).setNome(holder.etName.getText().toString().trim());
                    items.get(i).setNumero(holder.etNumber.getText().toString().trim());
                }
            }
        }
    }

    // ------------------- ADAPTER OTHER ITEMS -------------------
    private class OtherItemsAdapter extends RecyclerView.Adapter<OtherItemsAdapter.OtherViewHolder> {

        private List<String> items;
        private boolean editing = false;

        public OtherItemsAdapter(List<String> items) {
            this.items = items != null ? items : new ArrayList<>();
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

            if (!isNewRow) {
                holder.etName.setText(items.get(position));
                holder.btnRemove.setImageResource(R.drawable.delete_24px);
                holder.btnRemove.setColorFilter(null);
            } else {
                holder.btnRemove.setImageResource(R.drawable.ic_add);
                holder.btnRemove.setColorFilter(getResources().getColor(R.color.text_dark));
            }

            holder.etName.setEnabled(editing);
            holder.btnRemove.setVisibility(editing ? View.VISIBLE : View.GONE);

            holder.btnRemove.setOnClickListener(v -> {
                if (!isNewRow) {
                    items.remove(position);
                    notifyItemRemoved(position);
                } else {
                    items.add("");
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
            ImageButton btnRemove;

            OtherViewHolder(View itemView) {
                super(itemView);
                etName = itemView.findViewById(R.id.tvOtherName);
                btnRemove = itemView.findViewById(R.id.btnRemoveOther);
            }
        }

        public void saveAll() {
            for (int i = 0; i < items.size(); i++) {
                OtherViewHolder holder = (OtherViewHolder) recyclerOther.findViewHolderForAdapterPosition(i);
                if (holder != null) {
                    String text = holder.etName.getText().toString().trim();
                    if (!text.isEmpty()) {
                        items.set(i, text);
                    }
                }
            }
        }
    }
}
