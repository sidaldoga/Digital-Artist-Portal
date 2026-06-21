package db.dao;

import db.DatabaseConnection;
import model.ArtProduct;
import model.Artist;
import model.User;
import network.dto.ArtProductDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ArtProductDAO {
    private final UserDAO userDAO;
    public ArtProductDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
        createTable();
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS art_products ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT NOT NULL,"
                + "description TEXT,"
                + "base_price REAL NOT NULL,"
                + "artist_id TEXT NOT NULL"
                + ");";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error creating art_products table: " + e.getMessage());
        }
    }

    public void insertArtProduct(ArtProduct artProduct) {
        String sql = "INSERT INTO art_products (name, description, base_price, artist_id) VALUES (?, ?, ?, ?);";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, artProduct.getName());
            pstmt.setString(2, artProduct.getDescription());
            pstmt.setDouble(3, artProduct.getBasePrice());
            pstmt.setInt(4, artProduct.getArtist().getId());
            pstmt.executeUpdate();

            try (var generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    artProduct.setId(generatedKeys.getInt(1));
                }
            }

            System.out.println("ArtProduct successfully inserted " + artProduct.getName());
        } catch (SQLException e) {
            System.err.println("Error inserting art_product: " + e.getMessage());
        }
    }

    public void updateArtProduct(ArtProduct artProduct) {
        String sql = "UPDATE art_products SET name = ?, description = ?, base_price = ?, artist_id = ? WHERE id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, artProduct.getName());
            pstmt.setString(2, artProduct.getDescription());
            pstmt.setDouble(3, artProduct.getBasePrice());
            pstmt.setInt(4, artProduct.getArtist().getId());
            pstmt.setInt(5, artProduct.getId());

            pstmt.executeUpdate();
            System.out.println("ArtProduct successfully updated: " + artProduct.getName());

        } catch (SQLException e) {
            System.err.println("Error updating art_product: " + e.getMessage());
        }
    }

    public void deleteArtProduct(int id) {
        String sql = "DELETE FROM art_products WHERE id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("ArtProduct with ID " + id + " successfully deleted.");

        } catch (SQLException e) {
            System.err.println("Error deleting art_product: " + e.getMessage());
        }
    }

    public ArtProduct getArtProductById(int id) {
        String sql = "SELECT * FROM art_products WHERE id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            var rs = pstmt.executeQuery();

            if (rs.next()) {
                int artistId = rs.getInt("artist_id");

                User baseUser = userDAO.getUserById(artistId);
                Artist artistObj = null;

                if (baseUser instanceof Artist) {
                    artistObj = (Artist) baseUser;
                } else {
                    artistObj = new Artist("Unknown", "Artist", "unknown", "", "", false);
                    artistObj.setId(artistId);
                }

                ArtProduct product = new ArtProduct(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("base_price"),
                        artistObj
                ) {};

                return product;
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving art product: " + e.getMessage());
        }
        return null;
    }
    public List<ArtProduct> getAllArtProducts() {
        List<ArtProduct> artProducts = new ArrayList<>();
        String sql = "SELECT * FROM art_products;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            var rs = pstmt.executeQuery();

            while (rs.next()) {
                int artistId = rs.getInt("artist_id");

                User baseUser = userDAO.getUserById(artistId);
                Artist artistObj = null;

                if (baseUser instanceof Artist) {
                    artistObj = (Artist) baseUser;
                } else {
                    artistObj = new Artist("Unknown", "Artist", "unknown", "", "", false);
                    artistObj.setId(artistId);
                }

                ArtProduct artProduct = new ArtProduct(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("base_price"),
                        artistObj
                ) {};

                artProducts.add(artProduct);
            }
            return artProducts;

        } catch (SQLException e) {
            System.err.println("Error retrieving art_products: " + e.getMessage());
            return artProducts;
        }
    }

    public ArtProductDTO toDTO(ArtProduct product) {
        return new ArtProductDTO(
                product.getId(),
                product.getArtist().getId(),
                product.getName(),
                product.getDescription(),
                product.getBasePrice(),
                product.getArtist().getUsername()
        );
    }
}