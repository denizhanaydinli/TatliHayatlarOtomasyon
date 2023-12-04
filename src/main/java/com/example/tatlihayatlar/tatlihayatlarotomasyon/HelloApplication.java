package com.example.tatlihayatlar.tatlihayatlarotomasyon;

import com.example.tatlihayatlar.tatlihayatlarotomasyon.Entity.Masa;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    private static final int MAX_MASALAR = 20;
    private static final int MASALARI_SIRALA = 5;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Tatlı Hayatlar Cafe");

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Masaları oluştur
        for (int i = 0; i < MAX_MASALAR; i++) {
            Masa masaEntity = new Masa(i + 1, false);
            Button masaButton = createMasaButton(masaEntity);
            gridPane.add(masaButton, i % MASALARI_SIRALA, i / MASALARI_SIRALA);
        }

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
                System.out.println("Masa " + masa.getMasaNo() + " durumu değişti!");
                // Masa boşsa istediğiniz işlemleri yapabilirsiniz
                // Örneğin, sipariş alma, ödeme yapma vb.

                // Masa durumunu güncelle
                masa.setDolu(false);

                // Masa rengini eski haline döndür
                masaButton.setStyle("-fx-background-color: ;");
            } else {
                System.out.println("Masa " + masa.getMasaNo() + " seçildi!");
                // Masa boşsa istediğiniz işlemleri yapabilirsiniz
                // Örneğin, sipariş alma, ödeme yapma vb.

                // Masa rengini kırmızıya ayarla
                masaButton.setStyle("-fx-background-color: red;");
                // Masa durumunu güncelle
                masa.setDolu(true);
            }
        });

        return masaButton;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
