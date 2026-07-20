package db.dao;

import db.DatabaseConnection;
import model.Subscriber;
import model.Address;
import model.Artist;
import network.dto.AddressDTO;
import network.dto.SubscriberDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SubscriberDAO {
    private final UserDAO userDAO;

    public SubscriberDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
        createTable();
    }


    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS subscriber_details ("
                + "user_id INTEGER PRIMARY KEY,"
                + "street TEXT,"
                + "city TEXT,"
                + "zip_code TEXT,"
                + "country TEXT,"
                + "subscriber_point INTEGER DEFAULT 0,"
                + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"
                + ");";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error creating subscriber_details table: " + e.getMessage());
        }
    }

    public synchronized void insertSubscriberDetails(Subscriber subscriber) {
        String sql = "INSERT INTO subscriber_details (user_id, street, city, zip_code, country, subscriber_point) VALUES (?, ?, ?, ?, ?, ?);";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, subscriber.getId());
            if (subscriber.getAddress() != null) {
                pstmt.setString(2, subscriber.getAddress().getStreet());
                pstmt.setString(3, subscriber.getAddress().getCity());
                pstmt.setString(4, subscriber.getAddress().getZipCode());
                pstmt.setString(5, subscriber.getAddress().getCountry());
            } else {
                pstmt.setString(2, null);
                pstmt.setString(3, null);
                pstmt.setString(4, null);
                pstmt.setString(5, null);
            }
            pstmt.setInt(6, subscriber.getSubscriberpoint());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error inserting subscriber: " + e.getMessage());
        }
    }


    public synchronized void updateSubscriberDetails(Subscriber subscriber) {
        String sql = "UPDATE subscriber_details SET street = ?, city = ?, zip_code = ?, country = ?, subscriber_point = ? WHERE user_id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            Address addr = subscriber.getAddress();
            pstmt.setString(1, addr != null ? addr.getStreet() : null);
            pstmt.setString(2, addr != null ? addr.getCity() : null);
            pstmt.setString(3, addr != null ? addr.getZipCode() : null);
            pstmt.setString(4, addr != null ? addr.getCountry() : null);
            pstmt.setInt(5, subscriber.getSubscriberpoint());
            pstmt.setInt(6, subscriber.getId());

            pstmt.executeUpdate();
            System.out.println("Subscriber details successfully updated for: " + subscriber.getUsername());
        } catch (SQLException e) {
            System.err.println("Error updating subscriber details: " + e.getMessage());
        }
    }

    public Subscriber getSubscriberById(int userId) {
        model.User baseUser = userDAO.getUserById(userId);
        if (baseUser == null) return null;

        String sql = "SELECT * FROM subscriber_details WHERE user_id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            var rs = pstmt.executeQuery();

            if (rs.next()) {
                String street = rs.getString("street");
                String city = rs.getString("city");
                String zipCode = rs.getString("zip_code");
                String country = rs.getString("country");

                Address addressObj = (street != null) ? new Address(street, city, zipCode, country) : null;

                Subscriber subscriber = new Subscriber(
                        baseUser.getFname(),
                        baseUser.getLname(),
                        baseUser.getUsername(),
                        baseUser.getPassword(),
                        addressObj,
                        new ArrayList<Artist>(),
                        rs.getInt("subscriber_point"),
                        baseUser.getEmail()
                );
                subscriber.setId(userId);
                return subscriber;
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving subscriber details: " + e.getMessage());
        }
        return null;
    }

    public List<Subscriber> getAllSubscribers() {
        List<Subscriber> subscribers = new ArrayList<>();
        String sql = "SELECT user_id FROM subscriber_details;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             var rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Subscriber s = getSubscriberById(rs.getInt("user_id"));
                if (s != null) {
                    subscribers.add(s);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all subscribers: " + e.getMessage());
        }
        return subscribers;
    }


    public synchronized void deleteSubscriberDetails(int userId) {
        String sql = "DELETE FROM subscriber_details WHERE user_id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
            System.out.println("Subscriber details deleted for user ID: " + userId);
        } catch (SQLException e) {
            System.err.println("Error deleting subscriber details: " + e.getMessage());
        }
    }

    public Subscriber fromDTO(SubscriberDTO dto) {
        if (dto == null) {
            return null;
        }
        Address addr = null;
        if (dto.getAddress() != null) {
            AddressDTO a = dto.getAddress();
            addr = new Address(a.getStreet(), a.getCity(), a.getZipCode(), a.getCountry());
        }
        Subscriber subscriber = new Subscriber(
                dto.getFname(), dto.getLname(), dto.getUsername(),
                dto.getPassword(), addr, new ArrayList<>(),
                dto.getSubscriberPoint(), dto.getEmail());
        subscriber.setId(dto.getId());
        return subscriber;
    }

    public SubscriberDTO toDTO(Subscriber s) {
        AddressDTO addrDto = (s.getAddress() != null) ?
                new AddressDTO(s.getAddress().getStreet(), s.getAddress().getCity(), s.getAddress().getZipCode(), s.getAddress().getCountry()) : null;

        return new SubscriberDTO(s.getId(), s.getFname(), s.getLname(), s.getUsername(),
                s.getEmail(), "SUBSCRIBER", s.getSubscriberpoint(), addrDto,s.getPassword());
    }

}