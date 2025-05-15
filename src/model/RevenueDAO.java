package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RevenueDAO {

    // Get list of revenue per movie (title + total)
    public List<Revenue> getAllRevenue() {
    List<Revenue> revenueList = new ArrayList<>();
    String query = """
        SELECT m.title, COALESCE(SUM(r.amount), 0) AS total_revenue
        FROM movies m
        LEFT JOIN revenue r ON m.id = r.movie_id
        GROUP BY m.id, m.title
    """;

    try (Connection conn = DBHelper.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query);
         ResultSet rs = pstmt.executeQuery()) {

        while (rs.next()) {
            String title = rs.getString("title");
            double totalRevenue = rs.getDouble("total_revenue");
            revenueList.add(new Revenue(title, totalRevenue));
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return revenueList;
}


    // Update total revenue after a new ticket booking
    public void updateRevenueAfterBooking(int movieId) {
        String sql = """
            UPDATE revenue
            SET total_revenue = total_revenue + (
                SELECT price FROM movies WHERE id = ?
            )
            WHERE movie_id = ?;
        """;

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, movieId);
            pstmt.setInt(2, movieId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Insert total revenue value (used for initialization)
    public void insertRevenue(int movieId, double totalRevenue) {
        String sql = "INSERT INTO revenue(movie_id, total_revenue) VALUES (?, ?)";

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, movieId);
            pstmt.setDouble(2, totalRevenue);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

     
    // Insert or update revenue if entry exists
    public void insertOrUpdateRevenue(Connection conn, int movieId, double amount) throws SQLException {
        String selectSql = "SELECT total_revenue FROM revenue WHERE movie_id = ?";
        try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
            selectStmt.setInt(1, movieId);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                // Update existing revenue
                double currentRevenue = rs.getDouble("total_revenue");
                double newRevenue = currentRevenue + amount;
                String updateSql = "UPDATE revenue SET total_revenue = ? WHERE movie_id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setDouble(1, newRevenue);
                    updateStmt.setInt(2, movieId);
                    updateStmt.executeUpdate();
                }
            } else {
                // Insert new revenue
                String insertSql = "INSERT INTO revenue (movie_id, total_revenue) VALUES (?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, movieId);
                    insertStmt.setDouble(2, amount);
                    insertStmt.executeUpdate();
                }
            }
        }
    }

    // Get total revenue across all movies (optional utility)
    public double getTotalRevenue() {
        String sql = "SELECT SUM(total_revenue) AS total FROM revenue";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public double calculateRevenueForMovie(int movieId) {
    double totalRevenue = 0.0;
    String sql = "SELECT SUM(amount) FROM revenue WHERE movie_id = ?";

    try (Connection conn = DBHelper.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, movieId);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            totalRevenue = rs.getDouble(1);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return totalRevenue;
}

}
