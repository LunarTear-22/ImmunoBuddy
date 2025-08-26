package com.example.immunobubby;

public class Reazione {
    private String data;
    private String ora;
    private Allergene allergene;
    private String gravita;
    private String sintomi;
    private String farmaci;
    private String note;
    private boolean medicoContattato;
    private String fotoPath;

    // Constructors
    public Reazione() {}

    public Reazione(String data, String ora, Allergene allergene, String gravita,
                    String sintomi, String farmaci, String note, boolean medicoContattato) {
        this.data = data;
        this.ora = ora;
        this.allergene = allergene;
        this.gravita = gravita;
        this.sintomi = sintomi;
        this.farmaci = farmaci;
        this.note = note;
        this.medicoContattato = medicoContattato;
    }

    // Getters and Setters
    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public String getOra() { return ora; }
    public void setOra(String ora) { this.ora = ora; }

    public Allergene getAllergene() { return allergene; }
    public void setAllergene(Allergene allergene) { this.allergene = allergene; }

    public String getGravita() { return gravita; }
    public void setGravita(String gravita) { this.gravita = gravita; }

    public String getSintomi() { return sintomi; }
    public void setSintomi(String sintomi) { this.sintomi = sintomi; }

    public String getFarmaci() { return farmaci; }
    public void setFarmaci(String farmaci) { this.farmaci = farmaci; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public boolean isMedicoContattato() { return medicoContattato; }
    public void setMedicoContattato(boolean medicoContattato) { this.medicoContattato = medicoContattato; }

    public String getFotoPath() { return fotoPath; }
    public void setFotoPath(String fotoPath) { this.fotoPath = fotoPath; }
}
