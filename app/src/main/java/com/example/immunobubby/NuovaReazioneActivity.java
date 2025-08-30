package com.example.immunobubby;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NuovaReazioneActivity extends BaseActivity {

    private TextInputEditText dataEditText, oraEditText, allergeneEditText,
            sintomiEditText, farmaciEditText, noteEditText;
    private TextInputLayout dataLayout, oraLayout, allergeneLayout,
            gravitaLayout, sintomiLayout, farmaciLayout;
    private Spinner gravitaSpinner;
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
        gravitaSpinner = findViewById(R.id.gravita_spinner);
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

        setupGravitaSpinner();
    }

    private void setupGravitaSpinner() {
        String[] gravitaOptions = {"Seleziona gravità*", "Lieve", "Moderato", "Significativo", "Grave"};
        int[] gravitaColors = {
                android.R.color.transparent,
                R.color.gravity_mild,
                R.color.gravity_moderate,
                R.color.gravity_significant,
                R.color.gravity_severe
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, gravitaOptions) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;

                if (position == 0) {
                    textView.setTextColor(ContextCompat.getColor(getContext(), R.color.text_dark_transparent));
                } else {
                    textView.setTextColor(ContextCompat.getColor(getContext(), R.color.text_dark));

                    // Add colored dot
                    GradientDrawable dot = new GradientDrawable();
                    dot.setShape(GradientDrawable.OVAL);
                    dot.setColor(ContextCompat.getColor(getContext(), gravitaColors[position]));
                    dot.setSize(24, 24);

                    textView.setCompoundDrawablesWithIntrinsicBounds(dot, null, null, null);
                    textView.setCompoundDrawablePadding(16);
                }

                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;

                if (position == 0) {
                    textView.setTextColor(ContextCompat.getColor(getContext(), R.color.text_dark_transparent));
                } else {
                    textView.setTextColor(ContextCompat.getColor(getContext(), R.color.text_dark));

                    // Add colored dot
                    GradientDrawable dot = new GradientDrawable();
                    dot.setShape(GradientDrawable.OVAL);
                    dot.setColor(ContextCompat.getColor(getContext(), gravitaColors[position]));
                    dot.setSize(24, 24);

                    textView.setCompoundDrawablesWithIntrinsicBounds(dot, null, null, null);
                    textView.setCompoundDrawablePadding(16);
                }

                textView.setPadding(16, 16, 16, 16);
                return view;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gravitaSpinner.setAdapter(adapter);
    }

    private void setupClickListeners() {
        dataEditText.setOnClickListener(v -> showDatePicker());
        oraEditText.setOnClickListener(v -> showTimePicker());

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
        setupGravitaSpinnerListener();
        setupFieldWatcher(sintomiEditText, sintomiLayout);
        setupFieldWatcher(farmaciEditText, farmaciLayout);
    }

    private void setupGravitaSpinnerListener() {
        gravitaSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                boolean isFilled = position > 0; // First item is placeholder
                updateFieldBackground(gravitaLayout, isFilled);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                updateFieldBackground(gravitaLayout, false);
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
        String gravitaText = gravitaSpinner.getSelectedItemPosition() > 0 ?
                gravitaSpinner.getSelectedItem().toString() : "";
        String sintomiText = getTextSafely(sintomiEditText);

        // Validate required fields
        if (allergeneText.isEmpty()) {
            allergeneEditText.setError("Campo obbligatorio");
            return;
        }

        if (gravitaText.isEmpty() || gravitaSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Seleziona la gravità", Toast.LENGTH_SHORT).show();
            return;
        }

        if (sintomiText.isEmpty()) {
            sintomiEditText.setError("Campo obbligatorio");
            return;
        }

        String dataText = getTextSafely(dataEditText);
        if (!dataText.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date parsedDate = sdf.parse(dataText);
                currentReazione.setData(parsedDate);
            } catch (ParseException e) {
                dataEditText.setError("Formato data non valido");
                return;
            }
        }

        currentReazione.setOra(getTextSafely(oraEditText));

        currentReazione.setAllergene(allergeneText);

        currentReazione.setGravita(gravitaText);

        ArrayList<String> sintomiList = new ArrayList<>();
        if (!sintomiText.isEmpty()) {
            sintomiList.addAll(Arrays.asList(sintomiText.split(",\\s*")));
        }
        currentReazione.setSintomi(sintomiList);

        String farmaciText = getTextSafely(farmaciEditText);
        ArrayList<String> farmaciList = new ArrayList<>();
        if (!farmaciText.isEmpty()) {
            farmaciList.addAll(Arrays.asList(farmaciText.split(",\\s*")));
        }
        currentReazione.setFarmaci(farmaciList);

        currentReazione.setNote(getTextSafely(noteEditText));

        // Get radio button selection
        int selectedRadioId = medicoRadioGroup.getCheckedRadioButtonId();
        boolean medicoContattato = selectedRadioId == R.id.si_radio_button;
        currentReazione.setContattoMedico(medicoContattato);

        saveReazioneToDatabase(currentReazione);

        Toast.makeText(this, "Reazione salvata!", Toast.LENGTH_SHORT).show();

        // 🔹 Dopo il salvataggio apri la lista delle reazioni
        Intent intent = new Intent(this, ReazioniAllergicheActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void populateFormFromReazione(Reazione reazione) {
        if (reazione == null) return;

        if (reazione.getData() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            dataEditText.setText(sdf.format(reazione.getData()));
        }
        if (reazione.getOra() != null) {
            oraEditText.setText(reazione.getOra());
        }

        if (reazione.getAllergene() != null) {
            allergeneEditText.setText(reazione.getAllergene());
        }

        if (reazione.getGravita() != null) {
            String[] gravitaOptions = {"Seleziona gravità*", "Lieve", "Moderato", "Significativo", "Grave"};
            for (int i = 0; i < gravitaOptions.length; i++) {
                if (gravitaOptions[i].equals(reazione.getGravita())) {
                    gravitaSpinner.setSelection(i);
                    break;
                }
            }
        }

        if (reazione.getSintomi() != null && !reazione.getSintomi().isEmpty()) {
            sintomiEditText.setText(String.join(", ", reazione.getSintomi()));
        }
        if (reazione.getFarmaci() != null && !reazione.getFarmaci().isEmpty()) {
            farmaciEditText.setText(String.join(", ", reazione.getFarmaci()));
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
                ", Allergene: " + reazione.getAllergene() +
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
