package db.dao;

import db.DatabaseConnection;
import model.PhysicalProduct;
import model.ArtStyle;
import model.ProductType;
import model.MediumType;
import model.Dimensions;
import model.Artist;
import network.dto.DimensionsDTO;
import network.dto.PhysicalProductDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PhysicalProductDAO {
    private final UserDAO userDAO;
    private final DimensionsDAO dimensionsDAO;

    public PhysicalProductDAO(UserDAO userDAO, DimensionsDAO dimensionsDAO) {
        this.userDAO = userDAO;
        this.dimensionsDAO = dimensionsDAO;
        createTable();
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS physical_products ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT NOT NULL,"
                + "description TEXT,"
                + "base_price REAL NOT NULL,"
                + "artist_id INTEGER NOT NULL,"
                + "style TEXT NOT NULL,"
                + "dimensions_id INTEGER NOT NULL,"
                + "weight REAL NOT NULL,"
                + "stock INTEGER NOT NULL,"
                + "medium TEXT NOT NULL,"
                + "product_type TEXT NOT NULL,"
                + "FOREIGN KEY (artist_id) REFERENCES users(id) ON DELETE CASCADE,"
                + "FOREIGN KEY (dimensions_id) REFERENCES dimensions(id)"
                + ");";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error creating physical_products table: " + e.getMessage());
        }
    }

    public synchronized int insertPhysicalProduct(PhysicalProduct product) {
        int dimId = dimensionsDAO.insertDimensions(product.getDimensionsCm());

        String sql = "INSERT INTO physical_products "
                + "(name, description, base_price, artist_id, style, dimensions_id, weight, stock, medium, product_type) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getDescription());
            pstmt.setDouble(3, product.getBasePrice());
            pstmt.setInt(4, product.getArtist().getId());
            pstmt.setString(5, product.getStyle().name());
            pstmt.setInt(6, dimId);
            pstmt.setDouble(7, product.getWeight());
            pstmt.setInt(8, product.getStock());
            pstmt.setString(9, product.getMedium().name());
            pstmt.setString(10, product.getProductType().name());

            pstmt.executeUpdate();

            try (var keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    product.setId(keys.getInt(1));
                }
            }
            System.out.println("PhysicalProduct successfully inserted " + product.getName());
        } catch (SQLException e) {
            System.err.println("Error inserting physical product: " + e.getMessage());
        }
        return product.getId();
    }

    public synchronized void updatePhysicalProduct(PhysicalProduct product) {
        String sql = "UPDATE physical_products SET name = ?, description = ?, base_price = ?, artist_id = ?, "
                + "style = ?, dimensions_id = ?, weight = ?, stock = ?, medium = ?, product_type = ? WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getDescription());
            pstmt.setDouble(3, product.getBasePrice());
            pstmt.setInt(4, product.getArtist().getId());
            pstmt.setString(5, product.getStyle().name());
            pstmt.setInt(6, product.getDimensionsCm().getId());
            pstmt.setDouble(7, product.getWeight());
            pstmt.setInt(8, product.getStock());
            pstmt.setString(9, product.getMedium().name());
            pstmt.setString(10, product.getProductType().name());
            pstmt.setInt(11, product.getId());

            pstmt.executeUpdate();
            System.out.println("PhysicalProduct successfully updated: " + product.getName());
        } catch (SQLException e) {
            System.err.println("Error updating physical product: " + e.getMessage());
        }
    }

    public synchronized void deletePhysicalProduct(int id) {
        String sql = "DELETE FROM physical_products WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("PhysicalProduct with ID " + id + " successfully deleted.");
        } catch (SQLException e) {
            System.err.println("Error deleting physical product: " + e.getMessage());
        }
    }

    public synchronized void updateStock(int productId, int newStock) {
        String sql = "UPDATE physical_products SET stock = ? WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, newStock);
            pstmt.setInt(2, productId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating stock: " + e.getMessage());
        }
    }

    public PhysicalProduct getPhysicalProductById(int id) {
        String sql = "SELECT * FROM physical_products WHERE id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            var rs = pstmt.executeQuery();

            if (rs.next()) {
                Artist artist = (Artist) userDAO.getUserById(rs.getInt("artist_id"));
                Dimensions dimObj = dimensionsDAO.getDimensionsById(rs.getInt("dimensions_id"));

                return new PhysicalProduct(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("base_price"),
                        artist,
                        ArtStyle.valueOf(rs.getString("style")),
                        ProductType.valueOf(rs.getString("product_type")),
                        MediumType.valueOf(rs.getString("medium")),
                        rs.getInt("stock"),
                        rs.getDouble("weight"),
                        dimObj
                );
            }
            return null;
        } catch (SQLException e) {
            System.err.println("Error retrieving physical product: " + e.getMessage());
            return null;
        }
    }

    public List<PhysicalProduct> getAllPhysicalProducts() {
        List<PhysicalProduct> products = new ArrayList<>();
        String sql = "SELECT id FROM physical_products;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             var rs = pstmt.executeQuery()) {

            while (rs.next()) {
                PhysicalProduct p = getPhysicalProductById(rs.getInt("id"));
                if (p != null) {
                    products.add(p);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all physical products: " + e.getMessage());
        }
        return products;
    }

    public PhysicalProduct fromDTO(PhysicalProductDTO dto) {
        if (dto == null) {
            return null;
        }
        Artist artist = new Artist("", "", dto.getArtistUsername(), "", "", false);
        artist.setId(dto.getArtistId());

        Dimensions dims = null;
        if (dto.getDimensions() != null) {
            DimensionsDTO d = dto.getDimensions();
            dims = new Dimensions(0, d.getWidth(), d.getHeight(), d.getDepth());
        }
        return new PhysicalProduct(
                dto.getId(), dto.getName(), dto.getDescription(), dto.getBasePrice(), artist,
                ArtStyle.valueOf(dto.getStyle()),
                ProductType.valueOf(dto.getProductType()),
                MediumType.valueOf(dto.getMedium()),
                dto.getStock(), dto.getWeight(), dims);
    }

    public PhysicalProductDTO toDTO(PhysicalProduct p) {
        DimensionsDTO dim = (p.getDimensionsCm() != null) ?
                new DimensionsDTO(p.getDimensionsCm().getWidth(), p.getDimensionsCm().getHeight(), p.getDimensionsCm().getDepth()) : null;

        return new PhysicalProductDTO(
                p.getId(), p.getArtist().getId(), p.getName(), p.getDescription(), p.getBasePrice(),
                p.getArtist().getUsername(), p.getStyle().name(), p.getStock(), p.getWeight(),
                p.getMedium().name(), p.getProductType().name(), dim
        );
    }
}
