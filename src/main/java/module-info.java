module com.example.tatlihayatlar.tatlihayatlarotomasyon {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.tatlihayatlar.tatlihayatlarotomasyon to javafx.fxml;
    exports com.example.tatlihayatlar.tatlihayatlarotomasyon;
}