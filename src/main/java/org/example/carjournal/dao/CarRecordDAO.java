package org.example.carjournal.dao;


import org.example.carjournal.model.CarRecord;
import java.util.List;
import java.util.Optional;

public interface CarRecordDAO {
    List<CarRecord> findAll();
    Optional<CarRecord> findById(String id);
    void save(CarRecord record);
    void update(CarRecord record);
    void delete(String id);
    List<CarRecord> findByStatus(String status);
    List<CarRecord> findByType(String type);
}