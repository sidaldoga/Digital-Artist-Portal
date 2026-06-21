package db.dao;

import db.DatabaseConnection;
import model.Artist;
import model.Subscription;
import model.Subscriber;
import network.dto.SubscriptionDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SubscriptionDAO {
    private final UserDAO userDAO;

    public SubscriptionDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
        createTable();
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS subscriptions ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "subscriber_id INTEGER NOT NULL,"
                + "artist_id INTEGER NOT NULL,"
                + "start_date TEXT NOT NULL,"
                + "is_active INTEGER NOT NULL,"
                + "UNIQUE(subscriber_id, artist_id),"
                + "FOREIGN KEY (subscriber_id) REFERENCES users(id) ON DELETE CASCADE,"
                + "FOREIGN KEY (artist_id) REFERENCES users(id) ON DELETE CASCADE"
                + ");";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error creating subscriptions table: " + e.getMessage());
        }
    }

    public synchronized void insertSubscription(Subscriber subscriber, Artist artist) {
        if (!isAlreadySubscribed(subscriber.getId(), artist.getId())){
            String sql = "INSERT INTO subscriptions (subscriber_id, artist_id, start_date, is_active) VALUES (?, ?, ?, ?);";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, subscriber.getId());
                pstmt.setInt(2, artist.getId());
                pstmt.setString(3, java.time.LocalDate.now().toString());
                pstmt.setInt(4, 1);

                pstmt.executeUpdate();
                System.out.println("Successfully subscribed " + subscriber.getUsername() + " to artist " + artist.getUsername());
            } catch (SQLException e) {
                System.err.println("Error inserting subscription: " + e.getMessage());
            }
        } else {
            System.out.println("This subscriber is already following this artist the operation was skipped.");
        }

    }

    public synchronized void updateSubscription(Subscription subscription) {
        String sql = "UPDATE subscriptions SET subscriber_id = ?, artist_id = ?, start_date = ?, is_active = ? WHERE id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, subscription.getSubscriber().getId());
            pstmt.setInt(2, subscription.getArtist().getId());
            pstmt.setString(3, subscription.getStartdate().toString());
            pstmt.setInt(4, subscription.isActive() ? 1 : 0);
            pstmt.setInt(5, subscription.getId());

            pstmt.executeUpdate();
            System.out.println("Subscription successfully updated: " + subscription.getId());

        } catch (SQLException e) {
            System.err.println("Error updating subscription: " + e.getMessage());
        }
    }

    public synchronized void deleteSubscription(int subscriberId, int artistId) {
        String sql = "DELETE FROM subscriptions WHERE subscriber_id = ? AND artist_id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, subscriberId);
            pstmt.setInt(2, artistId);
            pstmt.executeUpdate();
            System.out.println("Subscription successfully removed.");
        } catch (SQLException e) {
            System.err.println("Error deleting subscription: " + e.getMessage());
        }
    }

    public Subscription getSubscriptionById(int id) {
        String sql = "SELECT * FROM subscriptions WHERE id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            var rs = pstmt.executeQuery();

            if (rs.next()) {
                Subscriber subscriber = (Subscriber) userDAO.getUserById(rs.getInt("subscriber_id"));
                Artist artist = (Artist) userDAO.getUserById(rs.getInt("artist_id"));

                Subscription subscription = new Subscription(
                        rs.getInt("id"),
                        subscriber,
                        artist
                );

                subscription.setActive(rs.getInt("is_active") == 1);
                return subscription;
            } else {
                System.out.println("No subscription found with ID: " + id);
                return null;
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving subscription: " + e.getMessage());
            return null;
        }
    }

    public List<Subscription> getAllSubscriptions() {
        List<Subscription> subscriptions = new ArrayList<>();
        String sql = "SELECT * FROM subscriptions;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            var rs = pstmt.executeQuery();

            while (rs.next()) {
                Subscriber subscriber = (Subscriber) userDAO.getUserById(rs.getInt("subscriber_id"));
                Artist artist = (Artist) userDAO.getUserById(rs.getInt("artist_id"));

                Subscription subscription = new Subscription(
                        rs.getInt("id"),
                        subscriber,
                        artist
                );
                subscription.setActive(rs.getInt("is_active") == 1);
                subscriptions.add(subscription);
            }
            return subscriptions;

        } catch (SQLException e) {
            System.err.println("Error retrieving subscriptions: " + e.getMessage());
            return subscriptions;
        }
    }

    public int getSubscriptionCountForArtist(int artistId) {
        String sql = "SELECT COUNT(*) FROM subscriptions WHERE artist_id = ? AND is_active = 1;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, artistId);
            try (var rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error counting subscriptions for artist check: " + e.getMessage());
        }
        return 0;
    }

    public boolean exists(int subscriberId, int artistId) {
        return isAlreadySubscribed(subscriberId, artistId);
    }

    private boolean isAlreadySubscribed(int subscriberId, int artistId) {
        String sql = "SELECT 1 FROM subscriptions WHERE subscriber_id = ? AND artist_id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, subscriberId);
            pstmt.setInt(2, artistId);

            try (var rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error checking subscription existence: " + e.getMessage());
            return false;
        }
    }

    public Subscription fromDTO(SubscriptionDTO dto) {
        if (dto == null) {
            return null;
        }
        Subscriber subscriber = new Subscriber("", "", dto.getSubscriberUsername(),
                "", null, new ArrayList<>(), 0, "");
        subscriber.setId(dto.getSubscriberId());

        Artist artist = new Artist("", "", dto.getArtistUsername(), "", "", false);
        artist.setId(dto.getArtistId());

        Subscription subscription = new Subscription(dto.getId(), subscriber, artist);
        subscription.setActive(dto.isActive());
        return subscription;
    }

    public SubscriptionDTO toDTO(Subscription sub) {
        return new SubscriptionDTO(
                sub.getId(),
                sub.getSubscriber().getId(), sub.getSubscriber().getUsername(),
                sub.getArtist().getId(), sub.getArtist().getUsername(),
                sub.isActive()
        );
    }
}