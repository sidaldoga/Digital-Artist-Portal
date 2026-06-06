package db;

import model.PhysicalProduct;
import model.ArtStyle;
import model.ProductType;
import model.MediumType;
import model.Dimensions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PhysicalProductDAO {
    private final DatabaseConnection dbConnection;
    private final ArtProductDAO artProductDAO;

    public PhysicalProductDAO(DatabaseConnection dbConnection, ArtProductDAO artProductDAO) {
        this.dbConnection = dbConnection;
        this.artProductDAO = artProductDAO;
        createTable();
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS physical_products ("
                + "product_id INTEGER PRIMARY KEY,"
                + "style TEXT NOT NULL,"
                + "dimensions TEXT NOT NULL,"
                + "weight REAL NOT NULL,"
                + "stock INTEGER NOT NULL,"
                + "medium TEXT NOT NULL,"
                + "product_type TEXT NOT NULL,"
                + "FOREIGN KEY (product_id) REFERENCES art_products(id) ON DELETE CASCADE"
                + ");";
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error creating physical_products table: " + e.getMessage());
        }
    }

    public void insertPhysicalProduct(PhysicalProduct product) {
        artProductDAO.insertArtProduct(product);

        String sql = "INSERT INTO physical_products (product_id, style, dimensions, weight, stock, medium, product_type) VALUES (?, ?, ?, ?, ?, ?, ?);";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, product.getId());
            pstmt.setString(2, product.getStyle().name());
            pstmt.setString(3, product.getDimensionsCm() != null ? product.getDimensionsCm().toString() : "");
            pstmt.setDouble(4, product.getWeight());
            pstmt.setInt(5, product.getStock());
            pstmt.setString(6, product.getMedium().name());
            pstmt.setString(7, product.getProductType().name());

            pstmt.executeUpdate();
            System.out.println("PhysicalProduct successfully inserted " + product.getName());
        } catch (SQLException e) {
            System.err.println("Error inserting physical product details: " + e.getMessage());
        }
    }

    public void updatePhysicalProduct(PhysicalProduct product) {
        artProductDAO.updateArtProduct(product);

        String sql = "UPDATE physical_products SET style = ?, dimensions = ?, weight = ?, stock = ?, medium = ?, product_type = ? WHERE product_id = ?;";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, product.getStyle().name());
            pstmt.setString(2, product.getDimensionsCm() != null ? product.getDimensionsCm().toString() : "");
            pstmt.setDouble(3, product.getWeight());
            pstmt.setInt(4, product.getStock());
            pstmt.setString(5, product.getMedium().name());
            pstmt.setString(6, product.getProductType().name());
            pstmt.setInt(7, product.getId());

            pstmt.executeUpdate();
            System.out.println("PhysicalProduct successfully updated: " + product.getName());

        } catch (SQLException e) {
            System.err.println("Error updating physical product details: " + e.getMessage());
        }
    }

    public void deletePhysicalProduct(int id) {
        artProductDAO.deleteArtProduct(id);
    }

    public PhysicalProduct getPhysicalProductById(int id) {
        model.ArtProduct baseProduct = artProductDAO.getArtProductById(id);
        if (baseProduct == null) return null;

        String sql = "SELECT * FROM physical_products WHERE product_id = ?;";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            var rs = pstmt.executeQuery();

            if (rs.next()) {
                String dimStr = rs.getString("dimensions");
                Dimensions dimObj = null;

                if (dimStr != null && !dimStr.isEmpty() && dimStr.contains("x")) {
                    try {
                        String[] parts = dimStr.split("x");
                        double width = Double.parseDouble(parts[0].replace("cm", "").trim());
                        double height = Double.parseDouble(parts[1].replace("cm", "").trim());
                        double depth = Double.parseDouble(parts[2].replace("cm", "").trim());

                        dimObj = new Dimensions(width, height, depth);
                    } catch (Exception e) {
                        dimObj = new Dimensions(0.0, 0.0, 0.0);
                    }
                }

                return new PhysicalProduct(
                        baseProduct.getId(),
                        baseProduct.getName(),
                        baseProduct.getDescription(),
                        baseProduct.getBasePrice(),
                        baseProduct.getArtist(),
                        ArtStyle.valueOf(rs.getString("style")),
                        ProductType.valueOf(rs.getString("product_type")),
                        MediumType.valueOf(rs.getString("medium")),
                        rs.getInt("stock"),
                        rs.getDouble("weight"),
                        dimObj
                );
            } else {
                System.out.println("No physical details found for product ID: " + id);
                return null;
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving physical product: " + e.getMessage());
            return null;
        }
    }

    public List<PhysicalProduct> getAllPhysicalProducts() {
        List<PhysicalProduct> products = new ArrayList<>();
        String sql = "SELECT product_id FROM physical_products;";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             var rs = pstmt.executeQuery()) {

            while (rs.next()) {
                PhysicalProduct p = getPhysicalProductById(rs.getInt("product_id"));
                if (p != null) {
                    products.add(p);
                }
            }
            return products;

        } catch (SQLException e) {
            System.err.println("Error retrieving all physical products: " + e.getMessage());
            return products;
        }
    }
}