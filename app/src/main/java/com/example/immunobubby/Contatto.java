package com.example.immunobubby;

public class Contatto {
    private String nome;
    private String numero;

    public Contatto(String nome, String numero) {
        this.nome = nome;
        this.numero = numero;
    }

    // Getter e Setter
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
}
