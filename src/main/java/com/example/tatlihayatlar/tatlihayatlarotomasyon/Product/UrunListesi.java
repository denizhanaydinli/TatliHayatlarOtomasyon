package com.example.tatlihayatlar.tatlihayatlarotomasyon.Product;
import com.example.tatlihayatlar.tatlihayatlarotomasyon.Entity.Urun;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class UrunListesi {

    private Connection connection;

    public UrunListesi() {
        initializeDatabase();
        createTable();
    }

    private void initializeDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:cafe.db");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS urunler (id INTEGER PRIMARY KEY AUTOINCREMENT, ad TEXT, fiyat REAL)";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void urunEkle(String urunAdi, double fiyat) {
        String insertSQL = "INSERT INTO urunler (ad, fiyat) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setString(1, urunAdi);
            preparedStatement.setDouble(2, fiyat);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void urunGuncelle(int urunId, String yeniAd, double yeniFiyat) {
        String updateSQL = "UPDATE urunler SET ad = ?, fiyat = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
            preparedStatement.setString(1, yeniAd);
            preparedStatement.setDouble(2, yeniFiyat);
            preparedStatement.setInt(3, urunId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void urunSil(int urunId) {
        String deleteSQL = "DELETE FROM urunler WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {
            preparedStatement.setInt(1, urunId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getUrunListesiAdlari() throws SQLException {
        List<String> urunListesi = new ArrayList<>();
        String selectSQL = "SELECT * FROM urunler";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectSQL)) {
            while (resultSet.next()) {
                String urunAdiFiyat = resultSet.getString("ad") + ": " + resultSet.getDouble("fiyat");
                urunListesi.add(urunAdiFiyat);
            }
        }
        return urunListesi;
    }

    public List<Urun> getUrunListesi() throws SQLException {
        List<Urun> urunler = new ArrayList<>();
        String selectSQL = "SELECT * FROM urunler";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectSQL)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String ad = resultSet.getString("ad");
                double fiyat = resultSet.getDouble("fiyat");
                Urun urun = new Urun(id, ad, fiyat);
                urunler.add(urun);
            }
        }
        return urunler;
    }
}
