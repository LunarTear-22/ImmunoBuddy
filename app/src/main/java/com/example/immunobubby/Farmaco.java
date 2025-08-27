package com.example.immunobubby;

public class Farmaco {
    private String nome;
    private String tipologia;
    private String dosaggio;

    public Farmaco(String nome, String tipologia, String dosaggio) {
        this.nome = nome;
        this.tipologia = tipologia;
        this.dosaggio = dosaggio;
    }

    // Getter e Setter
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTipologia() { return tipologia; }
    public void setTipologia(String tipologia) { this.tipologia = tipologia; }

    public String getDosaggio() { return dosaggio; }
    public void setDosaggio(String dosaggio) { this.dosaggio = dosaggio; }
}
