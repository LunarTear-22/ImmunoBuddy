package com.example.immunobubby;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FarmaciActivity extends BaseActivity {

    private static final String TAG = "FarmaciActivity";

    private RecyclerView recyclerView;
    private MedicinesAdapter adapter;
    private List<MedicineReminder> reminders;
    private Gson gson = new Gson();
    private SharedPreferences prefs;

    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmaci);

        prefs = getSharedPreferences("medicines", MODE_PRIVATE);
        recyclerView = findViewById(R.id.medicines_recycler);
        FloatingActionButton btnAdd = findViewById(R.id.btnAdd);

        loadReminders();
        Log.d(TAG, "Reminders caricati: " + reminders.size());

        adapter = new MedicinesAdapter(reminders, this::showTimePicker, this::saveAllAndAlarms);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnAdd.setOnClickListener(v -> {
            reminders.add(new MedicineReminder());
            adapter.notifyItemInserted(reminders.size() - 1);
            recyclerView.scrollToPosition(reminders.size() - 1);
            Log.d(TAG, "Aggiunto nuovo farmaco, posizione: " + (reminders.size() - 1));
            saveAllAndAlarms();
        });

        ConstraintLayout rootLayout = findViewById(R.id.farmaci_root);
        rootLayout.setOnClickListener(v -> hideKeyboard());

        // Configuro il launcher per il permesso
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        Toast.makeText(this, "Permesso notifiche concesso", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Permesso notifiche negato", Toast.LENGTH_SHORT).show();
                    }
                });

        checkNotificationPermission();
    }

    private void cancelAlarm(MedicineReminder reminder, int requestCode) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        for (int dayOfWeek = 1; dayOfWeek <= 7; dayOfWeek++) {
            Intent intent = new Intent(this, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    requestCode * 10 + dayOfWeek,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            alarmManager.cancel(pendingIntent);
        }

        Log.d(TAG, "Allarme cancellato per " + reminder.getName());
    }


    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Richiedo il permesso
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }


    private void showTimePicker(int position, MaterialButton btnTime) {
        Calendar now = Calendar.getInstance();
        MaterialTimePicker picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(now.get(Calendar.HOUR_OF_DAY))
                .setMinute(now.get(Calendar.MINUTE))
                .setTitleText("Scegli l'orario")
                .build();

        picker.addOnPositiveButtonClickListener(v -> {
            int hour = picker.getHour();
            int minute = picker.getMinute();

            MedicineReminder reminder = reminders.get(position);
            reminder.setHour(hour);
            reminder.setMinute(minute);

            btnTime.setText(String.format("%02d:%02d", hour, minute));
            Log.d(TAG, "Orario aggiornato per posizione " + position + ": " + hour + ":" + minute);
            saveAllAndAlarms();
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
                Log.d(TAG, "Tastiera nascosta");
            }
        }
    }

    private void saveAllAndAlarms() {
        saveReminders();
        for (int i = 0; i < reminders.size(); i++) {
            MedicineReminder reminder = reminders.get(i);
            // Imposta l'allarme solo se il nome non è vuoto
            if (reminder.getName() != null && !reminder.getName().trim().isEmpty()) {
                setAlarm(reminder, i);
                Log.d("FarmaciActivity", "Allarme impostato per " + reminder.getName() +
                        " giorno(s): " + reminder.getDays() + " ore: " + reminder.getHour() + ":" + reminder.getMinute());
            } else {
                Log.d("FarmaciActivity", "Promemoria inattivo o nome vuoto per posizione " + i);
            }
        }
        Log.d("FarmaciActivity", "Salvataggio e aggiornamento allarmi completati");
    }


    private void saveReminders() {
        String json = gson.toJson(reminders);
        prefs.edit().putString("reminders_list", json).apply();
        Log.d(TAG, "Reminders salvati nel SharedPreferences: " + reminders.size());
    }

    private void loadReminders() {
        String json = prefs.getString("reminders_list", null);
        if (json != null) {
            Type type = new TypeToken<List<MedicineReminder>>() {}.getType();
            reminders = gson.fromJson(json, type);
        } else {
            reminders = new ArrayList<>();
        }
        Log.d(TAG, "Reminders caricati: " + reminders.size());
    }

    public void setAlarm(MedicineReminder reminder, int requestCode) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (alarmManager == null) return;

        // Controllo nome e giorni
        if (reminder.getName() == null || reminder.getName().trim().isEmpty() || reminder.getDays().isEmpty()) {
            Log.d(TAG, "Promemoria inattivo o dati incompleti per posizione " + requestCode);
            return;
        }

        // Cicla sui giorni selezionati
        for (int dayOfWeek : reminder.getDays()) {

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
            calendar.set(Calendar.HOUR_OF_DAY, reminder.getHour());
            calendar.set(Calendar.MINUTE, reminder.getMinute());
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            // Se l'orario è già passato, passa alla settimana successiva
            if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                calendar.add(Calendar.WEEK_OF_YEAR, 1);
            }

            Intent intent = new Intent(this, AlarmReceiver.class);
            intent.putExtra("name", reminder.getName());
            intent.putExtra("requestCode", requestCode);
            intent.putExtra("hour", reminder.getHour());
            intent.putExtra("minute", reminder.getMinute());
            intent.putExtra("dayOfWeek", dayOfWeek);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    requestCode * 10 + dayOfWeek, // requestCode univoco per ogni giorno
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            // Controllo permessi exact alarm su Android 12+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    Log.w(TAG, "Exact alarms non permessi, uso set normale");
                    alarmManager.set(
                            AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(),
                            pendingIntent
                    );
                    continue;
                }
            }

            try {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent
                );
                Log.d(TAG, "Allarme exact impostato: " + reminder.getName() +
                        " giorno: " + dayOfWeek + " ore: " + reminder.getHour() + ":" + reminder.getMinute());
            } catch (SecurityException e) {
                Log.e(TAG, "Exact alarm non permesso, uso fallback", e);
                alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent
                );
            }
        }
    }


}
