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
    private final ArtProductDAO artProductDAO;
    private final DimensionsDAO dimensionsDAO;

    public PhysicalProductDAO(ArtProductDAO artProductDAO, DimensionsDAO dimensionsDAO) {
        this.artProductDAO = artProductDAO;
        this.dimensionsDAO = dimensionsDAO;
        createTable();
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS physical_products ("
                + "product_id INTEGER PRIMARY KEY,"
                + "style TEXT NOT NULL,"
                + "dimensions_id INTEGER NOT NULL,"
                + "weight REAL NOT NULL,"
                + "stock INTEGER NOT NULL,"
                + "medium TEXT NOT NULL,"
                + "product_type TEXT NOT NULL,"
                + "FOREIGN KEY (product_id) REFERENCES art_products(id) ON DELETE CASCADE, "
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
        artProductDAO.insertArtProduct(product);

        String sql = "INSERT INTO physical_products (product_id, style, dimensions_id, weight, stock, medium, product_type) VALUES (?, ?, ?, ?, ?, ?, ?);";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, product.getId());
            pstmt.setString(2, product.getStyle().name());
            pstmt.setInt(3, dimId);
            pstmt.setDouble(4, product.getWeight());
            pstmt.setInt(5, product.getStock());
            pstmt.setString(6, product.getMedium().name());
            pstmt.setString(7, product.getProductType().name());

            pstmt.executeUpdate();
            System.out.println("PhysicalProduct successfully inserted " + product.getName());
        } catch (SQLException e) {
            System.err.println("Error inserting physical product details: " + e.getMessage());
        }
        return product.getId();
    }

    public synchronized void updatePhysicalProduct(PhysicalProduct product) {
        artProductDAO.updateArtProduct(product);

        String sql = "UPDATE physical_products SET style = ?, dimensions_id = ?, weight = ?, stock = ?, medium = ?, product_type = ? WHERE product_id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, product.getStyle().name());
            pstmt.setInt(2, product.getDimensionsCm().getId());
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

    public synchronized void deletePhysicalProduct(int id) {
        artProductDAO.deleteArtProduct(id);
    }

    public synchronized void updateStock(int productId, int newStock) {
        String sql = "UPDATE physical_products SET stock = ? WHERE product_id = ?;";
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
        model.ArtProduct baseProduct = artProductDAO.getArtProductById(id);
        if (baseProduct == null) return null;

        String sql = "SELECT * FROM physical_products WHERE product_id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            var rs = pstmt.executeQuery();

            if (rs.next()) {
                int dimId = rs.getInt("dimensions_id");
                Dimensions dimObj = dimensionsDAO.getDimensionsById(dimId);

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

        try (Connection conn = DatabaseConnection.getConnection();
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