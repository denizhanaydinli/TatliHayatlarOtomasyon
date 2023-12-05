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
import javafx.scene.layout.*;
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

    private ListView<String> urunListView = new ListView<>();
    private VBox masaBilgisiPanel = new VBox();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Cafe Tatlı Hayatlar");

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

        // Sağ tarafta gösterilecek olan Masa Bilgisi Paneli
        masaBilgisiPanel.setAlignment(Pos.TOP_LEFT);
        masaBilgisiPanel.setSpacing(10);
        masaBilgisiPanel.setPadding(new Insets(10));

        // Sipariş fiyat listesi butonu
        Button fiyatListesiButton = new Button("Sipariş Fiyat Listesi");
        fiyatListesiButton.setOnAction(e -> {
            showUrunListesiPopup();
        });
        gridPane.add(fiyatListesiButton, 0, MAX_MASALAR / MASALARI_SIRALA + 2, 2, 1);

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(gridPane);
        borderPane.setRight(masaBilgisiPanel);

        Scene scene = new Scene(borderPane, 800, 600);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button createMasaButton(Masa masa) {
        Button masaButton = new Button("Masa " + masa.getMasaNo());
        masaButton.setMinSize(100, 100);

        // Masa tıklanabilir olduğunda gerçekleşecek olayları tanımla
        masaButton.setOnAction(e -> {
            showMasaBilgisi(masa);
        });

        return masaButton;
    }

    private void showMasaBilgisi(Masa masa) {
        masaBilgisiPanel.getChildren().clear();

        Label masaBilgisiLabel = new Label("Masa Bilgisi - Masa " + masa.getMasaNo());
        masaBilgisiLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        // Masaya ait sipariş bilgilerini getir
        String masaUrunAdi = "";  // Masa için kaydedilmiş ürün adı
        double masaUrunFiyati = 0.0;  // Masa için kaydedilmiş ürün fiyatı

        // Eğer masa dolu ise bilgileri çek
        try {
            // Masa ile ilgili bilgileri çek
            // Bu kısımda ilgili veritabanı sorgularını kullanmalısınız.
            // resultSet'den masaUrunAdi ve masaUrunFiyati'ni çekmelisiniz.

            // Örnek:
            // ResultSet resultSet = statement.executeQuery("SELECT * FROM siparisler WHERE masa_id = " + masa.getMasaNo());
            // if (resultSet.next()) {
            //    masaUrunAdi = resultSet.getString("urun_adi");
            //    masaUrunFiyati = resultSet.getDouble("urun_fiyati");
            // }

            // Eğer veritabanı kullanıyorsanız, gerçek veritabanı sorgularını kullanmalısınız.
            // Aşağıdaki satırlar sadece bir örnek olarak verilmiştir ve gerçek veritabanı yapınıza göre uyarlanmalıdır.

            masaUrunAdi = "Ürün adı";  // Örnek değer, gerçek veritabanı değeri ile değiştirilmeli
            masaUrunFiyati = 20.0;  // Örnek değer, gerçek veritabanı değeri ile değiştirilmeli

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Sipariş bilgileri alınırken bir hata oluştu.");
        }

        // Ürün Adı ve Fiyat giriş alanları
        Label urunAdiLabel = new Label("Ürün Adı: " + masaUrunAdi);
        Label fiyatLabel = new Label("Fiyat: " + masaUrunFiyati);

        HBox urunBilgisiBox = new HBox(urunAdiLabel, new Separator(), fiyatLabel);
        urunBilgisiBox.setAlignment(Pos.CENTER_LEFT);
        urunBilgisiBox.setSpacing(10);

        masaBilgisiPanel.getChildren().addAll(masaBilgisiLabel, urunBilgisiBox);

        // Ürün listesini ve butonları ekle
        VBox urunlerVBox = new VBox();
        urunlerVBox.setSpacing(10);
        urunlerVBox.setAlignment(Pos.TOP_LEFT);

        try {
            List<String> urunAdlari = urunListesi.getUrunListesiAdlari();
            for (String urunAdi : urunAdlari) {
                Button urunButton = new Button(urunAdi);
                urunButton.setOnAction(event -> {
                    // Ürün butonuna tıklandığında yapılacak işlemleri buraya ekleyebilirsiniz.
                    showAlert("Ürün seçildi: " + urunAdi);
                });
                urunlerVBox.getChildren().add(urunButton);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ürün listesi alınırken bir hata oluştu.");
        }

        masaBilgisiPanel.getChildren().add(urunlerVBox);
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

        urunListView.setPrefSize(200, 200);

        try {
            List<String> urunAdlari = urunListesi.getUrunListesiAdlari();
            urunListView.getItems().addAll(urunAdlari);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ürün listesi alınırken bir hata oluştu.");
        }

        urunListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selectedUrun = urunListView.getSelectionModel().getSelectedItem();
                if (selectedUrun != null) {
                    urunListesi.urunSil(selectedUrun);
                    refreshUrunListesi(urunListView);
                    showAlert("Ürün başarıyla silindi.");
                }
            }
        });

        urunListesiGrid.add(urunListView, 0, 0);

        TextField urunAdiField = new TextField();
        TextField fiyatField = new TextField();

        urunListesiGrid.add(new Label("Ürün Adı:"), 1, 0);
        urunListesiGrid.add(urunAdiField, 2, 0);
        urunListesiGrid.add(new Label("Fiyat:"), 1, 1);
        urunListesiGrid.add(fiyatField, 2, 1);

        Button kaydetButton = new Button("Kaydet");

        urunListesiGrid.add(kaydetButton, 1, 2);

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
