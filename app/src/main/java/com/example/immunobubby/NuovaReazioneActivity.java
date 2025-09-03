package com.example.immunobubby;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class NuovaReazioneActivity extends BaseActivity {

    private TextInputEditText dataEditText, oraEditText, allergeneEditText, sintomiEditText, farmacEditText, noteEditText;
    private AutoCompleteTextView gravitaDropdown;
    private TextInputLayout dataLayout, oraLayout, allergeneLayout, sintomiLayout, farmacLayout, noteLayout, gravitaLayout;
    private RadioGroup medicoRadioGroup;
    private RadioButton siRadioButton, noRadioButton;
    private Button aggiungiFotoButton;
    private FloatingActionButton saveButton;
    private ImageView imageView;

    private String savedPhotoPath = null;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuova_reazione);

        initializeViews();
        setupClickListeners();
        setupGravitaDropdown();
        setupGravitaAdapter();
        setupDropdownDividerColor(gravitaDropdown);
    }

    private void initializeViews() {
        dataEditText = findViewById(R.id.data_edit_text);
        oraEditText = findViewById(R.id.ora_edit_text);
        allergeneEditText = findViewById(R.id.allergene_edit_text);
        gravitaDropdown = findViewById(R.id.dropdown);
        sintomiEditText = findViewById(R.id.sintomi_edit_text);
        farmacEditText = findViewById(R.id.farmaci_edit_text);
        noteEditText = findViewById(R.id.note_edit_text);

        dataLayout = findViewById(R.id.data_layout);
        oraLayout = findViewById(R.id.ora_layout);
        allergeneLayout = findViewById(R.id.allergene_layout);
        sintomiLayout = findViewById(R.id.sintomi_layout);
        farmacLayout = findViewById(R.id.farmaci_layout);
        noteLayout = findViewById(R.id.note_layout);
        gravitaLayout = findViewById(R.id.inputGravitaLayout);

        medicoRadioGroup = findViewById(R.id.medico_radio_group);
        siRadioButton = findViewById(R.id.si_radio_button);
        noRadioButton = findViewById(R.id.no_radio_button);

        aggiungiFotoButton = findViewById(R.id.aggiungi_image_btn);
        saveButton = findViewById(R.id.btnCeck);
        imageView = findViewById(R.id.imageShow);
    }

    private void setupClickListeners() {
        dataEditText.setOnClickListener(v -> showDatePicker());
        oraEditText.setOnClickListener(v -> showTimePicker());
        aggiungiFotoButton.setOnClickListener(v -> selectPhoto());
        saveButton.setOnClickListener(v -> saveReaction());
    }

    private void setupGravitaDropdown() {
        gravitaDropdown.setKeyListener(null);
        gravitaDropdown.setFocusable(false);
        gravitaDropdown.setClickable(true);
    }

    private void setupGravitaAdapter() {
        String[] options = {"Lieve", "Moderato", "Significativo", "Grave"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.dropdown_gravita, options) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                return createCustomView(position, convertView, parent);
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                return createCustomView(position, convertView, parent);
            }

            private View createCustomView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    convertView = inflater.inflate(R.layout.dropdown_gravita, parent, false);
                }
                TextView label = convertView.findViewById(R.id.labelGravita);
                ImageView icon = convertView.findViewById(R.id.iconGravita);
                label.setText(options[position]);

                int drawableRes;
                switch (options[position]) {
                    case "Lieve": drawableRes = R.drawable.cerchio_verde; break;
                    case "Moderato": drawableRes = R.drawable.cerchio_giallo; break;
                    case "Significativo": drawableRes = R.drawable.cerchio_arancione; break;
                    case "Grave": drawableRes = R.drawable.cerchio_rosso; break;
                    default: drawableRes = 0;
                }
                icon.setImageResource(drawableRes);
                return convertView;
            }
        };
        gravitaDropdown.setAdapter(adapter);
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Seleziona una data")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setTimeInMillis(selection);
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            dataEditText.setText(format.format(calendar.getTime()));
        });

        datePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
    }

    private void showTimePicker() {
        Calendar now = Calendar.getInstance();
        MaterialTimePicker picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(now.get(Calendar.HOUR_OF_DAY))
                .setMinute(now.get(Calendar.MINUTE))
                .setTitleText("Scegli l'orario")
                .build();

        picker.addOnPositiveButtonClickListener(v ->
                oraEditText.setText(String.format("%02d:%02d", picker.getHour(), picker.getMinute()))
        );

        picker.show(getSupportFragmentManager(), "TIME_PICKER");
    }

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
                // Mostra l'immagine
                imageView.setImageURI(selectedImageUri);
                // Salva l'immagine in memoria interna
                savedPhotoPath = saveImageToInternalStorage(selectedImageUri);
                if (savedPhotoPath != null) {
                    Toast.makeText(this, "Foto salvata!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private String saveImageToInternalStorage(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            String fileName = "foto_" + System.currentTimeMillis() + ".jpg";
            File file = new File(getFilesDir(), fileName);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveReaction() {
        if (!validateForm()) {
            Toast.makeText(this, "Compila tutti i campi obbligatori!", Toast.LENGTH_SHORT).show();
            return;
        }

        Reazione nuova = new Reazione();
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            nuova.setData(format.parse(dataEditText.getText().toString()));
        } catch (Exception e) {
            nuova.setData(null);
        }

        nuova.setOra(oraEditText.getText().toString());
        nuova.setAllergene(allergeneEditText.getText().toString());
        nuova.setGravita(gravitaDropdown.getText().toString());

        ArrayList<String> sintomi = new ArrayList<>();
        sintomi.add(sintomiEditText.getText().toString());
        nuova.setSintomi(sintomi);

        ArrayList<String> farmaci = new ArrayList<>();
        if (!farmacEditText.getText().toString().trim().isEmpty()) {
            farmaci.add(farmacEditText.getText().toString());
        }
        nuova.setFarmaci(farmaci);

        nuova.setNote(noteEditText.getText().toString());
        nuova.setContattoMedico(siRadioButton.isChecked());

        // aggiungi foto se presente
        ArrayList<String> fotoList = new ArrayList<>();
        if (savedPhotoPath != null) {
            fotoList.add(savedPhotoPath);
        }
        nuova.setFoto(fotoList);

        List<Reazione> lista = ReazioneStorage.loadReactions(this);
        lista.add(nuova);
        ReazioneStorage.saveReactions(this, lista);

        Toast.makeText(this, "Reazione salvata con successo!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, ReazioniAllergicheActivity.class));
        finish();
    }

    private boolean validateForm() {
        boolean isValid = true;

        if (dataEditText.getText().toString().trim().isEmpty()) {
            dataLayout.setError("Campo obbligatorio");
            isValid = false;
        } else dataLayout.setError(null);

        if (allergeneEditText.getText().toString().trim().isEmpty()) {
            allergeneLayout.setError("Campo obbligatorio");
            isValid = false;
        } else allergeneLayout.setError(null);

        if (sintomiEditText.getText().toString().trim().isEmpty()) {
            sintomiLayout.setError("Campo obbligatorio");
            isValid = false;
        } else sintomiLayout.setError(null);

        if (gravitaDropdown.getText().toString().trim().isEmpty()) {
            gravitaLayout.setError("Campo obbligatorio");
            isValid = false;
        }

        return isValid;
    }
}
