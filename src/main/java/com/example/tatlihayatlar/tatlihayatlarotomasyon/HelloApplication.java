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
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.PrinterName;



import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.IOException;
import java.util.stream.Collectors;

public class HelloApplication extends Application {


    private static final int MAX_MASALAR = 20;
    private static final int MASALARI_SIRALA = 5;

    private final UrunListesi urunListesi = new UrunListesi();

    private ListView<String> urunListView = new ListView<>();
    private VBox masaBilgisiPanel = new VBox();
    private final List<VBox> masaBilgisiPanels = new ArrayList<>();
    private final List<Integer> masaNumbers = new ArrayList<>();

    private final List<ListView<String>> masaUrunListeleri = new ArrayList<>();

    private List<String> masaUrunVeFiyatListesi = new ArrayList<>();
    private final double[] toplamFiyat = {0.0};
    private final List<List<String>> masaUrunVeFiyatListeleri = new ArrayList<>();
    private final double[][] masaToplamFiyatArray = new double[MAX_MASALAR][1];

    private final Map<Integer, List<String>> masaUrunVeToplamListesiMap = new HashMap<>();

    private final ToplamTutucu toplamTutucu = new ToplamTutucu();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Cafe Tatlı Hayatlar");
        primaryStage.getIcons().add(new Image("/tatli_hayat.png"));

        // GridPane içinde Logo ve Masaları oluştur
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Masaları oluştur
        for (int i = 0; i < MAX_MASALAR; i++) {
            Masa masaEntity = new Masa(i + 1, false);
            Button masaButton = createMasaButton(masaEntity);

            ListView<String> masaUrunListView = new ListView<>();
            masaUrunListeleri.add(masaUrunListView);

            VBox masaBilgisiPanel = new VBox();
            masaBilgisiPanel.setAlignment(Pos.TOP_LEFT);
            masaBilgisiPanel.setSpacing(10);
            masaBilgisiPanel.setPadding(new Insets(10));
            masaBilgisiPanels.add(masaBilgisiPanel);

            gridPane.add(masaButton, i % MASALARI_SIRALA, i / MASALARI_SIRALA + 1);

            masaUrunVeToplamListesiMap.put(masaEntity.getMasaNo(), new ArrayList<>());
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
        VBox masaBilgisiPanelForMasa = masaBilgisiPanels.get(masa.getMasaNo() - 1);
        VBox currentMasaBilgisiPanel = new VBox();



        Label masaBilgisiLabel = new Label("Masa Bilgisi - Masa " + masa.getMasaNo());
        masaBilgisiLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        // Masaya ait sipariş bilgilerini getir
        //List<String> masaUrunAdlari = new ArrayList<>();  // Masa için kaydedilmiş ürün adları
        List<String> masaUrunAdlari = masaUrunVeToplamListesiMap.get(masa.getMasaNo());


        VBox urunBilgisiVBox = new VBox();
        urunBilgisiVBox.setSpacing(10);
        urunBilgisiVBox.setAlignment(Pos.TOP_LEFT);

        for (String masaUrunAdi : masaUrunAdlari) {
            urunBilgisiVBox.getChildren().add(new Label(masaUrunAdi));
        }

        ListView<String> masaUrunListView = masaUrunListeleri.get(masa.getMasaNo() - 1);
        masaUrunListView.setPrefSize(200, 200);

        // Ürün listesini ve butonları ekle
        VBox urunlerVBox = new VBox();
        urunlerVBox.setSpacing(10);
        urunlerVBox.setAlignment(Pos.TOP_LEFT);
        double[] toplamFiyat = masaToplamFiyatArray[masa.getMasaNo() - 1];
        Label toplamFiyatLabel = new Label();

        //List<String> urunVeToplamListesi = new ArrayList<>();
        List<String> urunVeToplamListesi = masaUrunVeToplamListesiMap.get(masa.getMasaNo());


        masaUrunListView.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                String selectedUrun = masaUrunListView.getSelectionModel().getSelectedItem();
                if (selectedUrun != null && selectedUrun.startsWith("Genel Toplam:")) {
                    // "Genel Toplam" tıklanırsa işlem yapma
                    return;
                }

                masaUrunListView.getItems().removeIf(item -> item.startsWith("Genel Toplam:"));

                toplamFiyat[0] -= getNumericValue(selectedUrun);

                masaUrunListView.getItems().remove(selectedUrun);

                urunVeToplamListesi.remove(selectedUrun);

                // Toplam fiyatı güncelle

                masaUrunListView.getItems().add("Genel Toplam: " + toplamFiyat[0] + " TL");

                masaToplamFiyatArray[masa.getMasaNo() - 1] = toplamFiyat;

            }
        });
        //List<String> urunVeToplamListesi = new ArrayList<>();
        try {
            List<String> urunAdlari = urunListesi.getUrunListesiAdlari();

            for (String urunAdi : urunAdlari) {
                Button urunButton = new Button(urunAdi);

                urunButton.setOnAction(event -> {
                    try {
                        Optional<ButtonType> result = showAlertWithConfirmation("Ürün seçildi: " + urunAdi);
                        if (result.isPresent() && result.get() == ButtonType.OK) {
                            urunBilgisiVBox.getChildren().add(new Label(urunAdi));

                            toplamFiyat[0] += getNumericValue(urunAdi);
                            toplamFiyatLabel.setText("Genel Toplam: " + toplamFiyat[0] + " TL");

                            urunVeToplamListesi.add(urunAdi);

                            masaUrunListView.getItems().setAll(urunVeToplamListesi);
                            masaUrunListView.getItems().add("Genel Toplam: " + toplamFiyat[0] + " TL");


                            urunBilgisiVBox.getChildren().remove(toplamFiyatLabel);
                            urunBilgisiVBox.getChildren().add(toplamFiyatLabel);

                            masaUrunVeToplamListesiMap.put(masa.getMasaNo(), urunVeToplamListesi);
                        }

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
            masaUrunListView.getItems().clear();

            // Siparişleri temizle
            urunBilgisiVBox.getChildren().clear();

            // Toplam fiyatı sıfırla
            toplamFiyat[0] = 0.0;

            // Toplam fiyat label'ını güncelle
            masaUrunListView.getItems().removeAll(urunVeToplamListesi);
            masaUrunListView.getItems().clear();
            urunVeToplamListesi.clear();
            masaUrunVeToplamListesiMap.put(masa.getMasaNo(), new ArrayList<>());

        });

        Button fisOlusturButton = new Button("Fiş Oluştur");
        fisOlusturButton.setOnAction(e -> {
            if (urunVeToplamListesi.isEmpty()) {
                showAlert("Fiş oluşturmak için sipariş eklemelisiniz.");
            } else {
                printFis(urunVeToplamListesi, toplamFiyat[0]);
            }
        });


        HBox urunBilgisiBox = new HBox(urunlerVBox, urunBilgisiVBox,temizleButton,fisOlusturButton);
        urunBilgisiBox.setAlignment(Pos.TOP_LEFT);
        urunBilgisiBox.setSpacing(10);
        masaBilgisiPanel.getChildren().addAll(masaBilgisiLabel, masaUrunListView);

        if (!masaNumbers.contains(masa.getMasaNo())) {
            masaNumbers.add(masa.getMasaNo());
            currentMasaBilgisiPanel.getChildren().addAll(masaBilgisiLabel, urunBilgisiBox);
        }
            masaBilgisiPanelForMasa.getChildren().add(currentMasaBilgisiPanel);
            masaBilgisiPanel.getChildren().add(masaBilgisiPanelForMasa);


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

    private Optional<ButtonType> showAlertWithConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Onay");
        alert.setHeaderText(null);
        alert.setContentText(message);

        return alert.showAndWait();
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

    //eğer fişi pdf değilde txt ye basmak istiyorsan bu metodu kullan
    private void printFis(List<String> urunVeToplamListesi, double toplamFiyat) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("fis.txt"))) {
            writer.write("Cafe Tatlı Hayatlar\n\n");
            writer.write("---------------------\n");
            for (String urun : urunVeToplamListesi) {
                writer.write(urun + "\n");
            }
            writer.write("---------------------\n");
            writer.write("Genel Toplam: " + toplamFiyat + " TL\n");

            showAlert("Fiş başarıyla oluşturuldu. (fis.txt)");

            // Aylık, haftalık ve günlük toplamları güncelle
            toplamTutucu.addAylarToplam(toplamFiyat);
            toplamTutucu.addHaftalarToplam(toplamFiyat);
            toplamTutucu.addGunlerToplam(toplamFiyat);

            // Yazdırma işlemi
            // printDocument(new File("fis.txt"));

            // Belirli bir yazıcıya gönder.aşağıdaki satırda printerın adını yazarak ilgili yazıcıdan çıktı alması sağlanabilir.bu durumda kodu comment out edilecek
            // printDocument(new File("fis.txt"), "samsunglaser");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Fiş oluşturulurken bir hata oluştu.");
        }
    }

  /*  private void printDocument(File file) {
        try {
            // Yazıcı adını belirle (örneğin, varsayılan yazıcıyı kullanalım)
            PrintService printService = PrintServiceLookup.lookupDefaultPrintService();

            if (printService != null) {
                DocFlavor[] supportedFlavors = printService.getSupportedDocFlavors();

                System.out.println("Desteklenen Belge Türleri:");

                for (DocFlavor flavor : supportedFlavors) {
                    System.out.println(flavor);
                }
            } else {
                System.out.println("Varsayılan yazıcı bulunamadı.");
            }

            // Print işlemi için özellikleri belirle
            PrintRequestAttributeSet attributeSet = new HashPrintRequestAttributeSet();
           // attributeSet.add(new PrinterName(printService.getName(), DocFlavor.BYTE_ARRAY.AUTOSENSE));
            attributeSet.add(new Copies(1));

            // Yazdırma işlemi için hazırlık yap
            DocPrintJob printJob = printService.createPrintJob();
            FileInputStream fis = new FileInputStream(file);
            DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
            Doc doc = new SimpleDoc(fis, flavor, null);

            // Yazdırma işlemi başlat
            printJob.print(doc, attributeSet);

            // Kaynakları serbest bırak
            fis.close();
        } catch (PrintException | IOException e) {
            e.printStackTrace();
            showAlert("Yazdırma işleminde bir hata oluştu.");
        }
    }*/

    //manuel printer service için kod
    /*
    private PrintService findPrintService(String printerName) {
        // Tüm yazıcı servislerini al
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);

        // İstenen yazıcıyı bul
        for (PrintService service : printServices) {
            if (service.getName().equalsIgnoreCase(printerName)) {
                return service;
            }
        }

        return null; // İstenen yazıcı bulunamazsa null döndür
    }

    private void printDocument(File file, String printerName) {
        try {
            // Yazıcı adını belirle
            PrintService printService = findPrintService(printerName);

            if (printService == null) {
                showAlert("Belirtilen yazıcı bulunamadı.");
                return;
            }

            // Print işlemi için özellikleri belirle
            PrintRequestAttributeSet attributeSet = new HashPrintRequestAttributeSet();
            attributeSet.add(new PrinterName(printService.getName(), null));
            attributeSet.add(new Copies(1));

            // Yazdırma işlemi için hazırlık yap
            DocPrintJob printJob = printService.createPrintJob();
            FileInputStream fis = new FileInputStream(file);
            DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
            Doc doc = new SimpleDoc(fis, flavor, null);

            // Yazdırma işlemi başlat
            printJob.print(doc, attributeSet);

            // Kaynakları serbest bırak
            fis.close();
        } catch (PrintException | IOException e) {
            e.printStackTrace();
            showAlert("Yazdırma işleminde bir hata oluştu.");
        }
    }*/
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
