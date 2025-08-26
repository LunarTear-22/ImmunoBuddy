package com.example.immunobubby;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SalvaAllergeneActivity extends BaseActivity {

    private TextView txtAllergeneScelto;
    private FloatingActionButton btnCeck;

    private ConstraintLayout background;
    private MaterialCardView customBanner;
    private ImageView bannerIcon;
    private TextView bannerMessage;
    private MaterialButton bannerAction;

    private String allergeneSelezionato;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salva_allergene);

        bindViews();

        // Recupero allergene dall'intent
        allergeneSelezionato = getIntent().getStringExtra("allergene_nome");
        txtAllergeneScelto.setText(
                allergeneSelezionato != null ? allergeneSelezionato : "Nessun allergene selezionato"
        );

        btnCeck.setOnClickListener(v -> salvaAllergeneHandler());
        bannerAction.setOnClickListener(v -> closeBannerAndGo());
    }

    private void bindViews() {
        txtAllergeneScelto = findViewById(R.id.txtAllergeneScelto);
        btnCeck = findViewById(R.id.btnCeck);

        background = findViewById(R.id.background);
        customBanner = findViewById(R.id.customBanner);
        bannerIcon = customBanner.findViewById(R.id.save_icon);
        bannerMessage = customBanner.findViewById(R.id.bannerMessage);
        bannerAction = customBanner.findViewById(R.id.bannerAction);
    }

    private void salvaAllergeneHandler() {
        if (allergeneSelezionato == null) {
            showBannerError("Seleziona un allergene prima di salvare");
            return;
        }

        boolean success = salvaAllergene(allergeneSelezionato);
        if (success) {
            showBannerSave("Allergene salvato con successo");
        } else {
            showBannerError("Errore durante il salvataggio");
        }

        // Timeout automatico dopo 3 secondi
        customBanner.postDelayed(this::closeBannerAndGo, 3000);
    }

    private boolean salvaAllergene(String allergene) {
        // TODO: implementa il salvataggio reale
        return true;
    }

    private void showBannerSave(String messaggio) {
        setupBanner(messaggio, R.color.text_dark, R.drawable.bookmark_check_24px,
                R.color.primary_dark, R.color.text_dark);
    }

    private void showBannerError(String messaggio) {
        setupBanner(messaggio, R.color.text_danger, R.drawable.dangerous_24px,
                R.color.danger, R.color.text_danger);
    }

    private void setupBanner(String messaggio, int textColor, int iconRes,
                             int backgroundColor, int strokeColor) {
        background.setVisibility(View.VISIBLE);
        bannerMessage.setText(messaggio);
        bannerMessage.setTextColor(getColor(textColor));
        bannerIcon.setImageResource(iconRes);
        customBanner.setCardBackgroundColor(getColor(backgroundColor));
        customBanner.setStrokeColor(getColor(strokeColor));
        customBanner.setStrokeWidth(2);
        customBanner.setVisibility(View.VISIBLE);
    }

    private void closeBannerAndGo() {
        customBanner.setVisibility(View.GONE);
        background.setVisibility(View.GONE);
        startActivity(new Intent(this, AllergeniActivity.class));
        finish();
    }
}
