package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class BookingDAO {
    public void bookTicket(int movieId, String customerName, List<String> seatNumbers) {
        try (Connection conn = DBHelper.getConnection()) {
            conn.setAutoCommit(false);

            int bookingId = insertBooking(conn, movieId, customerName);

            RevenueDAO revenueDAO = new RevenueDAO();

            for (String seat : seatNumbers) {
                insertTicket(conn, bookingId, seat); 
                revenueDAO.updateRevenueAfterBooking(conn, movieId, seat);
            }

            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean bookSeatAndUpdateRevenue(Connection conn, String movieId, String seatNumber) {
        try {
            conn.setAutoCommit(false);

            String checkSeatQuery = "SELECT is_booked FROM seat WHERE movie_id = ? AND seat_number = ?";
            try (PreparedStatement ps = conn.prepareStatement(checkSeatQuery)) {
                ps.setString(1, movieId);
                ps.setString(2, seatNumber);
                ResultSet rs = ps.executeQuery();

                if (!rs.next()) {
                    System.out.println("Seat not found for movie ID " + movieId);
                    conn.rollback();
                    return false;
                }

                if (rs.getInt("is_booked") == 1) {
                    System.out.println("Seat " + seatNumber + " is already booked.");
                    conn.rollback();
                    return false;
                }
            }

            String updateSeatQuery = "UPDATE seat SET is_booked = 1 WHERE movie_id = ? AND seat_number = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateSeatQuery)) {
                ps.setString(1, movieId);
                ps.setString(2, seatNumber);
                int updated = ps.executeUpdate();
                if (updated == 0) {
                    System.out.println("Failed to book seat.");
                    conn.rollback();
                    return false;
                }
            }

            double price = 0.0;
            String priceQuery = "SELECT price FROM movies WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(priceQuery)) {
                ps.setString(1, movieId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    price = rs.getDouble("price");
                } else {
                    System.out.println("Movie not found: " + movieId);
                    conn.rollback();
                    return false;
                }
            }

            String updateRevenueQuery = 
                "INSERT INTO revenue (movie_id, total_revenue) VALUES (?, ?) " +
                "ON CONFLICT(movie_id) DO UPDATE SET total_revenue = total_revenue + ?";

            try (PreparedStatement ps = conn.prepareStatement(updateRevenueQuery)) {
                ps.setString(1, movieId);
                ps.setDouble(2, price);
                ps.setDouble(3, price);
                int revenueUpdated = ps.executeUpdate();
                if (revenueUpdated == 0) {
                    System.out.println("Revenue update failed.");
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            System.out.println("Booking and revenue update completed successfully.");
            return true;

        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Rollback failed: " + rollbackEx.getMessage());
            }
            System.err.println("Booking failed: " + e.getMessage());
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Failed to reset auto-commit: " + e.getMessage());
            }
        }
    }

    private int insertBooking(Connection conn, int movieId, String customerName) throws SQLException {
        String sql = "INSERT INTO booking (movie_id, customer_name) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, movieId);
            ps.setString(2, customerName);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);  // booking ID
            } else {
                throw new SQLException("Failed to retrieve booking ID.");
            }
        }
    }

    private void insertTicket(Connection conn, int bookingId, String seatNumber) throws SQLException {
        String sql = "INSERT INTO ticket (booking_id, seat_number) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ps.setString(2, seatNumber);
            ps.executeUpdate();
        }

        String updateSeatSql = "UPDATE seat SET is_booked = 1 WHERE seat_number = ?";
        try (PreparedStatement ps = conn.prepareStatement(updateSeatSql)) {
            ps.setString(1, seatNumber);
            ps.executeUpdate();
        }
    }
}
