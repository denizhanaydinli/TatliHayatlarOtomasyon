package com.example.tatlihayatlar.tatlihayatlarotomasyon;

import com.example.tatlihayatlar.tatlihayatlarotomasyon.Entity.Masa;
import com.example.tatlihayatlar.tatlihayatlarotomasyon.Product.UrunListesi;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    private static final int MAX_MASALAR = 20;
    private static final int MASALARI_SIRALA = 5;

    private final UrunListesi urunListesi = new UrunListesi();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Tatlı Hayatlar Cafe");



        // GridPane içinde Logo ve Masaları oluştur
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Masaları oluştur
        for (int i = 0; i < MAX_MASALAR; i++) {
            Masa masaEntity = new Masa(i + 1, false);
            Button masaButton = createMasaButton(masaEntity);
            gridPane.add(masaButton, i % MASALARI_SIRALA, i / MASALARI_SIRALA + 1);
        }

        // Sipariş fiyat listesi butonu
        Button fiyatListesiButton = new Button("Sipariş Fiyat Listesi");
        fiyatListesiButton.setOnAction(e -> {
            showUrunListesiPopup();
        });
        gridPane.add(fiyatListesiButton, 0, MAX_MASALAR / MASALARI_SIRALA + 2, 2, 1);

        Scene scene = new Scene(gridPane, 800, 600);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button createMasaButton(Masa masa) {
        Button masaButton = new Button("Masa " + masa.getMasaNo());
        masaButton.setMinSize(100, 100);

        // Masa tıklanabilir olduğunda gerçekleşecek olayları tanımla
        masaButton.setOnAction(e -> {
            if (masa.isDolu()) {
                // Masa zaten doluysa popup ekranı açma
                System.out.println("Masa " + masa.getMasaNo() + " zaten dolu!");
            } else {
                // Masa boşsa popup ekranını aç
                showPopup(masa);
            }
        });

        return masaButton;
    }

    private void showPopup(Masa masa) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Ürün Bilgisi");

        GridPane popupGrid = new GridPane();
        popupGrid.setAlignment(Pos.CENTER);
        popupGrid.setHgap(10);
        popupGrid.setVgap(10);
        popupGrid.setPadding(new Insets(20, 20, 20, 20));

        // Ürün Adı
        Label urunAdiLabel = new Label("Ürün Adı:");
        TextField urunAdiField = new TextField();
        popupGrid.add(urunAdiLabel, 0, 0);
        popupGrid.add(urunAdiField, 1, 0);

        // Fiyat
        Label fiyatLabel = new Label("Fiyat:");
        TextField fiyatField = new TextField();
        popupGrid.add(fiyatLabel, 0, 1);
        popupGrid.add(fiyatField, 1, 1);

        // Kaydet butonu
        Button kaydetButton = new Button("Kaydet");
        kaydetButton.setOnAction(event -> {
            String urunAdi = urunAdiField.getText();
            String fiyatStr = fiyatField.getText();

            if (isValidUrunAdi(urunAdi) && isValidFiyat(fiyatStr)) {
                double fiyat = Double.parseDouble(fiyatStr);

                // Ürün bilgilerini kaydet
                urunListesi.urunEkle(urunAdi, fiyat);

                // Popup ekranı kapat
                popupStage.close();
            } else {
                // Geçerli bir ürün adı ve fiyat girilmediyse uyarı ver
                showAlert("Geçerli bir ürün adı ve fiyat giriniz.");
            }
        });
        popupGrid.add(kaydetButton, 1, 2);

        Scene popupScene = new Scene(popupGrid, 300, 200);
        popupStage.setScene(popupScene);
        popupStage.showAndWait();
    }

    private boolean isValidUrunAdi(String urunAdi) {
        // Sadece büyük ve küçük harfleri kabul et
        return urunAdi.matches("^[a-zA-Z]+$");
    }

    private boolean isValidFiyat(String fiyatStr) {
        // Sadece sayıları kabul et
        return fiyatStr.matches("^\\d*\\.?\\d+$");
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Uyarı");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showUrunListesiPopup() {
        urunListesi.showUrunListesiPopup();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
