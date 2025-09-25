package org.example.carjournal.model;

public enum RecordType {
    MAINTENANCE("Техническое обслуживание"),
    REPAIR("Ремонт"),
    FUEL("Заправка"),
    INSURANCE("Страхование"),
    OTHER("Другое");

    private final String displayName;

    RecordType(String displayName) {
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