package com.example.immunobubby;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AllergeniActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private AllergeniAdapter adapter;
    private List<Allergene> allergeniSalvati;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allergeni);

        // Inizializza RecyclerView
        recyclerView = findViewById(R.id.allergens_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // layout verticale

        // Trova il FAB e aggiungi il click listener
        FloatingActionButton fabAdd = findViewById(R.id.btnAdd);
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(AllergeniActivity.this, CategorieAllergeniActivity.class);
            startActivity(intent);
        });

        caricaAllergeni();
    }

    private void caricaAllergeni() {
        SharedPreferences prefs = getSharedPreferences("AllergeniPrefs", MODE_PRIVATE);
        String json = prefs.getString("allergeni", "[]");

        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Allergene>>(){}.getType();
        allergeniSalvati = gson.fromJson(json, listType);

        if (allergeniSalvati == null) allergeniSalvati = new ArrayList<>();

        adapter = new AllergeniAdapter(allergeniSalvati);
        recyclerView.setAdapter(adapter);
    }


}
