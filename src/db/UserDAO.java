package db;

import model.User;
import model.Subscriber;
import model.Artist;
import model.Address;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private final DatabaseConnection dbConnection;

    public UserDAO(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
        createTable();
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "fname TEXT NOT NULL,"
                + "lname TEXT NOT NULL,"
                + "username TEXT UNIQUE NOT NULL,"
                + "password_hash TEXT NOT NULL,"
                + "email TEXT NOT NULL,"
                + "role TEXT NOT NULL,"
                + "address TEXT,"
                + "subscriber_point INTEGER DEFAULT 0"
                + ");";
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error creating users table: " + e.getMessage());
        }
    }

    private Address parseAddress(String addrStr) {
        if (addrStr == null || addrStr.isEmpty()) {
            return null;
        }

        if (addrStr.contains(",")) {
            try {

                String[] parts = addrStr.split(",", 2);
                String street = parts[0].trim();

                String[] rest = parts[1].trim().split(" ", 2);
                String zipCode = rest[0].trim();

                String[] cityAndCountry = rest[1].trim().split("/", 2);
                String city = cityAndCountry[0].trim();
                String country = cityAndCountry[1].trim();

                return new Address(street, city, zipCode, country);
            } catch (Exception e) {
                return new Address(addrStr, "Unknown", "Unknown", "Unknown");
            }
        }

        return new Address(addrStr, "Unknown", "Unknown", "Unknown");
    }

    public void insertUser(User user, String role) {
        String sql = "INSERT INTO users (fname, lname, username, password_hash, email, role, address, subscriber_point) VALUES (?,?,?,?,?,?,?,?);";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getFname());
            pstmt.setString(2, user.getLname());
            pstmt.setString(3, user.getUsername());
            pstmt.setString(4, user.getPassword());
            pstmt.setString(5, user.getEmail());


            if (user instanceof Subscriber subscriber) {
                pstmt.setString(7, subscriber.getAddress() != null ? subscriber.getAddress().toString() : null);
                pstmt.setInt(8, subscriber.getSubscriberpoint());
            } else {
                pstmt.setNull(7, java.sql.Types.VARCHAR);
                pstmt.setInt(8, 0);
            }

            pstmt.executeUpdate();

            try (var generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                }
            }

            System.out.println("User successfully inserted " + user.getUsername());
        } catch (SQLException e) {
            System.err.println("Error inserting user: " + e.getMessage());
        }
    }

    public void updateUser(User user, String role) {
        String sql = "UPDATE users SET fname = ?, lname = ?, username = ?, password_hash = ?, email = ?, role = ?, address = ?, subscriber_point = ? WHERE id = ?;";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getFname());
            pstmt.setString(2, user.getLname());
            pstmt.setString(3, user.getUsername());
            pstmt.setString(4, user.getPassword());
            pstmt.setString(5, user.getEmail());
            pstmt.setString(6, role);

            if (user instanceof Subscriber subscriber) {
                pstmt.setString(7, subscriber.getAddress() != null ? subscriber.getAddress().toString() : null);
                pstmt.setInt(8, subscriber.getSubscriberpoint());
            } else {
                pstmt.setNull(7, java.sql.Types.VARCHAR);
                pstmt.setInt(8, 0);
            }
            pstmt.setInt(9, user.getId());

            pstmt.executeUpdate();
            System.out.println("User successfully updated: " + user.getUsername());

        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
        }
    }

    public void deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?;";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("User with ID " + id + " successfully deleted.");

        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
        }
    }

    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?;";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            var rs = pstmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                String addrStr = rs.getString("address");

                Address addressObj = parseAddress(addrStr);

                if ("SUBSCRIBER".equalsIgnoreCase(role)) {
                    Subscriber subscriber = new Subscriber(
                            rs.getString("fname"),
                            rs.getString("lname"),
                            rs.getString("username"),
                            rs.getString("password_hash"),
                            addressObj,
                            new ArrayList<Artist>(),
                            rs.getInt("subscriber_point"),
                            rs.getString("email")
                    );
                    subscriber.setId(rs.getInt("id"));
                    return subscriber;
                } else {
                    Artist artist = new Artist(
                            rs.getString("fname"),
                            rs.getString("lname"),
                            rs.getString("username"),
                            rs.getString("password_hash"),
                            rs.getString("email"),
                            false
                    );
                    artist.setId(rs.getInt("id"));
                    return artist;
                }
            } else {
                System.out.println("No user found with ID: " + id);
                return null;
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving user: " + e.getMessage());
            return null;
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users;";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            var rs = pstmt.executeQuery();

            while (rs.next()) {
                String role = rs.getString("role");
                String addrStr = rs.getString("address");

                Address addressObj = parseAddress(addrStr);

                if ("SUBSCRIBER".equalsIgnoreCase(role)) {
                    Subscriber subscriber = new Subscriber(
                            rs.getString("fname"),
                            rs.getString("lname"),
                            rs.getString("username"),
                            rs.getString("password_hash"),
                            addressObj,
                            new ArrayList<Artist>(),
                            rs.getInt("subscriber_point"),
                            rs.getString("email")
                    );
                    subscriber.setId(rs.getInt("id"));
                    users.add(subscriber);
                } else {
                    Artist artist = new Artist(
                            rs.getString("fname"),
                            rs.getString("lname"),
                            rs.getString("username"),
                            rs.getString("password_hash"),
                            rs.getString("email"),
                            false
                    );
                    artist.setId(rs.getInt("id"));
                    users.add(artist);
                }
            }
            return users;

        } catch (SQLException e) {
            System.err.println("Error retrieving users: " + e.getMessage());
            return users;
        }
    }
    public void registerUser(User user, String password, String role) {
        String hashedPassword = util.PasswordHasher.hashPassword(password);
        String sql = "INSERT INTO users (fname, lname, username, password_hash, email, role, address, subscriber_point) VALUES (?,?,?,?,?,?,?,?);";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getFname());
            pstmt.setString(2, user.getLname());
            pstmt.setString(3, user.getUsername());
            pstmt.setString(4, hashedPassword);
            pstmt.setString(5, user.getEmail());
            pstmt.setString(6, role);

            if (user instanceof Subscriber subscriber) {
                pstmt.setString(7, subscriber.getAddress() != null ? subscriber.getAddress().toString() : null);
                pstmt.setInt(8, subscriber.getSubscriberpoint());
            } else {
                pstmt.setNull(7, java.sql.Types.VARCHAR);
                pstmt.setInt(8, 0);
            }

            pstmt.executeUpdate();
            System.out.println("User successfully registered: " + user.getUsername());
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
        }
    }

    public boolean isUsernameTaken(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?;";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            var rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error checking username: " + e.getMessage());
        }
        return false;
    }

    public User loginUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ?;";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            var rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");

                if (util.PasswordHasher.checkPassword(password, storedHash)) {
                    System.out.println("Login successful: " + username);
                    String role = rs.getString("role");
                    String addrStr = rs.getString("address");

                    Address addressObj = parseAddress(addrStr);

                    if ("SUBSCRIBER".equalsIgnoreCase(role)) {
                        Subscriber subscriber = new Subscriber(
                                rs.getString("fname"),
                                rs.getString("lname"),
                                rs.getString("username"),
                                rs.getString("password_hash"),
                                addressObj,
                                new ArrayList<Artist>(),
                                rs.getInt("subscriber_point"),
                                rs.getString("email")
                        );
                        subscriber.setId(rs.getInt("id"));
                        return subscriber;
                    } else {
                        Artist artist = new Artist(
                                rs.getString("fname"),
                                rs.getString("lname"),
                                rs.getString("username"),
                                rs.getString("password_hash"),
                                rs.getString("email"),
                                false
                        );
                        artist.setId(rs.getInt("id"));
                        return artist;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during login: " + e.getMessage());
        }

        System.out.println("Login failed for username or password.");
        return null;
    }
}