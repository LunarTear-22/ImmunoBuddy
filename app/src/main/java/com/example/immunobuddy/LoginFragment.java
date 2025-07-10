package com.example.immunobuddy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.google.android.gms.auth.api.signin.GoogleSignInClient;

public class LoginFragment extends Fragment {
    public LoginFragment() {
        super(R.layout.login_screen);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        EditText email = view.findViewById(R.id.emailLogin);
        EditText password = view.findViewById(R.id.passwordLogin);
        Button loginButton = view.findViewById(R.id.loginButton);
        TextView toRegister = view.findViewById(R.id.toRegister);
        Button googleLogin = view.findViewById(R.id.googleLoginButton);
        Button appleLogin = view.findViewById(R.id.appleLoginButton);

        loginButton.setOnClickListener(v -> {
            String emailText = email.getText().toString();
            String passText = password.getText().toString();

            if (emailText.isEmpty() || passText.isEmpty()) {
                Toast.makeText(getActivity(), "Inserisci email e password", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            String savedEmail = prefs.getString("email", "");
            String savedPassword = prefs.getString("password", "");

            if (emailText.equals(savedEmail) && passText.equals(savedPassword)) {
                Toast.makeText(getActivity(), "Login avvenuto con successo", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Credenziali errate", Toast.LENGTH_SHORT).show();
            }
        });

        toRegister.setOnClickListener(v -> ((MainActivity) requireActivity()).showRegister());

        googleLogin.setOnClickListener(v -> {
            MainActivity activity = (MainActivity) requireActivity();
            Intent signInIntent = activity.googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, MainActivity.RC_SIGN_IN);
        });

        appleLogin.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Apple Sign-In non è ancora disponibile su Android", Toast.LENGTH_SHORT).show();
        });
    }
}
