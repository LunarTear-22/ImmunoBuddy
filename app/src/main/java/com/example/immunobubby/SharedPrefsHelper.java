package com.example.immunobubby;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SharedPrefsHelper {
    private static final String PREFS_NAME = "kit_emergenza_prefs";
    private static final String KEY_FARMACI = "farmaci";
    private static final String KEY_OTHER = "other_items";
    private static final String KEY_CONTACTS = "contacts";
    private static final Gson gson = new Gson();

    // --- FARMACI ---
    public static void saveFarmaci(Context context, List<Farmaco> list) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_FARMACI, gson.toJson(list)).apply();
        Log.d("SharedPrefsHelper", "Lista farmaci salvata (" + list.size() + " elementi)");
    }

    public static List<Farmaco> loadFarmaci(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_FARMACI, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<Farmaco>>(){}.getType();
        List<Farmaco> list = gson.fromJson(json, type);
        Log.d("SharedPrefsHelper", "Caricati " + list.size() + " farmaci da SharedPreferences");
        return list;
    }

    // --- OTHER ITEMS ---
    public static void saveOtherItems(Context context, List<String> list) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_OTHER, gson.toJson(list)).apply();
        Log.d("SharedPrefsHelper", "Lista elementi salvata (" + list.size() + " elementi)");
    }

    public static List<String> loadOtherItems(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_OTHER, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<String>>(){}.getType();
        List<String> list = gson.fromJson(json, type);
        Log.d("SharedPrefsHelper", "Caricati " + list.size() + " elementi da SharedPreferences");
        return list;
    }

    // --- CONTACTS ---
    public static void saveContacts(Context context, List<Contatto> list) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_CONTACTS, gson.toJson(list)).apply();
        Log.d("SharedPrefsHelper", "Lista contatti salvata (" + list.size() + " elementi)");
    }

    public static List<Contatto> loadContacts(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_CONTACTS, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<Contatto>>(){}.getType();
        List<Contatto> list = gson.fromJson(json, type);
        Log.d("SharedPrefsHelper", "Caricati " + list.size() + " contatti da SharedPreferences");
        return list;
    }
}
