package model;

import java.sql.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * RevenueDAO
 *
 *  - getAllRevenue()  – revenue per movie (price × number of booked seats)
 *  - getTotalRevenue() – grand-total across all movies
 *  - updateRevenueAfterBooking(...) – utility that flips the seat to "booked"
 *    and (optionally) creates a row in the seats table if it does not yet exist.
 *
 *  NOTE: this version uses the *seats* table's `is_booked` flag as the single
 *        source of truth for whether a ticket has been sold. If you always
 *        set `is_booked = 1` when a booking succeeds, the numbers here will
 *        always be accurate.
 */
public class RevenueDAO {

    // SQL constants
    private static final String UPDATE_REVENUE = """
        INSERT INTO revenue (movie_id, total_revenue)
        VALUES (?, ?)
        ON CONFLICT(movie_id)
        DO UPDATE SET total_revenue = total_revenue + EXCLUDED.total_revenue
    """;

    private static final String GET_STORED_REVENUE = """
        SELECT m.title, r.total_revenue
        FROM revenue r
        JOIN movies m ON r.movie_id = m.id
        ORDER BY m.title
    """;

    /* ------------------------------------------------------------------ */
    /*  1.  Per-movie revenue list                                        */
    /* ------------------------------------------------------------------ */
    public ObservableList<Revenue> getAllRevenue() {
        ObservableList<Revenue> list = FXCollections.observableArrayList();
        String sql = """
            SELECT m.title, 
                   COUNT(t.id) * m.price AS total_revenue
            FROM movies m
            LEFT JOIN booking b ON m.id = b.movie_id
            LEFT JOIN ticket t ON b.id = t.booking_id
            GROUP BY m.title, m.price
        """;

        try (Connection connection = DBHelper.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String title = rs.getString("title");
                double revenue = rs.getDouble("total_revenue");
                list.add(new Revenue(title, revenue));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    /* ------------------------------------------------------------------ */
    /*  2.  Grand-total revenue                                           */
    /* ------------------------------------------------------------------ */
    public double getTotalRevenue() {
        String sql = """
            SELECT SUM(m.price)
            FROM movies m
            JOIN booking b ON m.id = b.movie_id
            JOIN ticket t ON b.id = t.booking_id
        """;

        try (Connection connection = DBHelper.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    /* ------------------------------------------------------------------ */
    /*  3.  Utility: mark a seat booked and ensure it exists              */
    /*      (call this from BookingDAO when a purchase succeeds)          */
    /* ------------------------------------------------------------------ */
    public void updateRevenueAfterBooking(Connection conn, int movieId, String seatNumber) throws SQLException {
        // 1. Ensure the seat exists
        String ensureSeat = "INSERT OR IGNORE INTO seats (movie_id, seat_number, is_booked) VALUES (?, ?, 0)";
        try (PreparedStatement ps = conn.prepareStatement(ensureSeat)) {
            ps.setInt(1, movieId);
            ps.setString(2, seatNumber);
            ps.executeUpdate();
        }

        // 2. Mark it as booked
        String markSold = "UPDATE seats SET is_booked = 1 WHERE movie_id = ? AND seat_number = ?";
        try (PreparedStatement ps = conn.prepareStatement(markSold)) {
            ps.setInt(1, movieId);
            ps.setString(2, seatNumber);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new SQLException("Failed to mark seat as booked.");
            }
        }

        // 3. Fetch movie price
        double price = 0.0;
        String getPrice = "SELECT price FROM movies WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(getPrice)) {
            ps.setInt(1, movieId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    price = rs.getDouble("price");
                } else {
                    throw new SQLException("Movie not found: " + movieId);
                }
            }
        }

        // 4. Insert/update revenue table
        try (PreparedStatement ps = conn.prepareStatement(UPDATE_REVENUE)) {
            ps.setInt(1, movieId);
            ps.setDouble(2, price);
            ps.executeUpdate();
        }
    }


    /* ------------------------------------------------------------------ */
    /*  4.  Add revenue entry to revenue table                            */
    /* ------------------------------------------------------------------ */
    public void addRevenue(Connection conn, int movieId, double amount) throws SQLException {
        String sql = """
            INSERT INTO revenue (movie_id, total_revenue)
            VALUES (?, ?)
            ON CONFLICT(movie_id) DO UPDATE SET total_revenue = excluded.total_revenue
        """;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, movieId);
            pstmt.setDouble(2, amount);
            pstmt.executeUpdate();
        }
    }


    /* ------------------------------------------------------------------ */
    /*  5.  Print stored revenue from revenue table                       */
    /* ------------------------------------------------------------------ */
    public void printStoredRevenue() {
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(GET_STORED_REVENUE);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String title = rs.getString("title");
                double revenue = rs.getDouble("total_revenue");
                System.out.println("Movie: " + title + " | Revenue: $" + revenue);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void refreshAllRevenue() {
        String sql = """
            SELECT m.id, COUNT(t.id) * m.price AS total_revenue
            FROM movies m
            LEFT JOIN booking b ON m.id = b.movie_id
            LEFT JOIN ticket t ON b.id = t.booking_id
            GROUP BY m.id, m.price
        """;

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Before updating, clear existing revenue entries to avoid accumulating duplicates:
            try (PreparedStatement clearStmt = conn.prepareStatement("DELETE FROM revenue")) {
                clearStmt.executeUpdate();
            }

            while (rs.next()) {
                int movieId = rs.getInt("id");
                double revenue = rs.getDouble("total_revenue");
                addRevenue(conn, movieId, revenue); // Add new revenue
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
