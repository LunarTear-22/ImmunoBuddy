package com.example.immunobuddy;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import android.content.Intent;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    FragmentManager fragmentManager;
    public static final int RC_SIGN_IN = 9001;
    public GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        fragmentManager.beginTransaction()
                .replace(R.id.container, new LoginFragment())
                .commit();
    }

    public void showRegister() {
        fragmentManager.beginTransaction()
                .replace(R.id.container, new RegisterFragment())
                .commit();
    }

    public void showLogin() {
        fragmentManager.beginTransaction()
                .replace(R.id.container, new LoginFragment())
                .commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            try {
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                Toast.makeText(this, "Accesso Google riuscito: " + account.getEmail(), Toast.LENGTH_SHORT).show();
            } catch (ApiException e) {
                Toast.makeText(this, "Accesso Google fallito", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
