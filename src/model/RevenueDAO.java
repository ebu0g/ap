package model;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RevenueDAO {
    public List<Revenue> getAllRevenue() {
        List<Revenue> revenueList = new ArrayList<>();
        String query = "SELECT m.title, r.total_revenue " +
                       "FROM revenue r JOIN movies m ON r.movie_id = m.id";

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


    public double calculateRevenueForMovie(int movieId) {
    double total = 0.0;
    String sql = "SELECT COUNT(*) FROM seat_selection WHERE movie_id = ? AND booked = 1"; // assuming 'booked' marks confirmed seats
    try (Connection conn = DBHelper.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, movieId);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            int seatsBooked = rs.getInt(1);
            double ticketPrice = 100.0; // Set your fixed ticket price here
            total = seatsBooked * ticketPrice;
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return total;
}

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

public void insertOrUpdateRevenue(int movieId, double amount) {
        String sql = """
            INSERT INTO revenue (movie_id, total_revenue)
            VALUES (?, ?)
            ON CONFLICT(movie_id) DO UPDATE SET total_revenue = total_revenue + excluded.total_revenue;
        """;

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, movieId);
            pstmt.setDouble(2, amount);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}



