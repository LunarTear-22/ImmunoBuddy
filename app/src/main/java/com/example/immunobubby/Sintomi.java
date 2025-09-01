package com.example.immunobubby;

public class Sintomi {
    private String nome;
    private String frequenza;
    private String gravita;

    public Sintomi (String nome, String frequenza, String gravita) {
        this.nome = nome;
        this.frequenza = frequenza;
        this.gravita = gravita;
    }

    // Getter e Setter
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFrequenza() {
        return frequenza;
    }

    public void setFrequenza(String frequenza) {
        this.frequenza = frequenza;
    }

    public String getGravita() {
        return gravita;
    }

    public void setGravita(String gravita) {
        this.gravita = gravita;
    }
}
