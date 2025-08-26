package com.example.immunobubby;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

public class CategorieAllergeniActivity extends BaseActivity {

    private RecyclerView recyclerAllergeni;
    private CategorieAllergeniAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria_allergeni);

        FloatingActionButton btnCheck = findViewById(R.id.btnCeck);
        btnCheck.setEnabled(false);
        btnCheck.setAlpha(0.5f);

        recyclerAllergeni = findViewById(R.id.recyclerAllergeni);
        recyclerAllergeni.setLayoutManager(new LinearLayoutManager(this));

        // Legge i dati dal file JSON
        List<CategoriaAllergene> categorie = loadAllergeniFromJson();

        // Imposta l'adapter
        adapter = new CategorieAllergeniAdapter(this, categorie, recyclerAllergeni);
        recyclerAllergeni.setAdapter(adapter);

        // Collega la search bar al filtro
        TextInputEditText searchInput = findViewById(R.id.searchInput);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private List<CategoriaAllergene> loadAllergeniFromJson() {
        try {
            InputStreamReader reader = new InputStreamReader(getResources().openRawResource(R.raw.allergeni));
            Type listType = new TypeToken<List<CategoriaAllergene>>(){}.getType();
            return new Gson().fromJson(reader, listType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
