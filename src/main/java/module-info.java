module com.example.tatlihayatlar.tatlihayatlarotomasyon {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires org.xerial.sqlitejdbc;


    opens com.example.tatlihayatlar.tatlihayatlarotomasyon to javafx.fxml;
    exports com.example.tatlihayatlar.tatlihayatlarotomasyon;
}