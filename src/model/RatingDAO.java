package model;


import java.sql.Connection;

public class RatingDAO {
    public static void rateMovie(int userId, int movieId, double score) {
        try (Connection conn = DBHelper.getConnection();
             var stmt = conn.prepareStatement("INSERT INTO ratings(user_id, movie_id, score) VALUES (?, ?, ?)")) {
            stmt.setInt(1, userId);
            stmt.setInt(2, movieId);
            stmt.setDouble(3, score);
            stmt.executeUpdate();
            System.out.println("✅ Rating submitted.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
