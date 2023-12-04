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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
                showPopup(masa);  // Masa doluysa, sipariş bilgisini göster
            } else {
                // Masa boşsa uyarı ver
                showAlert("Masa " + masa.getMasaNo() + " boş. Lütfen önce bir sipariş girin.");
            }
        });

        return masaButton;
    }

    private void showPopup(Masa masa) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Sipariş Bilgisi");

        GridPane popupGrid = new GridPane();
        popupGrid.setAlignment(Pos.CENTER);
        popupGrid.setHgap(10);
        popupGrid.setVgap(10);
        popupGrid.setPadding(new Insets(20, 20, 20, 20));

        // Masaya ait ürün adı ve fiyatını getir
        String masaUrunAdi = "";  // Masa için kaydedilmiş ürün adı
        double masaUrunFiyati = 0.0;  // Masa için kaydedilmiş ürün fiyatı

        // Eğer masa dolu ise bilgileri çek
        if (masa.isDolu()) {
            // Masa ile ilgili bilgileri çek
            // Bu kısımda ilgili veritabanı sorgularını kullanmalısınız.
            // Örneğin, "SELECT * FROM siparisler WHERE masa_id = ?" gibi bir sorgu kullanabilirsiniz.
            // resultSet'den masaUrunAdi ve masaUrunFiyati'ni çekmelisiniz.

            // Örnek:
            // ResultSet resultSet = statement.executeQuery("SELECT * FROM siparisler WHERE masa_id = " + masa.getMasaNo());
            // if (resultSet.next()) {
            //    masaUrunAdi = resultSet.getString("urun_adi");
            //    masaUrunFiyati = resultSet.getDouble("urun_fiyati");
            // }

        }

        // Ürün Adı ve Fiyat giriş alanları
        Label urunAdiLabel = new Label("Ürün Adı:");
        TextField urunAdiField = new TextField(masaUrunAdi);
        urunAdiField.setEditable(false);  // Kullanıcıya düzenleme izni verme
        Label fiyatLabel = new Label("Fiyat:");
        TextField fiyatField = new TextField(String.valueOf(masaUrunFiyati));
        fiyatField.setEditable(false);  // Kullanıcıya düzenleme izni verme

        popupGrid.add(urunAdiLabel, 0, 0);
        popupGrid.add(urunAdiField, 1, 0);
        popupGrid.add(fiyatLabel, 0, 1);
        popupGrid.add(fiyatField, 1, 1);

        // Kaydet butonu
        Button kaydetButton = new Button("Kaydet");
        kaydetButton.setOnAction(event -> {
            // Eğer gerekirse, burada başka işlemler de yapabilirsiniz.
            // Örneğin, yeni bir sipariş oluşturabilir ve veritabanına ekleyebilirsiniz.
            showAlert("Sipariş başarıyla kaydedildi.");
            popupStage.close();
        });
        popupGrid.add(kaydetButton, 1, 2);

        Scene popupScene = new Scene(popupGrid, 300, 200);
        popupStage.setScene(popupScene);
        popupStage.showAndWait();
    }

    private void showUrunListesiPopup() {
        Stage urunListesiStage = new Stage();
        urunListesiStage.initModality(Modality.APPLICATION_MODAL);
        urunListesiStage.setTitle("Sipariş Fiyat Listesi");

        GridPane urunListesiGrid = new GridPane();
        urunListesiGrid.setAlignment(Pos.CENTER);
        urunListesiGrid.setHgap(10);
        urunListesiGrid.setVgap(10);
        urunListesiGrid.setPadding(new Insets(20, 20, 20, 20));

        // Eski ürün adı ve fiyatları gösteren alan
        ListView<String> urunListView = new ListView<>();
        urunListView.setPrefSize(200, 200);

        try {
            List<String> urunAdlari = urunListesi.getUrunListesiAdlari();
            urunListView.getItems().addAll(urunAdlari);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ürün listesi alınırken bir hata oluştu.");
        }

        urunListesiGrid.add(urunListView, 0, 0);

        // Ürün Adı ve Fiyat giriş alanları
        TextField urunAdiField = new TextField();
        TextField fiyatField = new TextField();

        urunListesiGrid.add(new Label("Ürün Adı:"), 1, 0);
        urunListesiGrid.add(urunAdiField, 2, 0);
        urunListesiGrid.add(new Label("Fiyat:"), 1, 1);
        urunListesiGrid.add(fiyatField, 2, 1);

        // Kaydet, Güncelle, Sil butonları
        Button kaydetButton = new Button("Kaydet");
        Button guncelleButton = new Button("Güncelle");
        Button silButton = new Button("Sil");

        urunListesiGrid.add(kaydetButton, 1, 2);
        urunListesiGrid.add(guncelleButton, 2, 2);
        urunListesiGrid.add(silButton, 3, 2);

        // Kaydet butonu
        kaydetButton.setOnAction(event -> {
            String urunAdi = urunAdiField.getText();
            String fiyatStr = fiyatField.getText();

            if (!urunAdi.isEmpty() && !fiyatStr.isEmpty()) {
                double fiyat = Double.parseDouble(fiyatStr);
                urunListesi.urunEkle(urunAdi, fiyat);
                refreshUrunListesi(urunListView);
                showAlert("Ürün başarıyla kaydedildi.");
            } else {
                showAlert("Lütfen tüm alanları doldurun.");
            }
        });

        // Güncelle butonu
        guncelleButton.setOnAction(event -> {
            String selectedUrun = urunListView.getSelectionModel().getSelectedItem();
            if (selectedUrun != null) {
                String[] parts = selectedUrun.split(": ");
                int urunId = Integer.parseInt(parts[0]);
                String urunAdi = urunAdiField.getText();
                String fiyatStr = fiyatField.getText();

                if (!urunAdi.isEmpty() && !fiyatStr.isEmpty()) {
                    double fiyat = Double.parseDouble(fiyatStr);
                    urunListesi.urunGuncelle(urunId, urunAdi, fiyat);
                    refreshUrunListesi(urunListView);
                    showAlert("Ürün başarıyla güncellendi.");
                } else {
                    showAlert("Lütfen tüm alanları doldurun.");
                }
            } else {
                showAlert("Lütfen güncellenecek bir ürün seçin.");
            }
        });

        // Sil butonu
        silButton.setOnAction(event -> {
            String selectedUrun = urunListView.getSelectionModel().getSelectedItem();
            if (selectedUrun != null) {
                String[] parts = selectedUrun.split(": ");
                int urunId = Integer.parseInt(parts[0]);
                urunListesi.urunSil(urunId);
                refreshUrunListesi(urunListView);
                showAlert("Ürün başarıyla silindi.");
            } else {
                showAlert("Lütfen silinecek bir ürün seçin.");
            }
        });

        Scene urunListesiScene = new Scene(urunListesiGrid, 500, 300);
        urunListesiStage.setScene(urunListesiScene);
        urunListesiStage.showAndWait();
    }

    private void refreshUrunListesi(ListView<String> urunListView) {
        urunListView.getItems().clear();
        try {
            List<String> urunAdlari = urunListesi.getUrunListesiAdlari();
            urunListView.getItems().addAll(urunAdlari);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ürün listesi alınırken bir hata oluştu.");
        }
    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Uyarı");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
