package com.example.immunobubby;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends BaseActivity {

    private static final int SPLASH_DURATION = 3000; // 3 secondi

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome); // Layout con il messaggio di benvenuto

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(WelcomeActivity.this, AccountDataActivity.class);
            intent.putExtra("hideNavbar", true); // flag
            startActivity(intent);
            finish();
        }, SPLASH_DURATION);

    }
}
