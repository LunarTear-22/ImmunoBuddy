package com.example.immunobubby;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Calendar;
import java.util.Locale;

public class NuovaReazioneActivity extends BaseActivity {

    private TextInputEditText dataEditText, oraEditText, allergeneEditText,
            gravitaEditText, sintomiEditText, farmaciEditText, noteEditText;
    private TextInputLayout dataLayout, oraLayout, allergeneLayout,
            gravitaLayout, sintomiLayout, farmaciLayout;
    private RadioGroup medicoRadioGroup;
    private RadioButton siRadioButton, noRadioButton;
    private FloatingActionButton btnCheck;
    private View aggiungiImageBtn;

    private Reazione currentReazione;

    private static final int AUTOCOMPLETE_REQUEST_ALLERGENE = 1001;
    private static final int AUTOCOMPLETE_REQUEST_SINTOMI = 1002;
    private static final int AUTOCOMPLETE_REQUEST_FARMACI = 1003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuova_reazione);

        currentReazione = new Reazione();

        initializeViews();
        setupClickListeners();
        setupTextWatchers();

        // Populate form from existing Reazione if editing
        Intent intent = getIntent();
        Reazione reazioneToEdit = intent.getParcelableExtra("REAZIONE");
        if (reazioneToEdit != null) {
            populateFormFromReazione(reazioneToEdit);
        }
    }

    private void initializeViews() {
        dataEditText = findViewById(R.id.data_edit_text);
        oraEditText = findViewById(R.id.ora_edit_text);
        allergeneEditText = findViewById(R.id.allergene_edit_text);
        gravitaEditText = findViewById(R.id.gravita_edit_text);
        sintomiEditText = findViewById(R.id.sintomi_edit_text);
        farmaciEditText = findViewById(R.id.farmaci_edit_text);
        noteEditText = findViewById(R.id.note_edit_text);

        dataLayout = findViewById(R.id.data_layout);
        oraLayout = findViewById(R.id.ora_layout);
        allergeneLayout = findViewById(R.id.allergene_layout);
        gravitaLayout = findViewById(R.id.gravita_layout);
        sintomiLayout = findViewById(R.id.sintomi_layout);
        farmaciLayout = findViewById(R.id.farmaci_layout);

        medicoRadioGroup = findViewById(R.id.medico_radio_group);
        siRadioButton = findViewById(R.id.si_radio_button);
        noRadioButton = findViewById(R.id.no_radio_button);
        btnCheck = findViewById(R.id.btnCeck);
        aggiungiImageBtn = findViewById(R.id.aggiungi_image_btn);
    }

    private void setupClickListeners() {
        dataEditText.setOnClickListener(v -> showDatePicker());
        oraEditText.setOnClickListener(v -> showTimePicker());
        gravitaEditText.setOnClickListener(v -> showGravitaOptions());

        allergeneEditText.setOnClickListener(v -> showFieldSearch("allergene"));
        sintomiEditText.setOnClickListener(v -> showFieldSearch("sintomi"));
        farmaciEditText.setOnClickListener(v -> showFieldSearch("farmaci"));

        aggiungiImageBtn.setOnClickListener(v -> {
            // Handle adding photo
            Toast.makeText(this, "Aggiungi foto", Toast.LENGTH_SHORT).show();
        });

        btnCheck.setOnClickListener(v -> saveReaction());
    }

    private void setupTextWatchers() {
        setupFieldWatcher(dataEditText, dataLayout);
        setupFieldWatcher(oraEditText, oraLayout);
        setupFieldWatcher(allergeneEditText, allergeneLayout);
        setupFieldWatcher(gravitaEditText, gravitaLayout);
        setupFieldWatcher(sintomiEditText, sintomiLayout);
        setupFieldWatcher(farmaciEditText, farmaciLayout);
        // Notes field excluded from color changes
    }

    private void showGravitaOptions() {
        String[] gravitaOptions = {"Lieve", "Moderato", "Significativo", "Grave"};
        int[] gravitaColors = {
                R.color.gravity_mild,
                R.color.gravity_moderate,
                R.color.gravity_significant,
                R.color.gravity_severe
        };

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);

        // Create custom adapter for colored options
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, gravitaOptions) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);

                view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.background_light));

                // Add colored dot before text
                GradientDrawable dot = new GradientDrawable();
                dot.setShape(GradientDrawable.OVAL);
                dot.setColor(ContextCompat.getColor(getContext(), gravitaColors[position]));
                dot.setSize(24, 24);

                textView.setCompoundDrawablesWithIntrinsicBounds(dot, null, null, null);
                textView.setCompoundDrawablePadding(16);
                textView.setPadding(16, 16, 16, 16);

                return view;
            }
        };

        builder.setAdapter(adapter, (dialog, which) -> gravitaEditText.setText(gravitaOptions[which]));
        builder.show();
    }

    private void setupFieldWatcher(TextInputEditText editText, TextInputLayout layout) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                boolean isFilled = !s.toString().trim().isEmpty();
                updateFieldBackground(layout, isFilled);
            }
        });
    }

    private void updateFieldBackground(TextInputLayout layout, boolean isFilled) {
        if (isFilled) {
            layout.setBackground(ContextCompat.getDrawable(this, R.drawable.filled_field_background));
        } else {
            layout.setBackground(ContextCompat.getDrawable(this, R.drawable.empty_field_background));
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String date = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year);
                    dataEditText.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                    oraEditText.setText(time);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private String getTextSafely(TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }

    private void saveReaction() {
        String allergeneText = getTextSafely(allergeneEditText);
        String gravitaText = getTextSafely(gravitaEditText);
        String sintomiText = getTextSafely(sintomiEditText);

        // Validate required fields
        if (allergeneText.isEmpty()) {
            allergeneEditText.setError("Campo obbligatorio");
            return;
        }

        if (gravitaText.isEmpty()) {
            gravitaEditText.setError("Campo obbligatorio");
            return;
        }

        if (sintomiText.isEmpty()) {
            sintomiEditText.setError("Campo obbligatorio");
            return;
        }

        currentReazione.setData(getTextSafely(dataEditText));
        currentReazione.setOra(getTextSafely(oraEditText));

        Allergene allergene = new Allergene(allergeneText, null);
        currentReazione.setAllergene(allergene);

        currentReazione.setGravita(gravitaText);
        currentReazione.setSintomi(sintomiText);
        currentReazione.setFarmaci(getTextSafely(farmaciEditText));
        currentReazione.setNote(getTextSafely(noteEditText));

        // Get radio button selection
        int selectedRadioId = medicoRadioGroup.getCheckedRadioButtonId();
        boolean medicoContattato = selectedRadioId == R.id.si_radio_button;
        currentReazione.setMedicoContattato(medicoContattato);

        saveReazioneToDatabase(currentReazione);

        Toast.makeText(this, "Reazione salvata!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void populateFormFromReazione(Reazione reazione) {
        if (reazione == null) return;

        if (reazione.getData() != null) {
            dataEditText.setText(reazione.getData());
        }
        if (reazione.getOra() != null) {
            oraEditText.setText(reazione.getOra());
        }

        if (reazione.getAllergene() != null && reazione.getAllergene().getNome() != null) {
            allergeneEditText.setText(reazione.getAllergene().getNome());
        }

        if (reazione.getGravita() != null) {
            gravitaEditText.setText(reazione.getGravita());
        }

        if (reazione.getSintomi() != null) {
            sintomiEditText.setText(reazione.getSintomi());
        }
        if (reazione.getFarmaci() != null) {
            farmaciEditText.setText(reazione.getFarmaci());
        }
        if (reazione.getNote() != null) {
            noteEditText.setText(reazione.getNote());
        }

        // Set radio button selection
        if (reazione.isMedicoContattato()) {
            siRadioButton.setChecked(true);
        } else {
            noRadioButton.setChecked(true);
        }
    }

    private void saveReazioneToDatabase(Reazione reazione) {
        // TODO: Implement database saving logic
        // This is where you would save the Reazione object to your database
        // For now, just log the data
        System.out.println("Saving Reazione: " +
                "Data: " + reazione.getData() +
                ", Allergene: " + (reazione.getAllergene() != null ? reazione.getAllergene().getNome() : "null") +
                ", Gravità: " + reazione.getGravita());
    }

    private void showFieldSearch(String fieldType) {
        // This will trigger the expandable search bar from BaseActivity
        // The search functionality is handled by setupExpandableSearchBar() in BaseActivity
        Toast.makeText(this, "Cerca " + fieldType, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
