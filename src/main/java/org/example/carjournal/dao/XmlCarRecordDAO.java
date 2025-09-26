package org.example.carjournal.dao;

import org.example.carjournal.model.CarRecord;
import org.example.carjournal.util.LocalDateModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
/**
 * Реализация DAO для работы с XML-хранилищем записей об операциях с автомобилем.
 * Использует Jackson XML для сериализации/десериализации данных.
 */
public class XmlCarRecordDAO implements CarRecordDAO {
    private static final String FILE_PATH = "car_records.xml";
    private final XmlMapper xmlMapper;
    private List<CarRecord> records;

    public XmlCarRecordDAO() {
        this.xmlMapper = new XmlMapper();

        // Регистрируем модули для обработки дат
        xmlMapper.registerModule(new JavaTimeModule());
        xmlMapper.registerModule(new LocalDateModule());

        // Настраиваем для корректной работы с датами
        xmlMapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        xmlMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        xmlMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);

        this.records = loadRecords();
    }
    /**
     * Загружает записи из XML-файла
     * @return список загруженных записей
     */
    private List<CarRecord> loadRecords() {
        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            System.out.println("No existing records file found, starting with empty list.");
            return new ArrayList<>();
        }

        try {
            System.out.println("Loading records from: " + file.getAbsolutePath());

            // Читаем как список
            List<CarRecord> loadedRecords = xmlMapper.readValue(file,
                    xmlMapper.getTypeFactory().constructCollectionType(List.class, CarRecord.class));

            System.out.println("Successfully loaded " + loadedRecords.size() + " records");
            return new ArrayList<>(loadedRecords);

        } catch (IOException e) {
            System.err.println("Error loading records: " + e.getMessage());
            e.printStackTrace();

            // Если не получается загрузить, создаем новый файл
            System.out.println("Creating new empty records file.");
            saveRecords(); // Создаем пустой файл
            return new ArrayList<>();
        }
    }
    /**
     * Сохраняет все записи в XML-файл
     */
    private void saveRecords() {
        try {
            File file = new File(FILE_PATH);
            System.out.println("Saving " + records.size() + " records to: " + file.getAbsolutePath());

            // Сохраняем как список
            xmlMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(file, records);

            System.out.println("Records saved successfully.");

        } catch (IOException e) {
            System.err.println("Error saving records: " + e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * Находит все записи в хранилище
     * @return список всех записей
     */
    @Override
    public List<CarRecord> findAll() {
        return new ArrayList<>(records);
    }
    /**
     * Находит запись по идентификатору
     * @param id идентификатор записи
     * @return Optional с найденной записью
     */
    @Override
    public Optional<CarRecord> findById(String id) {
        return records.stream()
                .filter(record -> record.getId() != null && record.getId().equals(id))
                .findFirst();
    }
    /**
     * Сохраняет новую запись в хранилище
     * @param record объект записи для сохранения
     */
    @Override
    public void save(CarRecord record) {
        if (record.getId() == null) {
            record.setId(java.util.UUID.randomUUID().toString());
        }

        // Убедимся, что дата установлена
        if (record.getDate() == null) {
            record.setDate(java.time.LocalDate.now());
        }

        records.add(record);
        saveRecords();
    }

    @Override
    public void update(CarRecord record) {
        delete(record.getId());
        records.add(record);
        saveRecords();
    }

    @Override
    public void delete(String id) {
        records.removeIf(record -> record.getId() != null && record.getId().equals(id));
        saveRecords();
    }

    @Override
    public List<CarRecord> findByStatus(String status) {
        return records.stream()
                .filter(record -> record.getStatus() != null &&
                        record.getStatus().name().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }

    @Override
    public List<CarRecord> findByType(String type) {
        return records.stream()
                .filter(record -> record.getType() != null &&
                        record.getType().name().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }
}