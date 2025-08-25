package com.example.immunobubby;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

public class CategorieAllergeniActivity extends BaseActivity {

    private RecyclerView recyclerAllergeni;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria_allergeni);

        recyclerAllergeni = findViewById(R.id.recyclerAllergeni);
        recyclerAllergeni.setLayoutManager(new LinearLayoutManager(this));

        // Legge i dati dal file JSON
        List<CategoriaAllergene> categorie = loadAllergeniFromJson();

        // Imposta l'adapter
        CategorieAllergeniAdapter adapter = new CategorieAllergeniAdapter(this, categorie);
        recyclerAllergeni.setAdapter(adapter);
    }

    private List<CategoriaAllergene> loadAllergeniFromJson() {
        try {
            InputStreamReader reader = new InputStreamReader(getResources().openRawResource(R.raw.allergeni));
            Type listType = new TypeToken<List<CategoriaAllergene>>(){}.getType();
            return new Gson().fromJson(reader, listType);
        } catch (Exception e) {
            e.printStackTrace();
            return null; // o Collections.emptyList()
        }
    }
}
