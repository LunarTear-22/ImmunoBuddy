package com.example.immunobubby;

import java.util.ArrayList;
import java.util.List;

public class Allergene {
    private String nome;
    private String categoria; // aggiunta
    private List<Allergene> sottocategorie;

    // costruttore classico
    public Allergene(String nome, List<Allergene> sottocategorie) {
        this.nome = nome;
        this.categoria = null;
        this.sottocategorie = sottocategorie;
    }

    // costruttore con categoria
    public Allergene(String nome, String categoria) {
        this.nome = nome;
        this.categoria = categoria;
        this.sottocategorie = new ArrayList<>();
    }

    public Allergene(String nome, String categoria, List<Allergene> sottocategorie) {
        this.nome = nome;
        this.categoria = categoria;
        this.sottocategorie = sottocategorie;
    }

    public String getNome() {
        return nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public List<Allergene> getSottocategorie() {
        return sottocategorie;
    }

    public boolean hasSottocategorie() {
        return sottocategorie != null && !sottocategorie.isEmpty();
    }
}
