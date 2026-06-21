package db.dao;

import db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class PurchaseDAO {

    public PurchaseDAO() {
        createTable();
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS purchases ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "buyer_id INTEGER NOT NULL,"
                + "product_id INTEGER NOT NULL,"
                + "purchase_date TEXT NOT NULL,"
                + "FOREIGN KEY (buyer_id) REFERENCES users(id) ON DELETE CASCADE,"
                + "FOREIGN KEY (product_id) REFERENCES art_products(id) ON DELETE CASCADE"
                + ");";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error creating purchases table: " + e.getMessage());
        }
    }

    public synchronized int insertPurchase(int buyerId, int productId) {
        String sql = "INSERT INTO purchases (buyer_id, product_id, purchase_date) VALUES (?, ?, ?);";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, buyerId);
            pstmt.setInt(2, productId);
            pstmt.setString(3, java.time.LocalDate.now().toString());
            pstmt.executeUpdate();

            try (var generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    System.out.println("Purchase recorded: buyer " + buyerId + " -> product " + productId);
                    return id;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting purchase: " + e.getMessage());
        }
        return -1;
    }
}
