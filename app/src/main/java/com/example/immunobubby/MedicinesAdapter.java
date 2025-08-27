package com.example.immunobubby;

import android.content.Context;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
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

public class MedicinesAdapter extends RecyclerView.Adapter<MedicinesAdapter.MedicineViewHolder> {

    private List<MedicineReminder> reminders;
    private OnTimeClickListener timeClickListener;

    public interface OnTimeClickListener {
        void onTimeClick(int position, MaterialButton btnTime);
    }

    public MedicinesAdapter(List<MedicineReminder> reminders, OnTimeClickListener listener) {
        this.reminders = reminders;
        this.timeClickListener = listener;
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
        MedicineReminder reminder = reminders.get(position);

        holder.txtName.setText(reminder.getName());
        holder.btnTime.setText(String.format("%02d:%02d", reminder.getHour(), reminder.getMinute()));

        // Espansione card + sezione interna con animazione
        holder.expandButton.setOnClickListener(v -> {
            boolean isExpanded = holder.expandedSection.getVisibility() == View.VISIBLE;

            TransitionManager.beginDelayedTransition(holder.card, new AutoTransition().setDuration(300));

            if (isExpanded) {
                holder.expandedSection.setVisibility(View.GONE);
                holder.expandButton.animate().rotation(0f).setDuration(300).start();
            } else {
                holder.expandedSection.setVisibility(View.VISIBLE);
                holder.expandButton.animate().rotation(180f).setDuration(300).start();
            }
        });

        // Click su ora
        holder.btnTime.setOnClickListener(v -> {
            if (timeClickListener != null) {
                timeClickListener.onTimeClick(position, holder.btnTime);
            }
        });

        // Aggiorna chip selezionati dai giorni
        for (int i = 0; i < holder.chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) holder.chipGroup.getChildAt(i);
            int day = mapChipToDayOfWeek(chip.getId());
            chip.setChecked(reminder.getDays().contains(day));

            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    if (!reminder.getDays().contains(day)) reminder.getDays().add(day);
                } else {
                    reminder.getDays().remove((Integer) day);
                }
            });
        }

        // Chiudi tastiera al click esterno
        holder.card.setOnClickListener(v -> {
            if (holder.txtName.hasFocus()) {
                holder.txtName.clearFocus();
                hideKeyboard(holder.txtName);
            }
        });

        holder.switchButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            reminder.setActive(isChecked); // aggiorna lo stato del promemoria
        });

    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    // Metodo helper per nascondere tastiera
    private void hideKeyboard(View view) {
        if (view == null) return;
        Context context = view.getContext();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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

            // Disabilita linea sottostante del TextInputEditText
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
