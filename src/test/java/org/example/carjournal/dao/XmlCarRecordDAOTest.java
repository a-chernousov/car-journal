package org.example.carjournal.dao;

import org.example.carjournal.model.CarRecord;
import org.example.carjournal.model.RecordStatus;
import org.example.carjournal.model.RecordType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Альтернативный тестовый класс без изменения final полей
 */
class XmlCarRecordDAOTestAlternative {

    private static final String TEST_FILE_PATH = "test_car_records.xml";
    private static final String ORIGINAL_FILE_PATH = "car_records.xml";

    private XmlCarRecordDAO carRecordDAO;

    @BeforeEach
    void setUp() throws IOException {
        // Создаем резервную копию оригинального файла
        File originalFile = new File(ORIGINAL_FILE_PATH);
        if (originalFile.exists()) {
            Files.copy(originalFile.toPath(),
                    Path.of(ORIGINAL_FILE_PATH + ".backup"),
                    StandardCopyOption.REPLACE_EXISTING);
        }

        // Удаляем тестовый файл если существует
        File testFile = new File(TEST_FILE_PATH);
        if (testFile.exists()) {
            testFile.delete();
        }

        // Создаем пустой тестовый файл
        testFile.createNewFile();

        // Копируем тестовый файл как основной
        Files.copy(testFile.toPath(),
                Path.of(ORIGINAL_FILE_PATH),
                StandardCopyOption.REPLACE_EXISTING);

        carRecordDAO = new XmlCarRecordDAO();
    }

    @AfterEach
    void tearDown() throws IOException {
        // Восстанавливаем оригинальный файл из резервной копии
        File backupFile = new File(ORIGINAL_FILE_PATH + ".backup");
        if (backupFile.exists()) {
            Files.copy(backupFile.toPath(),
                    Path.of(ORIGINAL_FILE_PATH),
                    StandardCopyOption.REPLACE_EXISTING);
            backupFile.delete();
        }

        // Удаляем тестовый файл
        File testFile = new File(TEST_FILE_PATH);
        if (testFile.exists()) {
            testFile.delete();
        }
    }

    @Test
    void testBasicOperations() {
        CarRecord record = new CarRecord();
        record.setTitle("Тест");
        record.setType(RecordType.MAINTENANCE);
        record.setDate(LocalDate.now());

        // Test save
        carRecordDAO.save(record);
        assertEquals(1, carRecordDAO.findAll().size());

        // Test find by id
        Optional<CarRecord> found = carRecordDAO.findById(record.getId());
        assertTrue(found.isPresent());
        assertEquals("Тест", found.get().getTitle());

        // Test update
        record.setTitle("Обновлено");
        carRecordDAO.update(record);
        found = carRecordDAO.findById(record.getId());
        assertEquals("Обновлено", found.get().getTitle());

        // Test delete
        carRecordDAO.delete(record.getId());
        assertEquals(0, carRecordDAO.findAll().size());
    }
}