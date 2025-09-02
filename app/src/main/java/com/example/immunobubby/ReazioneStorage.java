// ReazioneStorage.java
package com.example.immunobubby;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReazioneStorage {
    private static final String PREF_NAME = "reazioni_prefs";
    private static final String KEY_REAZIONI = "lista_reazioni";

    public static void saveReactions(Context context, List<Reazione> reazioni) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(reazioni);
        editor.putString(KEY_REAZIONI, json);
        editor.apply();
    }

    public static List<Reazione> loadReactions(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_REAZIONI, null);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Reazione>>() {}.getType();
        List<Reazione> lista = gson.fromJson(json, type);
        return lista != null ? lista : new ArrayList<>();
    }
}
