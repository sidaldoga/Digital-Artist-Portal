import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCTest {
    public static void main(String[] args) {
        // SQLite dosya tabanlı çalıştığı için bu adres projenizin kök dizininde test.db oluşturur.
        String url = "jdbc:sqlite:test.db";

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                System.out.println("Sürücü başarıyla yüklendi!");
                System.out.println("SQLite Veri tabanına bağlantı başarıyla sağlandı!");
            }
        } catch (SQLException e) {
            System.out.println("Bağlantı hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }
}