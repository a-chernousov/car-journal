module org.example.carjournal {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.xml;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires com.fasterxml.jackson.annotation;

    opens org.example.carjournal to javafx.fxml;
    opens org.example.carjournal.controller to javafx.fxml;
    opens org.example.carjournal.model to com.fasterxml.jackson.databind;
    opens org.example.carjournal.dao to com.fasterxml.jackson.databind; // ДОБАВЛЕНО

    exports org.example.carjournal;
    exports org.example.carjournal.controller;
    exports org.example.carjournal.dao; // ДОБАВЛЕНО
    exports org.example.carjournal.service;
    exports org.example.carjournal.model;
    exports org.example.carjournal.util;
}