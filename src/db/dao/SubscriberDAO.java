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
                + "address_text TEXT,"
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
        String sql = "INSERT INTO subscriber_details (user_id, address_text, subscriber_point) VALUES (?, ?, ?);";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, subscriber.getId());
            pstmt.setString(2, subscriber.getAddress() != null ? subscriber.getAddress().toString() : null);
            pstmt.setInt(3, subscriber.getSubscriberpoint());

            pstmt.executeUpdate();
            System.out.println("Subscriber details successfully inserted for: " + subscriber.getUsername());
        } catch (SQLException e) {
            System.err.println("Error inserting subscriber details: " + e.getMessage());
        }
    }


    public synchronized void updateSubscriberDetails(Subscriber subscriber) {
        String sql = "UPDATE subscriber_details SET address_text = ?, subscriber_point = ? WHERE user_id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, subscriber.getAddress() != null ? subscriber.getAddress().toString() : null);
            pstmt.setInt(2, subscriber.getSubscriberpoint());
            pstmt.setInt(3, subscriber.getId());

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
                String addrStr = rs.getString("address_text");
                Address addressObj = null;

                if (addrStr != null && !addrStr.isEmpty() && addrStr.contains(",")) {
                    try {
                        String[] parts = addrStr.split(",", 2);
                        String street = parts[0].trim();

                        String[] rest = parts[1].trim().split(" ", 2);
                        String zipCode = rest[0].trim();

                        String[] cityAndCountry = rest[1].trim().split("/", 2);
                        String city = cityAndCountry[0].trim();
                        String country = cityAndCountry[1].trim();

                        addressObj = new Address(street, city, zipCode, country);
                    } catch (Exception e) {
                        addressObj = new Address(addrStr, "Unknown", "Unknown", "Unknown");
                    }
                } else if (addrStr != null) {
                    addressObj = new Address(addrStr, "Unknown", "Unknown", "Unknown");
                }

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
                s.getEmail(), "SUBSCRIBER", s.getSubscriberpoint(), addrDto);
    }

}