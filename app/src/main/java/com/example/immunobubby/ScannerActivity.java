package com.example.immunobubby;

import android.media.Image;
import android.os.Bundle;
import android.util.Size;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ScannerActivity extends AppCompatActivity {

    private PreviewView previewView;
    private TextView tvMessage;
    private ChipGroup chipGroupAllergeni;

    private com.google.mlkit.vision.barcode.BarcodeScanner barcodeScanner;
    private com.google.mlkit.vision.text.TextRecognizer textRecognizer;

    private static final long SCAN_DELAY_MS = 3_000;
    private long lastScanTime = -1;
    private boolean isProcessing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        previewView = findViewById(R.id.previewView);
        tvMessage = findViewById(R.id.tvMessage);
        chipGroupAllergeni = findViewById(R.id.chipGroupAllergeni);

        // ML Kit Barcode
        barcodeScanner = com.google.mlkit.vision.barcode.BarcodeScanning.getClient(
                new com.google.mlkit.vision.barcode.BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(
                                com.google.mlkit.vision.barcode.common.Barcode.FORMAT_EAN_13,
                                com.google.mlkit.vision.barcode.common.Barcode.FORMAT_UPC_A,
                                com.google.mlkit.vision.barcode.common.Barcode.FORMAT_UPC_E,
                                com.google.mlkit.vision.barcode.common.Barcode.FORMAT_QR_CODE
                        )
                        .build()
        );

        // ML Kit OCR
        textRecognizer = com.google.mlkit.vision.text.TextRecognition.getClient(
                com.google.mlkit.vision.text.latin.TextRecognizerOptions.DEFAULT_OPTIONS
        );

        startCamera();
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> future = ProcessCameraProvider.getInstance(this);
        future.addListener(() -> {
            try {
                ProcessCameraProvider provider = future.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                ImageAnalysis analysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .setTargetResolution(new Size(1280, 720))
                        .build();

                analysis.setAnalyzer(ContextCompat.getMainExecutor(this), imageProxy -> {
                    if (isProcessing) {
                        imageProxy.close();
                        return;
                    }

                    long now = System.currentTimeMillis();
                    if (lastScanTime != -1 && (now - lastScanTime) < SCAN_DELAY_MS) {
                        imageProxy.close();
                        return;
                    }

                    @androidx.annotation.OptIn(markerClass = androidx.camera.core.ExperimentalGetImage.class)
                    Image mediaImage = imageProxy.getImage();
                    if (mediaImage == null) {
                        imageProxy.close();
                        return;
                    }

                    isProcessing = true;

                    int rotation = imageProxy.getImageInfo().getRotationDegrees();
                    InputImage inputImage = InputImage.fromMediaImage(mediaImage, rotation);

                    // Barcode
                    barcodeScanner.process(inputImage)
                            .addOnSuccessListener(barcodes -> {
                                if (barcodes != null && !barcodes.isEmpty()) {
                                    String code = barcodes.get(0).getRawValue();
                                    if (code != null && code.matches("\\d{8,14}")) {
                                        fetchIngredientsFromOpenFoodFacts(code, inputImage, imageProxy);
                                        return; // fetchIngredients chiuderà imageProxy
                                    }
                                }
                                // OCR fallback
                                textRecognizer.process(inputImage)
                                        .addOnSuccessListener(result -> showAllergeniFromText(result.getText()))
                                        .addOnCompleteListener(task -> {
                                            imageProxy.close();
                                            lastScanTime = System.currentTimeMillis();
                                            isProcessing = false;
                                        });
                            })
                            .addOnFailureListener(e -> {
                                textRecognizer.process(inputImage)
                                        .addOnCompleteListener(task -> {
                                            imageProxy.close();
                                            lastScanTime = System.currentTimeMillis();
                                            isProcessing = false;
                                        });
                            });
                });

                CameraSelector selector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                provider.unbindAll();
                provider.bindToLifecycle(this, selector, preview, analysis);

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(this, "Errore avvio camera", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void fetchIngredientsFromOpenFoodFacts(String barcode, InputImage fallbackImage, androidx.camera.core.ImageProxy imageProxy) {
        tvMessage.setText("Barcode: " + barcode + " – cerco ingredienti…");
        new Thread(() -> {
            try {
                okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
                String url = "https://world.openfoodfacts.org/api/v0/product/" + barcode + ".json";
                okhttp3.Request req = new okhttp3.Request.Builder().url(url).build();
                okhttp3.Response resp = client.newCall(req).execute();
                if (!resp.isSuccessful()) throw new IOException("HTTP " + resp.code());
                String body = resp.body().string();

                org.json.JSONObject root = new org.json.JSONObject(body);
                int status = root.optInt("status", 0);
                org.json.JSONObject product = root.optJSONObject("product");

                String ingredients = null;
                String lang = null;
                if (status == 1 && product != null) {
                    String[] fields = {
                            "ingredients_text_it",
                            "ingredients_text_en",
                            "ingredients_text_fr",
                            "ingredients_text_de",
                            "ingredients_text_es",
                            "ingredients_text"
                    };
                    for (String field : fields) {
                        ingredients = product.optString(field, null);
                        if (ingredients != null && !ingredients.isEmpty()) {
                            break; // prende il primo match disponibile
                        }
                    }
                }

                if (ingredients != null && !ingredients.isEmpty()) {
                    final String finalIngredients = ingredients;
                    final String detectedLang = detectLanguage(finalIngredients);
                    runOnUiThread(() -> showAllergeniFromText(finalIngredients, detectedLang));
                } else {
                    runOnUiThread(() -> {
                        tvMessage.setText("Ingredienti non trovati in OFF, uso OCR…");
                        chipGroupAllergeni.removeAllViews();
                    });
                    textRecognizer.process(fallbackImage)
                            .addOnSuccessListener(result -> showAllergeniFromText(result.getText()));
                }

            } catch (Exception e) {
                runOnUiThread(() -> {
                    tvMessage.setText("Errore OFF: " + e.getMessage() + "\nUso OCR…");
                    chipGroupAllergeni.removeAllViews();
                });
                textRecognizer.process(fallbackImage)
                        .addOnSuccessListener(result -> showAllergeniFromText(result.getText()));
            } finally {
                runOnUiThread(() -> {
                    imageProxy.close();
                    lastScanTime = System.currentTimeMillis();
                    isProcessing = false;
                });
            }
        }).start();
    }

    private void showAllergeniFromText(String ocrText) {
        String extracted = IngredientExtractor.extract(ocrText);
        if (extracted != null) {
            String detectedLang = detectLanguage(extracted);
            showAllergeniFromText(extracted, detectedLang);
        } else {
            tvMessage.setText("Ingredienti non trovati");
            chipGroupAllergeni.removeAllViews();
        }
    }

    private String detectLanguage(String ingredients) {
        return IngredientExtractor.detectLanguageFromKeys(ingredients);
    }

    private void showAllergeniFromText(String ingredients, String lang) {
        List<String> allergeni = IngredientExtractor.detectAllergens(ingredients, lang);
        tvMessage.setText(allergeni.isEmpty() ? "Nessun allergene rilevato" : "Allergeni rilevati:");
        chipGroupAllergeni.removeAllViews();
        for (String allergene : allergeni) {
            Chip chip = new Chip(this);
            chip.setText(allergene);
            chip.setCloseIconVisible(false);
            chip.setClickable(false);
            chip.setCheckable(false);
            chip.setChipStrokeColor(getResources().getColorStateList(R.color.primary_dark, null));
            chip.setTextColor(getResources().getColor(R.color.primary_dark, null));
            chip.setChipBackgroundColorResource(R.color.text_dark);
            chip.setChipCornerRadius(50f);
            chipGroupAllergeni.addView(chip);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (barcodeScanner != null) barcodeScanner.close();
        if (textRecognizer != null) textRecognizer.close();
    }
}
