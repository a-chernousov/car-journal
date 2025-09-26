package org.example.carjournal.service;

import org.example.carjournal.dao.CarRecordDAO;
import org.example.carjournal.model.CarRecord;
import org.example.carjournal.model.RecordStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
/**
 * Сервисный слой для работы с записями об операциях с автомобилем.
 * Содержит бизнес-логику приложения.
 */
public class CarRecordService {
    private final CarRecordDAO carRecordDAO;
    /**
     * Конструктор сервиса
     * @param carRecordDAO объект для доступа к данным
     */
    public CarRecordService(CarRecordDAO carRecordDAO) {
        this.carRecordDAO = carRecordDAO;
    }
    /**
     * Получает все записи об операциях
     * @return список всех записей
     */
    public List<CarRecord> getAllRecords() {
        return carRecordDAO.findAll();
    }
    /**
     * Находит запись по идентификатору
     * @param id уникальный идентификатор записи
     * @return Optional с найденной записью или empty если не найдена
     */
    public Optional<CarRecord> getRecordById(String id) {
        return carRecordDAO.findById(id);
    }
    /**
     * Добавляет новую запись об операции
     * @param record объект записи для добавления
     */
    public void addRecord(CarRecord record) {
        record.addHistoryEntry("Запись создана");
        carRecordDAO.save(record);
    }
    /**
     * Обновляет существующую запись
     * @param record объект записи с обновленными данными
     */
    public void updateRecord(CarRecord record) {
        record.addHistoryEntry("Запись обновлена");
        carRecordDAO.update(record);
    }
    /**
     * Удаляет запись по идентификатору
     * @param id уникальный идентификатор записи для удаления
     */
    public void deleteRecord(String id) {
        carRecordDAO.delete(id);
    }

    /**
     * Рассчитывает дату следующего технического обслуживания
     * @param currentMileage текущий пробег автомобиля
     * @param lastMaintenanceDate дата последнего ТО
     * @return прогнозируемая дата следующего ТО
     */
    public LocalDate calculateNextMaintenance(double currentMileage, LocalDate lastMaintenanceDate) {
        double nextMileage = currentMileage + 15000;
        LocalDate nextDateByTime = lastMaintenanceDate.plusYears(1);

        // Предполагаем средний пробег 1500 км в месяц для расчета по дате
        double avgMonthlyMileage = 1500;
        double monthsToMileage = 15000 / avgMonthlyMileage;
        LocalDate nextDateByMileage = lastMaintenanceDate.plusMonths((long) monthsToMileage);

        return nextDateByTime.isBefore(nextDateByMileage) ? nextDateByTime : nextDateByMileage;
    }

    /**
     * Рассчитывает стоимость владения автомобилем в рублях за километр
     * @return стоимость в руб/км
     */
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


    /**
     * Находит аномалии расхода топлива на основе записей о заправках
     * @return список записей с аномальным расходом топлива
     */
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
    /**
     * Выполняет поиск записей по ключевым словам
     * @param query строка поиска
     * @return список найденных записей
     */
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
    /**
     * Обновляет статусы записей (например, помечает просроченные)
     */
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
