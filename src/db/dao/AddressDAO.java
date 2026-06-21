package db.dao;

import db.DatabaseConnection;
import model.Address;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AddressDAO {

    public AddressDAO() {
        createTable();
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS addresses ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "street TEXT NOT NULL,"
                + "city TEXT NOT NULL,"
                + "zip_code TEXT NOT NULL,"
                + "country TEXT NOT NULL"
                + ");";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error creating addresses table: " + e.getMessage());
        }
    }

    public void insertAddress(Address address) {
        String sql = "INSERT INTO addresses (street, city, zip_code, country) VALUES (?, ?, ?, ?);";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, address.getStreet());
            pstmt.setString(2, address.getCity());
            pstmt.setString(3, address.getZipCode());
            pstmt.setString(4, address.getCountry());

            pstmt.executeUpdate();

            try (var generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    address.setId(generatedKeys.getInt(1));
                }
            }

            System.out.println("Address successfully inserted: " + address.toString());
        } catch (SQLException e) {
            System.err.println("Error inserting address: " + e.getMessage());
        }
    }

    public void updateAddress(int addressId, Address address) {
        String sql = "UPDATE addresses SET street = ?, city = ?, zip_code = ?, country = ? WHERE id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, address.getStreet());
            pstmt.setString(2, address.getCity());
            pstmt.setString(3, address.getZipCode());
            pstmt.setString(4, address.getCountry());
            pstmt.setInt(5, addressId);

            pstmt.executeUpdate();
            System.out.println("Address successfully updated: " + address.toString());

        } catch (SQLException e) {
            System.err.println("Error updating address: " + e.getMessage());
        }
    }

    public void deleteAddress(int id) {
        String sql = "DELETE FROM addresses WHERE id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Address with ID " + id + " successfully deleted.");

        } catch (SQLException e) {
            System.err.println("Error deleting address: " + e.getMessage());
        }
    }

    public Address getAddressById(int id) {
        String sql = "SELECT * FROM addresses WHERE id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            var rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Address(
                        rs.getString("street"),
                        rs.getString("city"),
                        rs.getString("zip_code"),
                        rs.getString("country")
                );
            } else {
                System.out.println("No address found with ID: " + id);
                return null;
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving address: " + e.getMessage());
            return null;
        }
    }

    public List<Address> getAllAddresses() {
        List<Address> addresses = new ArrayList<>();
        String sql = "SELECT * FROM addresses;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            var rs = pstmt.executeQuery();

            while (rs.next()) {
                Address address = new Address(
                        rs.getString("street"),
                        rs.getString("city"),
                        rs.getString("zip_code"),
                        rs.getString("country")
                );
                addresses.add(address);
            }
            return addresses;

        } catch (SQLException e) {
            System.err.println("Error retrieving addresses: " + e.getMessage());
            return addresses;
        }
    }
}