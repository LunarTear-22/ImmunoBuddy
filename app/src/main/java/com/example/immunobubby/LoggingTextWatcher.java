package com.example.immunobubby;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

public class LoggingTextWatcher implements TextWatcher {
    private final java.util.function.Consumer<String> onTextChanged;
    private final int position;
    private final String tag;

    public LoggingTextWatcher(String tag, int position, java.util.function.Consumer<String> onTextChanged) {
        this.tag = tag;
        this.position = position;
        this.onTextChanged = onTextChanged;
    }

    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    @Override public void afterTextChanged(Editable s) {}
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Log.d("KitEmergenzaActivity", tag + " changed at pos " + position + ": " + s.toString());
        onTextChanged.accept(s.toString());
    }
}