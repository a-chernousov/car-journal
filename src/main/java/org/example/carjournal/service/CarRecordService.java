package org.example.carjournal.service;

import org.example.carjournal.dao.CarRecordDAO;
import org.example.carjournal.model.CarRecord;
import org.example.carjournal.model.RecordStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CarRecordService {
    private final CarRecordDAO carRecordDAO;

    public CarRecordService(CarRecordDAO carRecordDAO) {
        this.carRecordDAO = carRecordDAO;
    }

    public List<CarRecord> getAllRecords() {
        return carRecordDAO.findAll();
    }

    public Optional<CarRecord> getRecordById(String id) {
        return carRecordDAO.findById(id);
    }

    public void addRecord(CarRecord record) {
        record.addHistoryEntry("Запись создана");
        carRecordDAO.save(record);
    }

    public void updateRecord(CarRecord record) {
        record.addHistoryEntry("Запись обновлена");
        carRecordDAO.update(record);
    }

    public void deleteRecord(String id) {
        carRecordDAO.delete(id);
    }

    // Функция 1: Прогноз ТО
    public LocalDate calculateNextMaintenance(double currentMileage, LocalDate lastMaintenanceDate) {
        double nextMileage = currentMileage + 15000;
        LocalDate nextDateByTime = lastMaintenanceDate.plusYears(1);

        // Предполагаем средний пробег 1500 км в месяц для расчета по дате
        double avgMonthlyMileage = 1500;
        double monthsToMileage = 15000 / avgMonthlyMileage;
        LocalDate nextDateByMileage = lastMaintenanceDate.plusMonths((long) monthsToMileage);

        return nextDateByTime.isBefore(nextDateByMileage) ? nextDateByTime : nextDateByMileage;
    }

    // Функция 2: Стоимость владения
    public double calculateCostPerKm() {
        List<CarRecord> records = carRecordDAO.findAll();
        if (records.isEmpty()) return 0.0;

        double totalCost = records.stream()
                .mapToDouble(CarRecord::getCost)
                .sum();

        double maxMileage = records.stream()
                .mapToDouble(CarRecord::getMileage)
                .max()
                .orElse(1.0);

        return totalCost / maxMileage;
    }

    // Функция 3: Аномалии расхода топлива (исправлено toList() на collect(Collectors.toList()))
    public List<CarRecord> findFuelAnomalies() {
        List<CarRecord> fuelRecords = carRecordDAO.findAll().stream()
                .filter(record -> record.getType().name().equals("FUEL"))
                .sorted((r1, r2) -> Double.compare(r1.getMileage(), r2.getMileage()))
                .collect(Collectors.toList());

        List<CarRecord> anomalies = new ArrayList<>();

        for (int i = 1; i < fuelRecords.size(); i++) {
            CarRecord current = fuelRecords.get(i);
            CarRecord previous = fuelRecords.get(i - 1);

            double distance = current.getMileage() - previous.getMileage();
            double fuelEfficiency = distance / current.getFuelAmount();

            // Если расход больше 20 л/100км - считаем аномалией
            if (fuelEfficiency < 5.0) { // 5 км/л = 20 л/100км
                anomalies.add(current);
            }
        }

        return anomalies;
    }

    public List<CarRecord> searchRecords(String query) {
        return carRecordDAO.findAll().stream()
                .filter(record -> containsIgnoreCase(record.getTitle(), query) ||
                        containsIgnoreCase(record.getDescription(), query) ||
                        containsIgnoreCase(record.getType().getDisplayName(), query))
                .collect(Collectors.toList());
    }

    public List<CarRecord> filterByStatus(RecordStatus status) {
        return carRecordDAO.findByStatus(status.name());
    }

    public void updateStatuses() {
        List<CarRecord> records = carRecordDAO.findAll();
        LocalDate today = LocalDate.now();

        for (CarRecord record : records) {
            if (record.getDueDate() != null && record.getDueDate().isBefore(today)
                    && record.getStatus() == RecordStatus.ACTIVE) {
                record.setStatus(RecordStatus.PENDING);
                record.addHistoryEntry("Статус изменен на 'В процессе' - просрочено");
                carRecordDAO.update(record);
            }
        }
    }

    private boolean containsIgnoreCase(String text, String query) {
        return text != null && text.toLowerCase().contains(query.toLowerCase());
    }
}
