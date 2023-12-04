package com.example.tatlihayatlar.tatlihayatlarotomasyon.Product;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.util.Properties;

public class UrunListesi {


    private final Properties urunProperties;

    public UrunListesi() {
        urunProperties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("urunler.properties")) {
            if (input != null) {
                urunProperties.load(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Varsayılan ürün listesini yükle (eğer boşsa)
        if (urunProperties.isEmpty()) {
            urunProperties.setProperty("Kahve", "5.0");
            urunProperties.setProperty("Çay", "3.0");
            urunProperties.setProperty("Pasta", "8.0");
        }
    }

    public void urunEkle(String urunAdi, double fiyat) {
        urunProperties.setProperty(urunAdi, String.valueOf(fiyat));
        // Veritabanına kaydet
        kaydet();
    }

    public double fiyatGetir(String urunAdi) {
        String fiyatStr = urunProperties.getProperty(urunAdi);
        if (fiyatStr != null) {
            return Double.parseDouble(fiyatStr);
        }
        return 0.0;
    }

    public Properties getUrunListesi() {
        return urunProperties;
    }

    public void showUrunListesiPopup() {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Sipariş Fiyat Listesi");

        GridPane popupGrid = new GridPane();
        popupGrid.setAlignment(Pos.CENTER);
        popupGrid.setHgap(10);
        popupGrid.setVgap(10);
        popupGrid.setPadding(new Insets(20, 20, 20, 20));

        int row = 0;
        for (Object key : urunProperties.keySet()) {
            Label urunLabel = new Label(key.toString() + ":");
            Label fiyatLabel = new Label(urunProperties.getProperty(key.toString()));

            popupGrid.add(urunLabel, 0, row);
            popupGrid.add(fiyatLabel, 1, row);

            row++;
        }

        Scene popupScene = new Scene(popupGrid, 300, 200);
        popupStage.setScene(popupScene);
        popupStage.showAndWait();
    }

    private void kaydet() {
        try (OutputStream output = new FileOutputStream("urunler.properties")) {
            urunProperties.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
