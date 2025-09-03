package com.example.immunobubby;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MedicinesAdapter extends RecyclerView.Adapter<MedicinesAdapter.MedicineViewHolder> {

    private static final String TAG = "FarmaciAdapter";

    private final List<MedicineReminder> reminders;
    private final OnTimeClickListener timeClickListener;
    private final Runnable saveCallback;

    public interface OnTimeClickListener {
        void onTimeClick(int position, MaterialButton btnTime);
    }

    public MedicinesAdapter(List<MedicineReminder> reminders, OnTimeClickListener listener, Runnable saveCallback) {
        this.reminders = reminders;
        this.timeClickListener = listener;
        this.saveCallback = saveCallback;
    }

    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_medicine_card, parent, false);
        return new MedicineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineViewHolder holder, int position) {
        int adapterPosition = holder.getAdapterPosition();
        if (adapterPosition == RecyclerView.NO_POSITION) return;

        AtomicReference<MedicineReminder> reminder = new AtomicReference<>(reminders.get(adapterPosition));

        holder.txtName.setText(reminder.get().getName());
        Log.d(TAG, "[Bind] Posizione " + adapterPosition + ", nome iniziale: '" + reminder.get().getName() + "'");

        // --- Handler per debounce ---
        final Handler handler = new Handler();
        final Runnable[] saveRunnable = new Runnable[1];

        holder.txtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                reminder.get().setName(s.toString());
                Log.d(TAG, "[TextChanged] Posizione " + adapterPosition + ", nome aggiornato: '" + s + "'");

                // Rimuove eventuale salvataggio precedente
                if (saveRunnable[0] != null) handler.removeCallbacks(saveRunnable[0]);

                // Programma il salvataggio dopo 500 ms di inattività
                saveRunnable[0] = () -> {
                    saveCallback.run();
                    Log.d(TAG, "[DebounceSave] Nome salvato: '" + reminder.get().getName() + "' (posizione " + adapterPosition + ")");
                };
                handler.postDelayed(saveRunnable[0], 500);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // --- Resto del bind invariato ---
        holder.btnTime.setText(String.format("%02d:%02d", reminder.get().getHour(), reminder.get().getMinute()));
        holder.btnTime.setOnClickListener(v -> {
            if (timeClickListener != null) timeClickListener.onTimeClick(adapterPosition, holder.btnTime);
            Log.d(TAG, "[TimeClick] Click su ora (posizione " + adapterPosition + ")");
        });

        // Espansione card
        holder.expandButton.setOnClickListener(v -> {
            boolean isExpanded = holder.expandedSection.getVisibility() == View.VISIBLE;
            TransitionManager.beginDelayedTransition(holder.card, new AutoTransition().setDuration(300));
            holder.expandedSection.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
            holder.expandButton.animate().rotation(isExpanded ? 0f : 180f).setDuration(300).start();
            Log.d(TAG, "[ExpandClick] Card " + (isExpanded ? "chiusa" : "aperta") + " (posizione " + adapterPosition + ")");
        });

        // Chip giorni
        for (int i = 0; i < holder.chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) holder.chipGroup.getChildAt(i);
            int day = mapChipToDayOfWeek(chip.getId());

            chip.setOnCheckedChangeListener(null);
            chip.setChecked(reminder.get().getDays().contains(day));

            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    if (!reminder.get().getDays().contains(day)) reminder.get().getDays().add(day);
                    Log.d(TAG, "[ChipChecked] Giorno aggiunto: " + day + " (posizione " + adapterPosition + ")");
                } else {
                    reminder.get().getDays().remove((Integer) day);
                    Log.d(TAG, "[ChipChecked] Giorno rimosso: " + day + " (posizione " + adapterPosition + ")");
                }
                saveCallback.run();
            });
        }

        // Switch attivo
        holder.switchButton.setOnCheckedChangeListener(null);
        holder.switchButton.setChecked(reminder.get().isActive());
        holder.switchButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            reminder.set(reminders.get(pos));

            if (isChecked) {
                // Attivazione → salvo subito
                reminder.get().setActive(true);
                saveCallback.run();
                Log.d(TAG, "[Switch] Attivato promemoria: " + reminder.get().getName());
            } else {
                // Disattivazione → chiedo conferma
                new AlertDialog.Builder(holder.itemView.getContext())
                        .setTitle("Disattiva promemoria")
                        .setMessage("Vuoi davvero disattivare il promemoria per " + reminder.get().getName() + "?")
                        .setPositiveButton("Conferma", (dialog, which) -> {
                            reminder.get().setActive(false);
                            saveCallback.run(); // salva subito con stato disattivo
                            Log.d(TAG, "[Switch] Disattivato promemoria: " + reminder.get().getName());
                        })
                        .setNegativeButton("Annulla", (dialog, which) -> {
                            // Ripristino switch su ON
                            buttonView.setChecked(true);
                        })
                        .show();
            }
        });
        holder.switchButton.setChecked(reminder.get().isActive());

        // Chiudi tastiera al click esterno
        holder.card.setOnClickListener(v -> {
            if (holder.txtName.hasFocus()) {
                holder.txtName.clearFocus();
                hideKeyboard(holder.txtName);
            }
        });
    }




    @Override
    public int getItemCount() {
        return reminders.size();
    }

    private void hideKeyboard(View view) {
        if (view == null) return;
        Context context = view.getContext();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static class MedicineViewHolder extends RecyclerView.ViewHolder {
        TextInputEditText txtName;
        MaterialButton btnTime;
        ImageButton expandButton;
        View expandedSection;
        ChipGroup chipGroup;
        ViewGroup card;
        MaterialSwitch switchButton;

        public MedicineViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.cardMedicine);
            txtName = itemView.findViewById(R.id.txtMedicineName);
            btnTime = itemView.findViewById(R.id.btnTime);
            expandButton = itemView.findViewById(R.id.expandButton);
            expandedSection = itemView.findViewById(R.id.expandedSection);
            chipGroup = itemView.findViewById(R.id.chipGroupDays);
            switchButton = itemView.findViewById(R.id.switchButton);
            txtName.setBackground(null);
        }
    }

    private int mapChipToDayOfWeek(int chipId) {
        if (chipId == R.id.chipSun) return Calendar.SUNDAY;
        else if (chipId == R.id.chipMon) return Calendar.MONDAY;
        else if (chipId == R.id.chipTue) return Calendar.TUESDAY;
        else if (chipId == R.id.chipWed) return Calendar.WEDNESDAY;
        else if (chipId == R.id.chipThu) return Calendar.THURSDAY;
        else if (chipId == R.id.chipFri) return Calendar.FRIDAY;
        else if (chipId == R.id.chipSat) return Calendar.SATURDAY;
        else return Calendar.MONDAY;
    }
}
