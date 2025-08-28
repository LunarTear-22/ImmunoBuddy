package com.example.immunobubby;

public class PollenData {
    public String name;
    public int level; // 0-100 o scala fornita dall'API

    public PollenData() {}

    public PollenData(String name, int level) {
        this.name = name;
        this.level = level;
    }



    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
