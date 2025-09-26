package org.example.carjournal.model;
/**
 * Перечисление типов операций с автомобилем
 */
public enum RecordType {
    /**
     * Техническое обслуживание
     */
    MAINTENANCE("Техническое обслуживание"),
    /**
     * Ремонтные работы
     */
    REPAIR("Ремонт"),
    /**
     * Заправка топливом
     */
    FUEL("Заправка"),
    /**
     * Страхование
     */
    INSURANCE("Страхование"),
    /**
     * Прочие операции
     */
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