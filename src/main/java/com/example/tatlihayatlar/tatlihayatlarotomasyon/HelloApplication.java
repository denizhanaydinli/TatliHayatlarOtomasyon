package com.example.tatlihayatlar.tatlihayatlarotomasyon;

import com.example.tatlihayatlar.tatlihayatlarotomasyon.Entity.Masa;
import com.example.tatlihayatlar.tatlihayatlarotomasyon.Product.UrunListesi;
import javafx.application.Application;
import javafx.application.Platform;
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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        List<String> masaUrunAdlari = new ArrayList<>();  // Masa için kaydedilmiş ürün adları

        // Eğer masa dolu ise bilgileri çek
        try {
            // Masa ile ilgili bilgileri çek
            // Bu kısımda ilgili veritabanı sorgularını kullanmalısınız.
            // resultSet'den masaUrunAdi ve masaUrunFiyati'ni çekmelisiniz.

            // Örnek:
            // ResultSet resultSet = statement.executeQuery("SELECT * FROM siparisler WHERE masa_id = " + masa.getMasaNo());
            // while (resultSet.next()) {
            //    masaUrunAdlari.add(resultSet.getString("urun_adi"));
            // }

            // Eğer veritabanı kullanıyorsanız, gerçek veritabanı sorgularını kullanmalısınız.
            // Aşağıdaki satırlar sadece bir örnek olarak verilmiştir ve gerçek veritabanı yapınıza göre uyarlanmalıdır.

            //    masaUrunAdlari.add("Ürün adı 1");  // Örnek değer, gerçek veritabanı değeri ile değiştirilmeli
            //   masaUrunAdlari.add("Ürün adı 2");  // Örnek değer, gerçek veritabanı değeri ile değiştirilmeli

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Sipariş bilgileri alınırken bir hata oluştu.");
        }

        VBox urunBilgisiVBox = new VBox();
        urunBilgisiVBox.setSpacing(10);
        urunBilgisiVBox.setAlignment(Pos.TOP_LEFT);

        for (String masaUrunAdi : masaUrunAdlari) {
            urunBilgisiVBox.getChildren().add(new Label(masaUrunAdi));
        }

        // Ürün listesini ve butonları ekle
        VBox urunlerVBox = new VBox();
        urunlerVBox.setSpacing(10);
        urunlerVBox.setAlignment(Pos.TOP_LEFT);
        final double[] toplamFiyat = {0.0};
        Label toplamFiyatLabel = new Label();

        try {
            List<String> urunAdlari = urunListesi.getUrunListesiAdlari();
            for (String urunAdi : urunAdlari) {
                Button urunButton = new Button(urunAdi);

                urunButton.setOnAction(event -> {
                    try {
                            showAlert("Ürün seçildi: " + urunAdi);
                            // Seçilen ürünü VBox'a ekleyin
                            urunBilgisiVBox.getChildren().add(new Label(urunAdi));

                            //--
                        toplamFiyat[0] += getNumericValue(urunAdi);
                        toplamFiyatLabel.setText("Genel Toplam: " + toplamFiyat[0] + " TL");

                        urunBilgisiVBox.getChildren().remove(toplamFiyatLabel);
                        urunBilgisiVBox.getChildren().add(toplamFiyatLabel);

                    } catch (Exception e) {
                        e.printStackTrace();
                        System.err.println("Hata: " + e.getMessage());
                    }
                });


                urunlerVBox.getChildren().add(urunButton);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ürün listesi alınırken bir hata oluştu.");
        }

        urunBilgisiVBox.getChildren().add(toplamFiyatLabel);

        // "Siparişleri Temizle" butonunu oluştur
        Button temizleButton = new Button("Siparişleri Temizle");
        temizleButton.setOnAction(e -> {
            urunBilgisiVBox.getChildren().clear(); // Siparişleri temizle
            toplamFiyat[0] = 0.0; // Toplam fiyatı sıfırla
            toplamFiyatLabel.setText("Genel Toplam: 0.0 TL"); // Toplam fiyatı sıfırla
        });


        HBox urunBilgisiBox = new HBox(urunlerVBox, urunBilgisiVBox,temizleButton);
        urunBilgisiBox.setAlignment(Pos.TOP_LEFT);
        urunBilgisiBox.setSpacing(10);

        masaBilgisiPanel.getChildren().addAll(masaBilgisiLabel, urunBilgisiBox);

    }

    private double getNumericValue(String input) {
        // Sayısal ifadeleri bulmak için regex pattern'ı
        Pattern pattern = Pattern.compile("\\d+(\\.\\d+)?");

        // Matcher nesnesi oluştur
        Matcher matcher = pattern.matcher(input);

        // Eşleşen ifadeleri bul
        while (matcher.find()) {
            // Eşleşen sayısal ifadeyi dönüştür ve geriye döndür
            return Double.parseDouble(matcher.group());
        }

        return 0.0; // Sayısal ifade bulunamazsa 0.0 döndür
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
                    urunListesi.urunSil(selectedUrun.split(" : ")[0]);
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
