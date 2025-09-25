package org.example.carjournal.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CarRecord {
    @JsonProperty("id")
    private String id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("type")
    private RecordType type;

    @JsonProperty("cost")
    private double cost;

    @JsonProperty("mileage")
    private double mileage;

    @JsonProperty("date")
    private LocalDate date;

    @JsonProperty("dueDate")
    private LocalDate dueDate;

    @JsonProperty("status")
    private RecordStatus status;

    @JsonProperty("priority")
    private Priority priority;

    @JsonProperty("fuelAmount")
    private double fuelAmount;

    @JsonProperty("fuelPrice")
    private double fuelPrice;

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

    public void addHistoryEntry(String action) {
        this.history.add(new HistoryEntry(LocalDate.now(), action));
    }
}