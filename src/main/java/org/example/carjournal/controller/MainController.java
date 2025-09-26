package org.example.carjournal.controller;

import org.example.carjournal.model.*;
import org.example.carjournal.service.CarRecordService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.Optional;
/**
 * Контроллер главного окна приложения.
 * Управляет пользовательским интерфейсом и обрабатывает действия пользователя.
 */
public class MainController {
    @FXML private TableView<CarRecord> recordsTable;
    @FXML private TableColumn<CarRecord, String> titleColumn;
    @FXML private TableColumn<CarRecord, RecordType> typeColumn;
    @FXML private TableColumn<CarRecord, Double> costColumn;
    @FXML private TableColumn<CarRecord, LocalDate> dateColumn;
    @FXML private TableColumn<CarRecord, RecordStatus> statusColumn;

    @FXML private TextField titleField;
    @FXML private TextArea descriptionField;
    @FXML private ComboBox<RecordType> typeComboBox;
    @FXML private TextField costField;
    @FXML private TextField mileageField;
    @FXML private DatePicker datePicker;
    @FXML private DatePicker dueDatePicker;
    @FXML private ComboBox<RecordStatus> statusComboBox;
    @FXML private ComboBox<Priority> priorityComboBox;
    @FXML private TextField fuelAmountField;
    @FXML private TextField fuelPriceField;
    @FXML private TextField searchField;

    @FXML private Label costPerKmLabel;
    @FXML private Label nextMaintenanceLabel;
    @FXML private ListView<CarRecord> anomaliesListView;

    private CarRecordService carRecordService;
    private ObservableList<CarRecord> records;
    private boolean initialized = false;

    /**
     * Инициализация контроллера после загрузки FXML
     */
    @FXML
    public void initialize() {
        setupTableColumns();
        setupComboBoxes();
        setupAnomaliesListView();
        // Не загружаем записи здесь, ждем установки carRecordService
        datePicker.setValue(LocalDate.now());
        initialized = true;
    }
    /**
     * Устанавливает сервис для работы с данными
     * @param service сервис автомобильных записей
     */
    public void setCarRecordService(CarRecordService service) {
        this.carRecordService = service;
        if (initialized && carRecordService != null) {
            loadRecords();
            updateStatistics();
        }
    }


    /**
     * Обрабатывает добавление новой записи
     */
    private void setupTableColumns() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        costColumn.setCellValueFactory(new PropertyValueFactory<>("cost"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        costColumn.setCellFactory(column -> new TableCell<CarRecord, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.2f руб.", item));
                }
            }
        });

        // Добавляем обработчик выбора строки
        recordsTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> handleRowSelect(newValue)
        );
    }

    private void setupComboBoxes() {
        typeComboBox.setItems(FXCollections.observableArrayList(RecordType.values()));
        statusComboBox.setItems(FXCollections.observableArrayList(RecordStatus.values()));
        priorityComboBox.setItems(FXCollections.observableArrayList(Priority.values()));

        typeComboBox.getSelectionModel().selectFirst();
        statusComboBox.getSelectionModel().selectFirst();
        priorityComboBox.getSelectionModel().selectFirst();
    }

    private void loadRecords() {
        if (carRecordService != null) {
            records = FXCollections.observableArrayList(carRecordService.getAllRecords());
            recordsTable.setItems(records);
        }
    }

    @FXML
    private void handleAddRecord() {
        if (validateInput()) {
            CarRecord record = createRecordFromForm();
            carRecordService.addRecord(record);
            clearForm();
            loadRecords();
            updateStatistics();
            showAlert("Успех", "Запись успешно добавлена", Alert.AlertType.INFORMATION);
        }
    }
    /**
     * Обрабатывает обновление существующей записи
     */
    @FXML
    private void handleUpdateRecord() {
        CarRecord selected = recordsTable.getSelectionModel().getSelectedItem();
        if (selected != null && validateInput()) {
            CarRecord updatedRecord = createRecordFromForm();
            updatedRecord.setId(selected.getId());
            updatedRecord.setHistory(selected.getHistory());
            carRecordService.updateRecord(updatedRecord);
            clearForm();
            loadRecords();
            updateStatistics();
            showAlert("Успех", "Запись успешно обновлена", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Ошибка", "Выберите запись для редактирования", Alert.AlertType.WARNING);
        }
    }
    /**
     * Обрабатывает удаление записи
     */
    @FXML
    private void handleDeleteRecord() {
        CarRecord selected = recordsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Подтверждение удаления");
            alert.setHeaderText("Удаление записи");
            alert.setContentText("Вы уверены, что хотите удалить запись: " + selected.getTitle() + "?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                carRecordService.deleteRecord(selected.getId());
                clearForm();
                loadRecords();
                updateStatistics();
                showAlert("Успех", "Запись успешно удалена", Alert.AlertType.INFORMATION);
            }
        } else {
            showAlert("Ошибка", "Выберите запись для удаления", Alert.AlertType.WARNING);
        }
    }
    /**
     * Выполняет поиск записей по введенному тексту
     */
    @FXML
    private void handleSearch() {
        String query = searchField.getText();
        if (query != null && !query.trim().isEmpty()) {
            recordsTable.setItems(FXCollections.observableArrayList(
                    carRecordService.searchRecords(query)
            ));
        } else {
            loadRecords();
        }
    }

    private void handleRowSelect(CarRecord selected) {
        if (selected != null) {
            populateForm(selected);
        }
    }

    @FXML
    private void handleUpdateStatuses() {
        carRecordService.updateStatuses();
        loadRecords();
        updateStatistics();
        showAlert("Обновление", "Статусы обновлены", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void clearForm() {
        titleField.clear();
        descriptionField.clear();
        typeComboBox.getSelectionModel().selectFirst();
        costField.clear();
        mileageField.clear();
        datePicker.setValue(LocalDate.now());
        dueDatePicker.setValue(null);
        statusComboBox.getSelectionModel().selectFirst();
        priorityComboBox.getSelectionModel().selectFirst();
        fuelAmountField.clear();
        fuelPriceField.clear();
        recordsTable.getSelectionModel().clearSelection();
    }

    private void setupAnomaliesListView() {
        anomaliesListView.setCellFactory(lv -> new ListCell<CarRecord>() {
            @Override
            protected void updateItem(CarRecord item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    // Форматируем отображение аномалии
                    setText(String.format("Аномалия: %s (Пробег: %.0f км, Топливо: %.1f л)",
                            item.getTitle(), item.getMileage(), item.getFuelAmount()));
                }
            }
        });
    }

    private CarRecord createRecordFromForm() {
        CarRecord record = new CarRecord();
        record.setTitle(titleField.getText());
        record.setDescription(descriptionField.getText());
        record.setType(typeComboBox.getValue());
        record.setCost(parseDouble(costField.getText()));
        record.setMileage(parseDouble(mileageField.getText()));
        record.setDate(datePicker.getValue());
        record.setDueDate(dueDatePicker.getValue());
        record.setStatus(statusComboBox.getValue());
        record.setPriority(priorityComboBox.getValue());
        record.setFuelAmount(parseDouble(fuelAmountField.getText()));
        record.setFuelPrice(parseDouble(fuelPriceField.getText()));
        return record;
    }

    private void populateForm(CarRecord record) {
        titleField.setText(record.getTitle());
        descriptionField.setText(record.getDescription());
        typeComboBox.setValue(record.getType());
        costField.setText(String.valueOf(record.getCost()));
        mileageField.setText(String.valueOf(record.getMileage()));
        datePicker.setValue(record.getDate());
        dueDatePicker.setValue(record.getDueDate());
        statusComboBox.setValue(record.getStatus());
        priorityComboBox.setValue(record.getPriority());
        fuelAmountField.setText(String.valueOf(record.getFuelAmount()));
        fuelPriceField.setText(String.valueOf(record.getFuelPrice()));
    }

    private boolean validateInput() {
        if (titleField.getText() == null || titleField.getText().trim().isEmpty()) {
            showAlert("Ошибка", "Введите название", Alert.AlertType.ERROR);
            return false;
        }
        if (datePicker.getValue() == null) {
            showAlert("Ошибка", "Выберите дату", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    private double parseDouble(String text) {
        try {
            return text == null || text.trim().isEmpty() ? 0.0 : Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    /**
     * Обновляет статистические показатели на интерфейсе
     */
    private void updateStatistics() {
        if (carRecordService == null) return;

        // Обновление стоимости владения
        double costPerKm = carRecordService.calculateCostPerKm();
        costPerKmLabel.setText(String.format("Стоимость владения: %.2f руб/км", costPerKm));

        // Обновление аномалий расхода топлива
        anomaliesListView.setItems(FXCollections.observableArrayList(
                carRecordService.findFuelAnomalies()
        ));

        // Прогноз ТО (упрощенный)
        nextMaintenanceLabel.setText("Прогноз ТО: рассчитается при добавлении записей ТО");
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}