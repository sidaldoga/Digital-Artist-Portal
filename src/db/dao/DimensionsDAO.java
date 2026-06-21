package db.dao;

import db.DatabaseConnection;
import model.Dimensions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DimensionsDAO {

    public DimensionsDAO() {
        createTable();
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS dimensions ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "width REAL NOT NULL,"
                + "height REAL NOT NULL,"
                + "depth REAL NOT NULL"
                + ");";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error creating dimensions table: " + e.getMessage());
        }
    }

    public int insertDimensions(Dimensions dimensions) {
        String sql = "INSERT INTO dimensions (width, height, depth) VALUES (?, ?, ?);";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setDouble(1, dimensions.getWidth());
            pstmt.setDouble(2, dimensions.getHeight());
            pstmt.setDouble(3, dimensions.getDepth());

            pstmt.executeUpdate();

            try (var generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    System.out.println("Dimensions successfully inserted with ID: " + generatedId);
                    return generatedId;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting dimensions: " + e.getMessage());
        }
        return -1;
    }

    public void updateDimensions(int id, Dimensions dimensions) {
        String sql = "UPDATE dimensions SET width = ?, height = ?, depth = ? WHERE id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, dimensions.getWidth());
            pstmt.setDouble(2, dimensions.getHeight());
            pstmt.setDouble(3, dimensions.getDepth());
            pstmt.setInt(4, id);

            pstmt.executeUpdate();
            System.out.println("Dimensions successfully updated for ID: " + id);

        } catch (SQLException e) {
            System.err.println("Error updating dimensions: " + e.getMessage());
        }
    }

    public void deleteDimensions(int id) {
        String sql = "DELETE FROM dimensions WHERE id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Dimensions with ID " + id + " successfully deleted.");

        } catch (SQLException e) {
            System.err.println("Error deleting dimensions: " + e.getMessage());
        }
    }

    public Dimensions getDimensionsById(int id) {
        String sql = "SELECT * FROM dimensions WHERE id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            var rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Dimensions(
                        rs.getDouble("width"),
                        rs.getDouble("height"),
                        rs.getDouble("depth")
                );
            } else {
                System.out.println("No dimensions found with ID: " + id);
                return null;
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving dimensions: " + e.getMessage());
            return null;
        }
    }

    public List<Dimensions> getAllDimensions() {
        List<Dimensions> dimensionsList = new ArrayList<>();
        String sql = "SELECT * FROM dimensions;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            var rs = pstmt.executeQuery();

            while (rs.next()) {
                Dimensions dimensions = new Dimensions(
                        rs.getDouble("width"),
                        rs.getDouble("height"),
                        rs.getDouble("depth")
                );
                dimensionsList.add(dimensions);
            }
            return dimensionsList;

        } catch (SQLException e) {
            System.err.println("Error retrieving dimensions list: " + e.getMessage());
            return dimensionsList;
        }
    }
}