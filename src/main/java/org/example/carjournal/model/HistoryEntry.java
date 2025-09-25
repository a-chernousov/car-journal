package org.example.carjournal.model;

import java.time.LocalDate;

public class HistoryEntry {
    private LocalDate date;
    private String action;

    public HistoryEntry() {}

    public HistoryEntry(LocalDate date, String action) {
        this.date = date;
        this.action = action;
    }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
}