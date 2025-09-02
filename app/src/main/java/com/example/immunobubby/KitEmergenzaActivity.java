package com.example.immunobubby;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class KitEmergenzaActivity extends BaseActivity {

    private boolean isEditing = false;
    private FloatingActionButton fab;

    private RecyclerView recyclerOther, recyclerContacts, recyclerMedicines;
    private OtherItemsAdapter otherItemsAdapter;
    private ContactsAdapter contactsAdapter;
    private FarmaciAdapter medicinesAdapter;
    private LinearLayout intestazioneTabella;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kit_emergenza);

        intestazioneTabella = findViewById(R.id.intestazioneTabella);

        fab = findViewById(R.id.fab_edit_save);
        recyclerOther = findViewById(R.id.recycler_other_items);
        recyclerContacts = findViewById(R.id.recycler_contacts);
        recyclerMedicines = findViewById(R.id.recycler_medicines);

        // --- Carica la lista salvata dall'adapter ---
        List<String> otherItemsList = new OtherItemsAdapter(this, new ArrayList<>(),false).loadItems();
        List<Contatto> contactsList = new ContactsAdapter(this, new ArrayList<>(), false).loadContacts();
        List<Farmaco> medicinesList = new FarmaciAdapter(this, new ArrayList<>(), false).loadMedicines();
        otherItemsAdapter = new OtherItemsAdapter(this, otherItemsList, isEditing);
        contactsAdapter = new ContactsAdapter(this, contactsList, isEditing);
        medicinesAdapter = new FarmaciAdapter(this, medicinesList, isEditing);


        // --- Configura RecyclerView ---
        recyclerOther.setLayoutManager(new LinearLayoutManager(this));
        recyclerOther.setAdapter(otherItemsAdapter);

        recyclerContacts.setLayoutManager(new LinearLayoutManager(this));
        recyclerContacts.setAdapter(contactsAdapter);

        recyclerMedicines.setLayoutManager(new LinearLayoutManager(this));
        recyclerMedicines.setAdapter(medicinesAdapter);

        // Imposto header correttamente all'avvio
        updateIntestazione();

        // --- FAB: toggle edit/save ---
        fab.setOnClickListener(view -> {
            isEditing = !isEditing;  // toggle edit mode
            updateFabIcon();

            // Aggiorna gli adapter
            otherItemsAdapter.setEditing(isEditing);
            contactsAdapter.setEditing(isEditing);
            medicinesAdapter.setEditing(isEditing);


            // Se si sta salvando (uscendo da edit mode)
            if (!isEditing) {
                otherItemsAdapter.saveAll();
                contactsAdapter.saveAll();
                medicinesAdapter.saveAll();

                Log.d("KitEmergenzaActivity", "Lista elementi salvata (" + otherItemsAdapter.getItems().size() + " elementi)");
                Log.d("KitEmergenzaActivity", "Lista contatti salvata (" + contactsAdapter.getItems().size() + " elementi)");
                Log.d("KitEmergenzaActivity", "Lista farmaci salvata (" + medicinesAdapter.getItems().size() + " elementi)");
            }

            // Aggiorno intestazione dopo ogni toggle
            updateIntestazione();
        });

    }

    /** Mostra o nasconde l’intestazione in base allo stato attuale */
    private void updateIntestazione() {
        if (isEditing) {
            intestazioneTabella.setVisibility(View.VISIBLE);
        } else {
            if (!medicinesAdapter.getItems().isEmpty()) {
                intestazioneTabella.setVisibility(View.VISIBLE);
            } else {
                intestazioneTabella.setVisibility(View.GONE);
            }
        }
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
}
