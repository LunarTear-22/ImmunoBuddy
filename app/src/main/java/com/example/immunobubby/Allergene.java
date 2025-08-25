package com.example.immunobubby;

import java.util.List;

public class Allergene {
    private String nome;
    private List<Allergene> sottocategorie; // può essere null se non ci sono sottocategorie

    public Allergene(String nome, List<Allergene> sottocategorie) {
        this.nome = nome;
        this.sottocategorie = sottocategorie;
    }

    public String getNome() {
        return nome;
    }

    public List<Allergene> getSottocategorie() {
        return sottocategorie;
    }

    public boolean hasSottocategorie() {
        return sottocategorie != null && !sottocategorie.isEmpty();
    }
}
