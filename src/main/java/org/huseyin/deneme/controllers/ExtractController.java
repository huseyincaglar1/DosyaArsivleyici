package org.huseyin.deneme.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ExtractController {

    @FXML
    private Label selectedFileLabel;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label percentLabel;

    @FXML
    private Label statusLabel;

    private List<File> selectedZipFiles;

    @FXML
    private void selectZipFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ZIP Dosyaları", "*.zip"));
        fileChooser.setTitle("ZIP Dosyası Seçin");
        Stage stage = (Stage) selectedFileLabel.getScene().getWindow();
        selectedZipFiles = fileChooser.showOpenMultipleDialog(stage);

        if (selectedZipFiles != null && !selectedZipFiles.isEmpty()) {
            StringBuilder sb = new StringBuilder("Seçilen ZIP dosyaları:\n");
            for (File file : selectedZipFiles) {
                sb.append(file.getAbsolutePath()).append("\n");
            }
            selectedFileLabel.setText(sb.toString());
        } else {
            selectedFileLabel.setText("Seçilen dosya yok");
        }
    }

    @FXML
    private void decompressFiles() {
        if (selectedZipFiles == null || selectedZipFiles.isEmpty()) {
            showAlert("Hata", "Lütfen bir veya daha fazla ZIP dosyası seçin!");
            return;
        }

        // İlerleme çubuğunu ve etiketlerini görünür hale getir
        progressBar.setVisible(true);
        progressBar.setProgress(0);
        percentLabel.setVisible(true);
        percentLabel.setText("0%");
        statusLabel.setVisible(true);
        statusLabel.setText("İşlem başlatılıyor...");

        // Thread havuzunu oluştur
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        // Dosyaları çıkarmaya başla
        long startTime = System.currentTimeMillis();
        AtomicInteger processedEntries = new AtomicInteger(0);
        AtomicInteger totalEntries = new AtomicInteger(0);

        // Dosya sayısını ve girişleri say
        for (File selectedZipFile : selectedZipFiles) {
            executor.submit(() -> countEntries(selectedZipFile, totalEntries));
        }

        // ZIP dosyalarını işleme başla
        for (File selectedZipFile : selectedZipFiles) {
            executor.submit(() -> extractZipFile(selectedZipFile, processedEntries, totalEntries));
        }

        // Thread havuzunun tamamlanmasını bekle
        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.HOURS)) {
                System.out.println("İşlem tamamlanmadı.");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        Platform.runLater(() -> {
            progressBar.setVisible(false);
            percentLabel.setVisible(false);
            statusLabel.setVisible(false);
            showAlert("Tamamlandı", "Tüm ZIP dosyaları başarıyla çıkarıldı! Süre: " + (duration / 1000.0) + " saniye.");
        });
    }


    private void countEntries(File zipFile, AtomicInteger totalEntries) {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                totalEntries.incrementAndGet();
                zis.closeEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void extractZipFile(File zipFile, AtomicInteger processedEntries, AtomicInteger totalEntries) {
        try {
            File destDir = new File(zipFile.getParent(), zipFile.getName().replace(".zip", ""));
            if (!destDir.exists() && !destDir.mkdir()) {
                return;
            }

            try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
                ZipEntry zipEntry;
                while ((zipEntry = zis.getNextEntry()) != null) {
                    File newFile = new File(destDir, zipEntry.getName());
                    if (zipEntry.isDirectory()) {
                        newFile.mkdirs();
                    } else {
                        new File(newFile.getParent()).mkdirs();
                        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(newFile))) {
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = zis.read(buffer)) > 0) {
                                bos.write(buffer, 0, length);
                            }
                        }
                    }

                    // İşlem edilen giriş sayısını güncelle
                    int currentProcessed = processedEntries.incrementAndGet();
                    double progress = (double) currentProcessed / totalEntries.get();

                    Platform.runLater(() -> {
                        progressBar.setProgress(progress);
                        percentLabel.setText(String.format("%.0f%%", progress * 100));
                        statusLabel.setText(String.format("İşlenen: %d / %d", currentProcessed, totalEntries.get()));
                    });

                    zis.closeEntry();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/huseyin/deneme/hello-view.fxml"));
            Parent root = loader.load();
            Stage primaryStage = (Stage) selectedFileLabel.getScene().getWindow();
            primaryStage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

