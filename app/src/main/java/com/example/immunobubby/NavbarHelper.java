package com.example.immunobubby;


import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.example.immunobubby.R;  // Assicurati che il package sia corretto
// import com.example.immunobubby.AlertActivity;
import com.example.immunobubby.MainActivity;
import com.example.immunobubby.ImpostazioniAccountActivity;

public class NavbarHelper {

    /**
     * Inizializza i pulsanti della navbar per l'Activity corrente
     * @param activity L'activity che include il layout navbar
     */
    public static void setupNavbar(Activity activity) {

        // Bottone Alert
        MaterialButton btnAlert = activity.findViewById(R.id.nav_alert);
       /* if (btnAlert != null) {
            btnAlert.setOnClickListener(v -> {
                Intent intent = new Intent(activity, AlertActivity.class);
                activity.startActivity(intent);
                activity.overridePendingTransition(0,0); // senza animazione
            });
        }*/

        // Bottone Home
        MaterialButton btnHome = activity.findViewById(R.id.nav_home);
        if (btnHome != null) {
            btnHome.setOnClickListener(v -> {
                Intent intent = new Intent(activity, MainActivity.class);
                activity.startActivity(intent);
                activity.overridePendingTransition(0,0);
            });
        }

        // Bottone Account
        MaterialButton btnAccount = activity.findViewById(R.id.btnAccountNav);
        if (btnAccount != null) {
            btnAccount.setOnClickListener(v -> {
                Intent intent = new Intent(activity, ImpostazioniAccountActivity.class);
                activity.startActivity(intent);
                activity.overridePendingTransition(0,0);
            });
        }
    }
}
