package com.example.immunobubby;

import java.util.Date;

public class ReazioniPreview {
    private String name;
    private Date data;

    // Costruttore, getter e setter
    public ReazioniPreview() {

    }

    public ReazioniPreview(String name, Date date) {
        this.name = name;
        this.data = date;
    }

    public String getName() {
        return name;
    }

    public Date getData() {
        return data;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setData(Date data) {
        this.data = data;
    }
    @Override
    public String toString() {
        return "ReazioniPreview{" + "name='" + name + '\'' + ", data=" + data + '}';
    }
}
