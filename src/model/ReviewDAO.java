package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ReviewDAO {

    // Method to save a review to the database
    public static void saveReview(int userId, int movieId, String review) {
        try (Connection conn = DBHelper.getConnection()) {
            String query = "INSERT INTO reviews (user_id, movie_id, review) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, movieId);
                stmt.setString(3, review);
                stmt.executeUpdate();
                System.out.println("✅ Review submitted.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to retrieve reviews (example, can be expanded)
    public static void getReviewsForMovie(int movieId) {
        String query = "SELECT users.username, reviews.review FROM reviews " +
                       "JOIN users ON reviews.user_id = users.id " +
                       "WHERE reviews.movie_id = ?";

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, movieId);
            var rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println("User: " + rs.getString("username") + " | Review: " + rs.getString("review"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean submitReview(int userId, int movieId, int rating, String comment) {
        String query = "INSERT INTO reviews (user_id, movie_id, rating, comment) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, movieId);
            stmt.setInt(3, rating);
            stmt.setString(4, comment);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
