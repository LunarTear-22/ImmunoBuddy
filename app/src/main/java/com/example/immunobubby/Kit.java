package com.example.immunobubby;

public class Kit {
    private String name;
    private String description;

    // Costruttore, getter e setter
    public Kit() {

    }

    public Kit(String name, String description, String date) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
