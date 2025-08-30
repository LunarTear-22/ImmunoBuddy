package com.example.immunobubby;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.core.graphics.drawable.DrawableCompat;

import java.util.Calendar;

public class NuovaReazioneActivity extends BaseActivity {

    private TextInputEditText dataEditText, oraEditText, allergeneEditText, sintomiEditText, farmacEditText, noteEditText;
    private AutoCompleteTextView gravitaDropdown;
    private TextInputLayout dataLayout, oraLayout, allergeneLayout, gravitaLayout, sintomiLayout, farmacLayout, noteLayout;
    private RadioGroup medicoRadioGroup;
    private RadioButton siRadioButton, noRadioButton;
    private Button aggiungiFotoButton;
    private FloatingActionButton saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuova_reazione);

        initializeViews();
        setupClickListeners();
        setupTextWatchers();
        setupGravitaDropdown();
    }

    private void initializeViews() {
        dataEditText = findViewById(R.id.data_edit_text);
        oraEditText = findViewById(R.id.ora_edit_text);
        allergeneEditText = findViewById(R.id.allergene_edit_text);
        gravitaDropdown = findViewById(R.id.gravita_dropdown);
        sintomiEditText = findViewById(R.id.sintomi_edit_text);
        farmacEditText = findViewById(R.id.farmaci_edit_text);
        noteEditText = findViewById(R.id.note_edit_text);

        dataLayout = findViewById(R.id.data_layout);
        oraLayout = findViewById(R.id.ora_layout);
        allergeneLayout = findViewById(R.id.allergene_layout);
        gravitaLayout = findViewById(R.id.gravita_layout);
        sintomiLayout = findViewById(R.id.sintomi_layout);
        farmacLayout = findViewById(R.id.farmaci_layout);
        noteLayout = findViewById(R.id.note_layout);

        medicoRadioGroup = findViewById(R.id.medico_radio_group);
        siRadioButton = findViewById(R.id.si_radio_button);
        noRadioButton = findViewById(R.id.no_radio_button);

        aggiungiFotoButton = findViewById(R.id.aggiungi_image_btn);
        saveButton = findViewById(R.id.btnCeck);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                    String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    dataEditText.setText(date);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (TimePicker view, int selectedHour, int selectedMinute) -> {
                    String time = String.format("%02d:%02d", selectedHour, selectedMinute);
                    oraEditText.setText(time);
                },
                hour, minute, true
        );
        timePickerDialog.show();
    }

    private static final int PICK_IMAGE_REQUEST = 1;

    private void selectPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                // Qui potresti mostrare la foto in un ImageView
                Toast.makeText(this, "Foto selezionata: " + selectedImageUri.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveReaction() {
        if (validateForm()) {
            // Qui puoi salvare i dati in un database locale o inviarli a un server
            Toast.makeText(this, "Reazione salvata con successo!", Toast.LENGTH_SHORT).show();

            // Vai alla pagina ReazioniAllergiche
            Intent intent = new Intent(NuovaReazioneActivity.this, ReazioniAllergicheActivity.class);
            startActivity(intent);

            finish(); // Chiudi l'activity corrente così non torni indietro con "back"
        } else {
            Toast.makeText(this, "Compila tutti i campi obbligatori!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupClickListeners() {
        dataEditText.setOnClickListener(v -> showDatePicker());
        oraEditText.setOnClickListener(v -> showTimePicker());
        gravitaDropdown.setOnClickListener(v -> showGravitaDialog());
        aggiungiFotoButton.setOnClickListener(v -> selectPhoto());
        saveButton.setOnClickListener(v -> saveReaction());
    }

    private void setupTextWatchers() {
        setupFieldWatcher(dataEditText, dataLayout);
        setupFieldWatcher(oraEditText, oraLayout);
        setupFieldWatcher(allergeneEditText, allergeneLayout);
        setupFieldWatcher(sintomiEditText, sintomiLayout);
        setupFieldWatcher(farmacEditText, farmacLayout);
        setupFieldWatcher(noteEditText, noteLayout);

        // Setup gravity dropdown watcher
        gravitaDropdown.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                updateFieldBackground(gravitaLayout, !s.toString().trim().isEmpty());
            }
        });
    }

    private void setupFieldWatcher(TextInputEditText editText, TextInputLayout layout) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                updateFieldBackground(layout, !s.toString().trim().isEmpty());
            }
        });
    }

    private void updateFieldBackground(TextInputLayout layout, boolean isFilled) {
        if (isFilled) {
            layout.setBoxBackgroundColor(ContextCompat.getColor(this, R.color.primary_dark));
        } else {
            layout.setBoxBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        }
    }

    private void setupGravitaDropdown() {
        gravitaDropdown.setKeyListener(null);
        gravitaDropdown.setFocusable(false);
        gravitaDropdown.setClickable(true);
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    private void showGravitaDialog() {
        String[] gravitaOptions = {"Lieve", "Moderato", "Significativo", "Grave"};
        int[] gravitaColors = {
                R.color.gravity_mild,
                R.color.gravity_moderate,
                R.color.gravity_significant,
                R.color.gravity_severe
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleziona Gravità");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, gravitaOptions) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);

                Drawable base = ContextCompat.getDrawable(getContext(), R.drawable.circle_indicator);
                if (base != null) {
                    Drawable wrapped = DrawableCompat.wrap(base.mutate());
                    int color = ContextCompat.getColor(getContext(), gravitaColors[position]);
                    DrawableCompat.setTint(wrapped, color);

                    int size = dpToPx(12); // 12dp: regolalo a piacere
                    wrapped.setBounds(0, 0, size, size);
                    tv.setCompoundDrawables(wrapped, null, null, null);
                    tv.setCompoundDrawablePadding(dpToPx(8));
                }

                tv.setPadding(dpToPx(16), dpToPx(12), dpToPx(16), dpToPx(12));
                return tv;
            }
        };

        builder.setAdapter(adapter, (dialog, which) -> {
            gravitaDropdown.setText(gravitaOptions[which], false);
            dialog.dismiss();
        });

        builder.show();
    }

    private boolean validateForm() {
        boolean isValid = true;

        if (dataEditText.getText().toString().trim().isEmpty()) {
            dataLayout.setError("Campo obbligatorio");
            isValid = false;
        } else {
            dataLayout.setError(null);
        }

        if (allergeneEditText.getText().toString().trim().isEmpty()) {
            allergeneLayout.setError("Campo obbligatorio");
            isValid = false;
        } else {
            allergeneLayout.setError(null);
        }

        if (sintomiEditText.getText().toString().trim().isEmpty()) {
            sintomiLayout.setError("Campo obbligatorio");
            isValid = false;
        } else {
            sintomiLayout.setError(null);
        }

        return isValid;
    }
}
