package org.example.carjournal.model;

public enum RecordStatus {
    ACTIVE("Активно"),
    COMPLETED("Завершено"),
    PENDING("В процессе"),
    CANCELLED("Отменено");

    private final String displayName;

    RecordStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}