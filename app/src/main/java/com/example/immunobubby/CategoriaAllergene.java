package com.example.immunobubby;

import java.util.List;

public class CategoriaAllergene {
    private String nome;
    private List<Allergene> allergeni;

    public CategoriaAllergene(String nome, List<Allergene> allergeni) {
        this.nome = nome;
        this.allergeni = allergeni;
    }

    public String getNome() {
        return nome;
    }

    public List<Allergene> getAllergeni() {
        return allergeni;
    }
}
