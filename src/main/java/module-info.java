module com.example.tatlihayatlar.tatlihayatlarotomasyon {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.tatlihayatlar.tatlihayatlarotomasyon to javafx.fxml;
    exports com.example.tatlihayatlar.tatlihayatlarotomasyon;
}