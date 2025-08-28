package com.example.immunobubby;

import android.graphics.Color;

public class PollenData {
    private String name;        // Nome del polline (es. Grass, Tree, Weed)
    private int level;          // Valore numerico dell'indice pollini
    private String category;    // Categoria (Very Low, Low, Medium, High)
    private int color;          // Colore associato alla categoria per la UI

    public PollenData() {}

    public PollenData(String name, int level, String category, int color) {
        this.name = name;
        this.level = level;
        this.category = category;
        this.color = color;
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public String getCategory() {
        return category;
    }

    public int getColor() {
        return color;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
