package db.dao;

import db.DatabaseConnection;
import model.Artist;
import network.dto.ArtistDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ArtistDAO {
    private final UserDAO userDAO;


    public ArtistDAO( UserDAO userDAO) {
        this.userDAO = userDAO;
        createTable();
    }


    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS artist_details ("
                + "user_id INTEGER PRIMARY KEY,"
                + "is_digital INTEGER NOT NULL,"
                + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"
                + ");";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error creating artist_details table: " + e.getMessage());
        }
    }


    public synchronized void insertArtistDetails(Artist artist) {
        String sql = "INSERT INTO artist_details (user_id, is_digital) VALUES (?, ?);";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, artist.getId());
            pstmt.setInt(2, artist.isDigital() ? 1 : 0);
            pstmt.executeUpdate();
            System.out.println("Artist details successfully inserted for: " + artist.getUsername());
        } catch (SQLException e) {
            System.err.println("Error inserting artist details: " + e.getMessage());
        }
    }


    public synchronized void updateArtistDetails(Artist artist) {
        String sql = "UPDATE artist_details SET is_digital = ? WHERE user_id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, artist.isDigital() ? 1 : 0);
            pstmt.setInt(2, artist.getId());

            pstmt.executeUpdate();
            System.out.println("Artist details successfully updated for: " + artist.getUsername());
        } catch (SQLException e) {
            System.err.println("Error updating artist details: " + e.getMessage());
        }
    }

    public Artist getArtistById(int userId) {
        model.User baseUser = userDAO.getUserById(userId);
        if (baseUser == null) return null;

        String sql = "SELECT * FROM artist_details WHERE user_id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            var rs = pstmt.executeQuery();

            if (rs.next()) {

                Artist artist = new Artist(
                        baseUser.getFname(),
                        baseUser.getLname(),
                        baseUser.getUsername(),
                        baseUser.getPassword(),
                        baseUser.getEmail(),
                        rs.getInt("is_digital") == 1
                );
                artist.setId(userId);
                return artist;
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving artist details: " + e.getMessage());
        }
        return null;
    }


    public List<Artist> getAllArtists() {
        List<Artist> artists = new ArrayList<>();
        String sql = "SELECT user_id FROM artist_details;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             var rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Artist artist = getArtistById(rs.getInt("user_id"));
                if (artist != null) {
                    artists.add(artist);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all artists: " + e.getMessage());
        }
        return artists;
    }

    public synchronized void deleteArtistDetails(int userId) {
        String sql = "DELETE FROM artist_details WHERE user_id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
            System.out.println("Artist details deleted for user ID: " + userId);
        } catch (SQLException e) {
            System.err.println("Error deleting artist details: " + e.getMessage());
        }
    }

    public Artist fromDTO(ArtistDTO dto) {
        if (dto == null) {
            return null;
        }
        Artist artist = new Artist(
                dto.getFname(), dto.getLname(), dto.getUsername(),
                dto.getPassword(), dto.getEmail(), dto.isDigital());
        artist.setId(dto.getId());
        return artist;
    }

    public ArtistDTO toDTO(Artist artist) {
        return new ArtistDTO(
                artist.getId(),
                artist.getFname(),
                artist.getLname(),
                artist.getUsername(),
                artist.getEmail(),
                "ARTIST",
                artist.isDigital(),
                artist.getPassword()
        );
    }

}