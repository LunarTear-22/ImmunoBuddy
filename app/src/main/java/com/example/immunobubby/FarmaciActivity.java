package com.example.immunobubby;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FarmaciActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private MedicinesAdapter adapter;
    private List<MedicineReminder> reminders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmaci);

        recyclerView = findViewById(R.id.medicines_recycler);
        FloatingActionButton btnAdd = findViewById(R.id.btnAdd);

        reminders = new ArrayList<>();
        adapter = new MedicinesAdapter(reminders, this::showTimePicker);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnAdd.setOnClickListener(v -> {
            reminders.add(new MedicineReminder());
            adapter.notifyItemInserted(reminders.size() - 1);
            recyclerView.scrollToPosition(reminders.size() - 1);
        });

        // Chiudi tastiera al click ovunque nel root layout
        ConstraintLayout rootLayout = findViewById(R.id.farmaci_root);
        rootLayout.setOnClickListener(v -> hideKeyboard());
    }

    private void showTimePicker(int position, MaterialButton btnTime) {
        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);

        MaterialTimePicker picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(hour)
                .setMinute(minute)
                .setTitleText("Scegli l'orario")
                .setPositiveButtonText("Imposta il promemoria")
                .build();

        picker.addOnPositiveButtonClickListener(v -> {
            int selectedHour = picker.getHour();
            int selectedMinute = picker.getMinute();

            MedicineReminder reminder = reminders.get(position);
            reminder.setHour(selectedHour);
            reminder.setMinute(selectedMinute);

            btnTime.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
        });

        picker.show(getSupportFragmentManager(), "TIME_PICKER");
    }

    private void hideKeyboard() {
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                currentFocus.clearFocus();
            }
        }
    }
}
