package db.dao;

import db.DatabaseConnection;
import model.User;
import model.Subscriber;
import model.Artist;
import model.Address;
import network.dto.AddressDTO;
import network.dto.ArtistDTO;
import network.dto.SubscriberDTO;
import network.dto.UserDTO;
import util.PasswordHasher;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public UserDAO() {
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
        try (Connection conn = DatabaseConnection.getConnection();
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

    public synchronized void updateUser(User user) {
        String role = (user instanceof Subscriber) ? "SUBSCRIBER" : "ARTIST";

        boolean changePassword = user.getPassword() != null && !user.getPassword().isEmpty();

        String sql = changePassword
                ? "UPDATE users SET fname = ?, lname = ?, username = ?, password_hash = ?, email = ?, role = ?, address = ?, subscriber_point = ? WHERE id = ?;"
                : "UPDATE users SET fname = ?, lname = ?, username = ?, email = ?, role = ?, address = ?, subscriber_point = ? WHERE id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int idx = 1;
            pstmt.setString(idx++, user.getFname());
            pstmt.setString(idx++, user.getLname());
            pstmt.setString(idx++, user.getUsername());
            if (changePassword) {
                pstmt.setString(idx++, util.PasswordHasher.hashPassword(user.getPassword()));
            }
            pstmt.setString(idx++, user.getEmail());
            pstmt.setString(idx++, role);

            if (user instanceof Subscriber subscriber) {
                pstmt.setString(idx++, subscriber.getAddress() != null ? subscriber.getAddress().toString() : null);
                pstmt.setInt(idx++, subscriber.getSubscriberpoint());
            } else {
                pstmt.setNull(idx++, java.sql.Types.VARCHAR);
                pstmt.setInt(idx++, 0);
            }
            pstmt.setInt(idx, user.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
        }
    }

    public synchronized void deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
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

        try (Connection conn = DatabaseConnection.getConnection();
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

        try (Connection conn = DatabaseConnection.getConnection();
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
    public synchronized boolean registerUser(User user, String password, String role) {
        String hashedPassword = util.PasswordHasher.hashPassword(password);
        String sql = "INSERT INTO users (fname, lname, username, password_hash, email, role, address, subscriber_point) VALUES (?,?,?,?,?,?,?,?);";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

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

            int rows = pstmt.executeUpdate();
            if (rows == 0) {
                System.err.println("Error registering user: no row inserted for " + user.getUsername());
                return false;
            }

            try (var generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                }
            }

            System.out.println("User successfully registered: " + user.getUsername());
            return true;
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
            return false;
        }
    }

    public boolean isUsernameTaken(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
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

        if (!authenticate(username, password)) {
            System.out.println("Login failed for: " + username);
            return null;
        }

        String sql = "SELECT * FROM users WHERE username = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
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
                    }

                    else {
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

    public synchronized boolean authenticate(String username, String password) {
        String sql = "SELECT password_hash FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                return PasswordHasher.checkPassword(password, storedHash);
            }

        } catch (SQLException e) {
            System.err.println("Database error during authentication: " + e.getMessage());
        }
        return false;
    }

    public User fromDTO(UserDTO dto) {
        if (dto instanceof SubscriberDTO s) {
            Address addr = null;
            if (s.getAddress() != null) {
                addr = new Address(
                        s.getAddress().getStreet(), s.getAddress().getCity(),
                        s.getAddress().getZipCode(), s.getAddress().getCountry());
            }
            Subscriber sub = new Subscriber(
                    dto.getFname(), dto.getLname(), dto.getUsername(),
                    dto.getPassword(), addr, new ArrayList<>(),
                    s.getSubscriberPoint(), dto.getEmail());
            sub.setId(dto.getId());
            return sub;
        } else if (dto instanceof ArtistDTO a) {
            Artist artist = new Artist(
                    dto.getFname(), dto.getLname(), dto.getUsername(),
                    dto.getPassword(), dto.getEmail(), a.isDigital());
            artist.setId(dto.getId());
            return artist;
        }
        throw new IllegalArgumentException("Unsupported UserDTO subtype: "
                + (dto == null ? "null" : dto.getClass().getSimpleName()));
    }

    public UserDTO toDTO(User user) {
        if (user instanceof Subscriber s) {
            AddressDTO addrDto = null;
            if (s.getAddress() != null) {
                addrDto = new AddressDTO(
                        s.getAddress().getStreet(), s.getAddress().getCity(),
                        s.getAddress().getZipCode(), s.getAddress().getCountry()
                );
            }
            return new SubscriberDTO(s.getId(), s.getFname(), s.getLname(), s.getUsername(),
                    s.getEmail(), "SUBSCRIBER", s.getSubscriberpoint(), addrDto);
        } else if (user instanceof Artist a) {
            return new ArtistDTO(a.getId(), a.getFname(), a.getLname(), a.getUsername(),
                    a.getEmail(), "ARTIST", a.isDigital());
        }
        return new UserDTO(user.getId(), user.getFname(), user.getLname(), user.getUsername(), user.getEmail(), "USER");
    }
}