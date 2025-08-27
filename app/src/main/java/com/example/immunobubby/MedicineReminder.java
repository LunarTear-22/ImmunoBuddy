package com.example.immunobubby;

import java.util.ArrayList;
import java.util.List;

public class MedicineReminder {
    private String name;
    private int hour;
    private int minute;
    private List<Integer> days;
    private boolean active; // indica se il promemoria è attivo

    public MedicineReminder() {
        this.name = null; // nome provvisorio
        this.hour = 0;
        this.minute = 0;
        this.days = new ArrayList<>(); // giorni selezionati
        this.active = false; // di default non attivo
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getHour() { return hour; }
    public void setHour(int hour) { this.hour = hour; }

    public int getMinute() { return minute; }
    public void setMinute(int minute) { this.minute = minute; }

    public List<Integer> getDays() { return days; }
    public void setDays(List<Integer> days) { this.days = days; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
