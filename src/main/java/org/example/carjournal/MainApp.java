package org.example.carjournal;

import org.example.carjournal.controller.MainController;
import org.example.carjournal.dao.XmlCarRecordDAO;
import org.example.carjournal.service.CarRecordService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        // Инициализация зависимостей
        XmlCarRecordDAO carRecordDAO = new XmlCarRecordDAO();
        CarRecordService carRecordService = new CarRecordService(carRecordDAO);

        // Загрузка FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Parent root = loader.load();

        // Установка сервиса в контроллер
        MainController controller = loader.getController();
        controller.setCarRecordService(carRecordService);

        // Настройка сцены
        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setTitle("Автомобильный журнал");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}