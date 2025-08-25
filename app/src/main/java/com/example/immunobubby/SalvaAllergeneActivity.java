package com.example.immunobubby;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
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

    private boolean bannerClosed = false; // previene doppio avvio dell'Intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salva_allergene);

        // Bind views
        txtAllergeneScelto = findViewById(R.id.txtAllergeneScelto);
        btnCeck = findViewById(R.id.btnCeck);
        btnCeck.setEnabled(false); // inizialmente disabilitato

        // Bind banner e background
        background = findViewById(R.id.background);
        background.setClickable(true);
        background.setFocusable(true);

        customBanner = findViewById(R.id.customBanner);
        bannerIcon = customBanner.findViewById(R.id.save_icon);
        bannerMessage = customBanner.findViewById(R.id.bannerMessage);
        bannerAction = customBanner.findViewById(R.id.bannerAction);

        // Recupero allergene dall'intent
        String allergeneSelezionato = getIntent().getStringExtra("allergene_nome");
        txtAllergeneScelto.setText(
                allergeneSelezionato != null ? allergeneSelezionato : "Nessun allergene selezionato"
        );

        // Simula completamento compilazione per abilitare bottone (da sostituire con logica reale)
        if (allergeneSelezionato != null) {
            btnCeck.setEnabled(true);
        }

        btnCeck.setOnClickListener(v -> {
            if (allergeneSelezionato != null) {
                boolean success = salvaAllergene(allergeneSelezionato);
                if (success) {
                    showBannerSave("Allergene salvato con successo");
                } else {
                    showBannerError("Errore durante il salvataggio");
                }
            } else {
                showBannerError("Seleziona un allergene prima di salvare");
            }

            // Click sul banner
            bannerAction.setOnClickListener(v2 -> closeBannerAndGo());

            // Timeout automatico dopo 3 secondi
            customBanner.postDelayed(this::closeBannerAndGo, 3000);
        });
    }

    private boolean salvaAllergene(String allergene) {
        // TODO: implementa il salvataggio reale
        return true;
    }

    private void showBannerSave(String messaggio) {
        background.setVisibility(View.VISIBLE);
        customBanner.setVisibility(View.VISIBLE);

        bannerMessage.setText(messaggio);
        bannerMessage.setTextColor(getColor(R.color.text_dark));
        bannerIcon.setImageResource(R.drawable.bookmark_check_24px);
        customBanner.setCardBackgroundColor(getColor(R.color.primary_dark));
        customBanner.setStrokeColor(getColor(R.color.text_dark));
        customBanner.setStrokeWidth(2);
    }

    private void showBannerError(String messaggio) {
        background.setVisibility(View.VISIBLE);
        customBanner.setVisibility(View.VISIBLE);

        bannerMessage.setText(messaggio);
        bannerMessage.setTextColor(getColor(R.color.text_danger));
        bannerIcon.setImageResource(R.drawable.dangerous_24px);
        bannerAction.setIconTintResource(R.color.text_danger);
        customBanner.setCardBackgroundColor(getColor(R.color.danger));
        customBanner.setStrokeColor(getColor(R.color.text_danger));
        customBanner.setStrokeWidth(2);
    }

    private void closeBannerAndGo() {
        if (bannerClosed) return; // previene doppio avvio
        bannerClosed = true;

        customBanner.setVisibility(View.GONE);
        if (background != null) background.setVisibility(View.GONE);

        Intent intent = new Intent(SalvaAllergeneActivity.this, AllergeniActivity.class);
        startActivity(intent);
    }
}
