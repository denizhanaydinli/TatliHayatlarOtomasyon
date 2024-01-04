package com.example.tatlihayatlar.tatlihayatlarotomasyon;
import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
public class ToplamTutucu {
    private Connection connection;
    private static final String FILE_PATH = "toplamlar.txt";

    public ToplamTutucu() {
        initializeDatabase();
        createTable();
    }

    private void initializeDatabase() {
        try {
            // JDBC sürücüsünü yükle
            Class.forName("org.sqlite.JDBC");

            // Veritabanına bağlantı oluştur
            connection = DriverManager.getConnection("jdbc:sqlite:cafe.db");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS toplamlar (id INTEGER PRIMARY KEY AUTOINCREMENT, tarih TEXT, tip TEXT, toplam REAL)";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addToplam(String tip, double toplam) {
        String tarih = LocalDate.now().toString();
        String insertSQL = "INSERT INTO toplamlar (tarih, tip, toplam) VALUES (?, ?, ?)";
        String updateSQL = "UPDATE toplamlar SET toplam = ? WHERE tarih = ? AND tip = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setString(1, tarih);
            preparedStatement.setString(2, tip);
            preparedStatement.setDouble(3, toplam);

            int rowsAffected = preparedStatement.executeUpdate();

            // Eğer etkilenen satır sayısı 0 ise (yani aynı tarih ve tip için zaten bir kayıt varsa), güncelleme yapalım.
            if (rowsAffected == 0) {
                try (PreparedStatement updateStatement = connection.prepareStatement(updateSQL)) {
                    updateStatement.setDouble(1, toplam);
                    updateStatement.setString(2, tarih);
                    updateStatement.setString(3, tip);
                    updateStatement.executeUpdate();
                }
            }

            // Sadece yeni bir fiş oluşturulduğunda ve belirli bir tarih için dosyaya yazma işlemi gerçekleştirilecek.
            if (rowsAffected == 1 && !"Günlük".equals(tip) && !"Haftalık".equals(tip) && !"Aylık".equals(tip)) {
                writeToplamlarToFile();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void writeToplamlarToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH),false)) {
            String selectSQL = "SELECT tarih, tip, toplam FROM toplamlar ORDER BY tarih DESC LIMIT 6";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(selectSQL)) {
                while (resultSet.next()) {
                    String tarih = resultSet.getString("tarih");
                    String tip = resultSet.getString("tip");
                    double toplam = resultSet.getDouble("toplam");
                    String line = tarih + " - " + tip + ": " + toplam + " TL";
                    writer.println(line);
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public void printToplamlar() {
        String selectSQL = "SELECT tarih, tip, toplam FROM toplamlar ORDER BY tarih DESC LIMIT 6";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectSQL)) {
            while (resultSet.next()) {
                String tarih = resultSet.getString("tarih");
                String tip = resultSet.getString("tip");
                double toplam = resultSet.getDouble("toplam");
                System.out.println(tarih + " - " + tip + ": " + toplam + " TL");
                writeToFile(tarih + " - " + tip + ": " + toplam + " TL", "toplamlar.txt");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void writeToFile(String content, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            // Sadece belirli bir tarih için bir kere yazma kontrolü yapalım.
            if (!isDateAlreadyWritten(content)) {
                writer.write(content + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Verilen içerikteki tarihin daha önce yazılıp yazılmadığını kontrol etmek için kullanılır.
    private boolean isDateAlreadyWritten(String content) {
        String[] lines = content.split("\n");
        if (lines.length > 0) {
            String lastLine = lines[lines.length - 1];
            // Tarih kısmını alalım (örneğin: 2024-01-03)
            String datePart = lastLine.substring(0, 10);
            // Dosyada bu tarih var mı kontrol edelim.
            return lines.length > 1 && content.contains(datePart);
        }
        return false;
    }

    private void saveToFile() {
        String selectSQL = "SELECT tarih, tip, toplam FROM toplamlar";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectSQL)) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
                while (resultSet.next()) {
                    String tarih = resultSet.getString("tarih");
                    String tip = resultSet.getString("tip");
                    double toplam = resultSet.getDouble("toplam");
                    String line = tarih + " - " + tip + ": " + toplam + " TL";
                    writer.write(line + "\n");
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFromFile() {
        // Veritabanından okuma işlemi yapmak yerine dosyadan okuma işlemi gerçekleştirilecek.
        // Bu nedenle bu metodun içeriği değişecek.
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addAylarToplam(double toplam) {
        double aylarToplam = getAylarToplam() + toplam;
        setAylarToplam(aylarToplam);
        addToplam("Aylık", toplam);
        saveToFile();
    }

    public void addHaftalarToplam(double toplam) {
        double haftalarToplam = getHaftalarToplam() + toplam;
        setHaftalarToplam(haftalarToplam);
        addToplam("Haftalık", toplam);
        saveToFile();
    }

    public void addGunlerToplam(double toplam) {
        double gunlerToplam = getGunlerToplam() + toplam;
        setGunlerToplam(gunlerToplam);
        addToplam("Günlük", toplam);
        saveToFile();
    }

    private double getAylarToplam() {
        return readToplam("Aylık");
    }

    private void setAylarToplam(double toplam) {
        writeToplam("Aylık", toplam);
    }

    private double getHaftalarToplam() {
        return readToplam("Haftalık");
    }

    private void setHaftalarToplam(double toplam) {
        writeToplam("Haftalık", toplam);
    }

    private double getGunlerToplam() {
        return readToplam("Günlük");
    }

    private void setGunlerToplam(double toplam) {
        writeToplam("Günlük", toplam);
    }

    private double readToplam(String tip) {
        String selectSQL = "SELECT toplam FROM toplamlar WHERE tip = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
            preparedStatement.setString(1, tip);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() ? resultSet.getDouble("toplam") : 0.0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    private void writeToplam(String tip, double toplam) {
        String updateSQL = "UPDATE toplamlar SET toplam = ? WHERE tip = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
            preparedStatement.setDouble(1, toplam);
            preparedStatement.setString(2, tip);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ToplamTutucu toplamTutucu = new ToplamTutucu();
        toplamTutucu.printToplamlar();
    }
}
