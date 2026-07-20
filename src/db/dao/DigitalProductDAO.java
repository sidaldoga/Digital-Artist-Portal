package db.dao;

import db.DatabaseConnection;
import model.DigitalProduct;
import model.ArtStyle;
import model.Artist;
import model.DigProductType;
import network.dto.DigitalProductDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class DigitalProductDAO {
    private final UserDAO userDAO;

    public DigitalProductDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
        createTable();
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS digital_products ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT NOT NULL,"
                + "description TEXT,"
                + "base_price REAL NOT NULL,"
                + "artist_id INTEGER NOT NULL,"
                + "style TEXT NOT NULL,"
                + "download_url TEXT NOT NULL,"
                + "file_size_mb REAL NOT NULL,"
                + "file_extension TEXT NOT NULL,"
                + "dig_product_type TEXT NOT NULL,"
                + "FOREIGN KEY (artist_id) REFERENCES users(id) ON DELETE CASCADE"
                + ");";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error creating digital_products table: " + e.getMessage());
        }
    }

    public synchronized int insertDigitalProduct(DigitalProduct product) {
        String sql = "INSERT INTO digital_products "
                + "(name, description, base_price, artist_id, style, download_url, file_size_mb, file_extension, dig_product_type) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getDescription());
            pstmt.setDouble(3, product.getBasePrice());
            pstmt.setInt(4, product.getArtist().getId());
            pstmt.setString(5, product.getStyle().name());
            pstmt.setString(6, product.getDownloadURL());
            pstmt.setDouble(7, product.getFileSizeMb());
            pstmt.setString(8, product.getFileExtension());
            pstmt.setString(9, product.getDigProductType().name());

            pstmt.executeUpdate();

            try (var keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    product.setId(keys.getInt(1));
                }
            }
            System.out.println("DigitalProduct successfully inserted " + product.getName());
        } catch (SQLException e) {
            System.err.println("Error inserting digital product: " + e.getMessage());
        }
        return product.getId();
    }

    public synchronized void updateDigitalProduct(DigitalProduct product) {
        String sql = "UPDATE digital_products SET name = ?, description = ?, base_price = ?, artist_id = ?, "
                + "style = ?, download_url = ?, file_size_mb = ?, file_extension = ?, dig_product_type = ? WHERE id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getDescription());
            pstmt.setDouble(3, product.getBasePrice());
            pstmt.setInt(4, product.getArtist().getId());
            pstmt.setString(5, product.getStyle().name());
            pstmt.setString(6, product.getDownloadURL());
            pstmt.setDouble(7, product.getFileSizeMb());
            pstmt.setString(8, product.getFileExtension());
            pstmt.setString(9, product.getDigProductType().name());
            pstmt.setInt(10, product.getId());

            pstmt.executeUpdate();
            System.out.println("DigitalProduct successfully updated: " + product.getName());
        } catch (SQLException e) {
            System.err.println("Error updating digital product: " + e.getMessage());
        }
    }

    public synchronized void deleteDigitalProduct(int id) {
        String sql = "DELETE FROM digital_products WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("DigitalProduct with ID " + id + " successfully deleted.");
        } catch (SQLException e) {
            System.err.println("Error deleting digital product: " + e.getMessage());
        }
    }

    public DigitalProduct getDigitalProductById(int id) {
        String sql = "SELECT * FROM digital_products WHERE id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            var rs = pstmt.executeQuery();

            if (rs.next()) {
                Artist artist = (Artist) userDAO.getUserById(rs.getInt("artist_id"));

                return new DigitalProduct(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("base_price"),
                        artist,
                        ArtStyle.valueOf(rs.getString("style")),
                        DigProductType.valueOf(rs.getString("dig_product_type")),
                        rs.getString("file_extension"),
                        rs.getDouble("file_size_mb"),
                        rs.getString("download_url")
                );
            }
            return null;
        } catch (SQLException e) {
            System.err.println("Error retrieving digital product: " + e.getMessage());
            return null;
        }
    }

    public List<DigitalProduct> getAllDigitalProducts() {
        List<DigitalProduct> products = new ArrayList<>();
        String sql = "SELECT id FROM digital_products;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             var rs = pstmt.executeQuery()) {

            while (rs.next()) {
                DigitalProduct p = getDigitalProductById(rs.getInt("id"));
                if (p != null) {
                    products.add(p);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all digital products: " + e.getMessage());
        }
        return products;
    }

    public DigitalProduct fromDTO(DigitalProductDTO dto) {
        if (dto == null) {
            return null;
        }
        Artist artist = new Artist("", "", dto.getArtistUsername(), "", "", false);
        artist.setId(dto.getArtistId());
        return new DigitalProduct(
                dto.getId(), dto.getName(), dto.getDescription(), dto.getBasePrice(), artist,
                ArtStyle.valueOf(dto.getStyle()),
                DigProductType.valueOf(dto.getDigProductType()),
                dto.getFileExtension(), dto.getFileSizeMb(), dto.getDownloadUrl());
    }

    public DigitalProductDTO toDTO(DigitalProduct p) {
        return new DigitalProductDTO(
                p.getId(), p.getArtist().getId(), p.getName(), p.getDescription(), p.getBasePrice(),
                p.getArtist().getUsername(), p.getStyle().name(), p.getDownloadURL(),
                p.getFileSizeMb(), p.getFileExtension(), p.getDigProductType().name()
        );
    }
}
