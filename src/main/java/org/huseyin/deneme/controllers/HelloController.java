package org.huseyin.deneme.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class HelloController {

    @FXML
    private void goToCompress(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/org/huseyin/deneme/Compress-view.fxml")));

        // Yeni sahneyi oluştur ve ayarla
        Scene scene = new Scene(root, 800, 600); // Genişlik ve yükseklik burada ayarlandı

        // Mevcut sahneyi almak için getStage() yöntemini kullanma
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Sahneyi güncelle
        stage.setScene(scene);
        stage.setWidth(800); // Genişlik
        stage.setHeight(600); // Yükseklik
        stage.show();
    }

    @FXML
    private void goToExtract(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/org/huseyin/deneme/Extract-view.fxml")));

        Scene scene = new Scene(root, 800, 600); // Genişlik ve yükseklik burada ayarlandı
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(scene);
        stage.setWidth(800); // Genişlik
        stage.setHeight(600); // Yükseklik
        stage.show();
    }


}
