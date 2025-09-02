package com.example.immunobubby;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SintomiStorage {
    private static final String PREFS_NAME = "sintomi_prefs";
    private static final String KEY_SINTOMI = "sintomi_list";

    /** Salva lista */
    public static void saveSintomi(Context context, List<Sintomi> sintomiList) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(sintomiList);
        editor.putString(KEY_SINTOMI, json);
        editor.apply();
    }

    /** Carica lista */
    public static List<Sintomi> loadSintomi(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_SINTOMI, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Sintomi>>() {}.getType();
        return gson.fromJson(json, type);
    }
}
