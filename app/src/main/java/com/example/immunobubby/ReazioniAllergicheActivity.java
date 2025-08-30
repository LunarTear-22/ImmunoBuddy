package com.example.immunobubby;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ReazioniAllergicheActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReazioneAdapter adapter;
    private List<Reazione> reactionsList;
    private FloatingActionButton fabAddReaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reazioni_allergiche);

        initViews();
        setupRecyclerView();
        loadReactions();
        setupClickListeners();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.reactions_recycler);
        fabAddReaction = findViewById(R.id.btnAddReaction);
    }

    private void setupRecyclerView() {
        reactionsList = new ArrayList<>();
        adapter = new ReazioneAdapter(this, reactionsList);
        adapter.setOnReactionClickListener(this::onReactionClick);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadReactions() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        try {
            Reazione reaction1 = new Reazione();
            reaction1.setData(dateFormat.parse("17/08/2025"));
            reaction1.setAllergene("pistacchi");
            reaction1.setGravita("Media");
            reaction1.addSintomo("Gonfiore");
            reactionsList.add(reaction1);

            Reazione reaction2 = new Reazione();
            reaction2.setData(dateFormat.parse("05/04/2025"));
            reaction2.setAllergene("pollini");
            reaction2.setGravita("Lieve");
            reaction2.addSintomo("Rinite");
            reactionsList.add(reaction2);

            Reazione reaction3 = new Reazione();
            reaction3.setData(dateFormat.parse("18/04/2025"));
            reaction3.setAllergene("acari");
            reaction3.setGravita("Grave");
            reaction3.addSintomo("Asma");
            reactionsList.add(reaction3);

            Reazione reaction4 = new Reazione();
            reaction4.setData(dateFormat.parse("27/04/2025"));
            reaction4.setAllergene("allergeni vari");
            reaction4.setGravita("Lieve");
            reaction4.addSintomo("Congiuntivite");
            reactionsList.add(reaction4);

            Reazione reaction5 = new Reazione();
            reaction5.setData(dateFormat.parse("06/05/2025"));
            reaction5.setAllergene("frutta secca");
            reaction5.setGravita("Media");
            reaction5.addSintomo("Prurito orale");
            reactionsList.add(reaction5);

            Reazione reaction6 = new Reazione();
            reaction6.setData(dateFormat.parse("20/05/2025"));
            reaction6.setAllergene("antibiotico");
            reaction6.setGravita("Grave");
            reaction6.addSintomo("Anafilassi");
            reactionsList.add(reaction6);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        adapter.notifyDataSetChanged();
    }

    private void setupClickListeners() {
        fabAddReaction.setOnClickListener(v -> {
            Intent intent = new Intent(this, NuovaReazioneActivity.class);
            startActivityForResult(intent, 1001);
        });
    }

    private void onReactionClick(Reazione reaction) {
        Intent intent = new Intent(this, NuovaReazioneActivity.class);
        intent.putExtra("edit_mode", true);
        intent.putExtra("reaction_data", reaction);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            loadReactions();
        }
    }



}
