package com.example.immunobubby;

import java.util.ArrayList;
import java.util.Date;
import java.io.Serializable;

public class Reazione implements Serializable {
    private Date data;
    private String ora;
    private String allergene;
    private String gravita;
    private ArrayList<String> sintomi;
    private ArrayList<String> farmaci;
    private Boolean contattoMedico;
    private String note;
    private ArrayList<String> foto;

    // Constructor
    public Reazione() {
        this.sintomi = new ArrayList<>();
        this.farmaci = new ArrayList<>();
        this.foto = new ArrayList<>();
    }

    // Getters and Setters
    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getOra() {
        return ora;
    }

    public void setOra(String ora) {
        this.ora = ora;
    }

    public String getAllergene() {
        return allergene;
    }

    public void setAllergene(String allergene) {
        this.allergene = allergene;
    }

    public String getGravita() {
        return gravita;
    }

    public void setGravita(String gravita) {
        this.gravita = gravita;
    }

    public ArrayList<String> getSintomi() {
        return sintomi;
    }

    public void setSintomi(ArrayList<String> sintomi) {
        this.sintomi = sintomi;
    }

    public void addSintomo(String sintomo) {
        this.sintomi.add(sintomo);
    }

    public void removeSintomo(int index) {
        if (index >= 0 && index < sintomi.size()) {
            this.sintomi.remove(index);
        }
    }

    public ArrayList<String> getFarmaci() {
        return farmaci;
    }

    public void setFarmaci(ArrayList<String> farmaci) {
        this.farmaci = farmaci;
    }

    public void addFarmaco(String farmaco) {
        this.farmaci.add(farmaco);
    }

    public void removeFarmaco(int index) {
        if (index >= 0 && index < farmaci.size()) {
            this.farmaci.remove(index);
        }
    }

    public Boolean getContattoMedico() {
        return contattoMedico;
    }

    public void setContattoMedico(Boolean contattoMedico) {
        this.contattoMedico = contattoMedico;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isMedicoContattato() {
        return contattoMedico != null && contattoMedico;
    }

    public ArrayList<String> getFoto() {
        return foto;
    }

    public void setFoto(ArrayList<String> foto) {
        this.foto = foto;
    }

    public void addFoto(String fotoPath) {
        this.foto.add(fotoPath);
    }

    public void removeFoto(int index) {
        if (index >= 0 && index < foto.size()) {
            this.foto.remove(index);
        }
    }

    public int getGravitaValue() {
        if (gravita == null) return 0;
        switch (gravita.toLowerCase()) {
            case "lieve": return 1;
            case "moderato": return 2;
            case "significativo": return 3;
            case "grave": return 4;
            default: return 0;
        }
    }


    // Validation method for required fields
    public boolean isValid() {
        return data != null &&
                allergene != null && !allergene.trim().isEmpty() &&
                gravita != null && !gravita.trim().isEmpty() &&
                sintomi != null && !sintomi.isEmpty();
    }
}
