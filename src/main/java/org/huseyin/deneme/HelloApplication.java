package org.huseyin.deneme;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class HelloApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/org/huseyin/deneme/Hello-view.fxml")));
        primaryStage.setTitle("Dosya Arşivleyici");

        // Pencere boyutunu ayarlama
        primaryStage.setScene(new Scene(root, 800, 600)); // Genişlik ve yükseklik burada ayarlandı
        primaryStage.setResizable(false); // Pencere boyutunu değiştirilemez yap

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
