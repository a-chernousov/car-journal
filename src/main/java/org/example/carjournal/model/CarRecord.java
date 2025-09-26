package org.example.carjournal.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс представляет запись об операции с автомобилем.
 * Содержит информацию о типе операции, стоимости, пробеге, датах и истории изменений.
 */
public class CarRecord {
    /**
     * Уникальный идентификатор записи
     */
    @JsonProperty("id")
    private String id;

    /**
     * Название операции (например, "Замена масла")
     */
    @JsonProperty("title")
    private String title;
    /**
     * Полное описание операции
     */
    @JsonProperty("description")
    private String description;
    /**
     * Тип операции (ТО, ремонт, заправка и т.д.)
     */
    @JsonProperty("type")
    private RecordType type;
    /**
     * Стоимость операции в рублях
     */
    @JsonProperty("cost")
    private double cost;
    /**
     * Пробег автомобиля на момент операции
     */
    @JsonProperty("mileage")
    private double mileage;

    /**
     * Дата выполнения операции
     */
    @JsonProperty("date")
    private LocalDate date;
    /**
     * Срок выполнения для отложенных операций
     */
    @JsonProperty("dueDate")
    private LocalDate dueDate;
    /**
     * Статус выполнения операции
     */
    @JsonProperty("status")
    private RecordStatus status;
    /**
     * Приоритет операции
     */
    @JsonProperty("priority")
    private Priority priority;
    /**
     * Количество заправленного топлива (для операций типа FUEL)
     */
    @JsonProperty("fuelAmount")
    private double fuelAmount;
    /**
     * Цена топлива за литр (для операций типа FUEL)
     */
    @JsonProperty("fuelPrice")
    private double fuelPrice;
    /**
     * История изменений записи
     */
    @JsonProperty("history")
    private List<HistoryEntry> history;

    public CarRecord() {
        this.history = new ArrayList<>();
        this.status = RecordStatus.ACTIVE;
        this.priority = Priority.MEDIUM;
        this.date = LocalDate.now();
    }

    // Геттеры и сеттеры остаются без изменений
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public RecordType getType() { return type; }
    public void setType(RecordType type) { this.type = type; }

    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }

    public double getMileage() { return mileage; }
    public void setMileage(double mileage) { this.mileage = mileage; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public RecordStatus getStatus() { return status; }
    public void setStatus(RecordStatus status) { this.status = status; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public double getFuelAmount() { return fuelAmount; }
    public void setFuelAmount(double fuelAmount) { this.fuelAmount = fuelAmount; }

    public double getFuelPrice() { return fuelPrice; }
    public void setFuelPrice(double fuelPrice) { this.fuelPrice = fuelPrice; }

    public List<HistoryEntry> getHistory() { return history; }
    public void setHistory(List<HistoryEntry> history) { this.history = history; }
    /**
     * Добавляет запись в историю изменений
     * @param action Описание выполненного действия
     */
    public void addHistoryEntry(String action) {
        this.history.add(new HistoryEntry(LocalDate.now(), action));
    }
}