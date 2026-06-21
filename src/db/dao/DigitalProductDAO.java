package db.dao;

import db.DatabaseConnection;
import model.DigitalProduct;
import model.ArtStyle;
import model.DigProductType;
import network.dto.DigitalProductDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DigitalProductDAO {
    private final ArtProductDAO artProductDAO;

    public DigitalProductDAO(ArtProductDAO artProductDAO) {
        this.artProductDAO = artProductDAO;
        createTable();
    }

    private void createTable() {

        String sql = "CREATE TABLE IF NOT EXISTS digital_products ("
                + "product_id INTEGER PRIMARY KEY,"
                + "style TEXT NOT NULL,"
                + "download_url TEXT NOT NULL,"
                + "file_size_mb REAL NOT NULL,"
                + "file_extension TEXT NOT NULL,"
                + "dig_product_type TEXT NOT NULL,"
                + "FOREIGN KEY (product_id) REFERENCES art_products(id) ON DELETE CASCADE"
                + ");";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error creating digital_products table: " + e.getMessage());
        }
    }

    public void insertDigitalProduct(DigitalProduct product) {
        artProductDAO.insertArtProduct(product);

        String sql = "INSERT INTO digital_products (product_id, style, download_url, file_size_mb, file_extension, dig_product_type) VALUES (?, ?, ?, ?, ?, ?);";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, product.getId());
            pstmt.setString(2, product.getStyle().name());
            pstmt.setString(3, product.getDownloadURL());
            pstmt.setDouble(4, product.getFileSizeMb());
            pstmt.setString(5, product.getFileExtension());
            pstmt.setString(6, product.getDigProductType().name());

            pstmt.executeUpdate();
            System.out.println("DigitalProduct successfully inserted " + product.getName());
        } catch (SQLException e) {
            System.err.println("Error inserting digital product details: " + e.getMessage());
        }
    }

    public void updateDigitalProduct(DigitalProduct product) {
        artProductDAO.updateArtProduct(product);

        String sql = "UPDATE digital_products SET style = ?, download_url = ?, file_size_mb = ?, file_extension = ?, dig_product_type = ? WHERE product_id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, product.getStyle().name());
            pstmt.setString(2, product.getDownloadURL());
            pstmt.setDouble(3, product.getFileSizeMb());
            pstmt.setString(4, product.getFileExtension());
            pstmt.setString(5, product.getDigProductType().name());
            pstmt.setInt(6, product.getId());

            pstmt.executeUpdate();
            System.out.println("DigitalProduct successfully updated: " + product.getName());

        } catch (SQLException e) {
            System.err.println("Error updating digital product details: " + e.getMessage());
        }
    }

    public void deleteDigitalProduct(int id) {
        artProductDAO.deleteArtProduct(id);
    }

    public DigitalProduct getDigitalProductById(int id) {
        model.ArtProduct baseProduct = artProductDAO.getArtProductById(id);
        if (baseProduct == null) return null;

        String sql = "SELECT * FROM digital_products WHERE product_id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            var rs = pstmt.executeQuery();

            if (rs.next()) {
                return new DigitalProduct(
                        baseProduct.getId(),
                        baseProduct.getName(),
                        baseProduct.getDescription(),
                        baseProduct.getBasePrice(),
                        baseProduct.getArtist(),
                        ArtStyle.valueOf(rs.getString("style")),
                        DigProductType.valueOf(rs.getString("dig_product_type")),
                        rs.getString("file_extension"),
                        rs.getDouble("file_size_mb"),
                        rs.getString("download_url")
                );
            } else {
                System.out.println("No digital details found for product ID: " + id);
                return null;
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving digital product: " + e.getMessage());
            return null;
        }
    }
    public List<DigitalProduct> getAllDigitalProducts() {
        List<DigitalProduct> products = new ArrayList<>();
        String sql = "SELECT product_id FROM digital_products;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             var rs = pstmt.executeQuery()) {

            while (rs.next()) {
                DigitalProduct p = getDigitalProductById(rs.getInt("product_id"));
                if (p != null) {
                    products.add(p);
                }
            }
            return products;

        } catch (SQLException e) {
            System.err.println("Error retrieving all digital products: " + e.getMessage());
            return products;
        }
    }

    public DigitalProductDTO toDTO(DigitalProduct p) {
        return new DigitalProductDTO(
                p.getId(), p.getArtist().getId(), p.getName(), p.getDescription(), p.getBasePrice(),
                p.getArtist().getUsername(), p.getStyle().name(), p.getDownloadURL(),
                p.getFileSizeMb(), p.getFileExtension(), p.getDigProductType().name()
        );
    }
}