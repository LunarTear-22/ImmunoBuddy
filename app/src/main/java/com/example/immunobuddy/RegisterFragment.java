package com.example.immunobuddy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class RegisterFragment extends Fragment {
    public RegisterFragment() {
        super(R.layout.register_screen);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        EditText email = view.findViewById(R.id.emailRegister);
        EditText password = view.findViewById(R.id.passwordRegister);
        Button registerButton = view.findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> {
            String emailText = email.getText().toString();
            String passText = password.getText().toString();

            if (emailText.isEmpty() || passText.isEmpty()) {
                Toast.makeText(getActivity(), "Compila tutti i campi", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("email", emailText);
            editor.putString("password", passText);
            editor.apply();

            Toast.makeText(getActivity(), "Registrazione completata", Toast.LENGTH_SHORT).show();
            ((MainActivity) requireActivity()).showLogin();
        });
    }
}

