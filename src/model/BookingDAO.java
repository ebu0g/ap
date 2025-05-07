package model;

import java.sql.Connection;

public class BookingDAO {
    public static void bookSeat(int userId, int seatId) {
        try (Connection conn = DBHelper.getConnection()) {
            conn.setAutoCommit(false);

            try (var insertStmt = conn.prepareStatement("INSERT INTO bookings(user_id, seat_id) VALUES (?, ?)");
                 var updateStmt = conn.prepareStatement("UPDATE seats SET is_booked = 1 WHERE id = ?")) {

                insertStmt.setInt(1, userId);
                insertStmt.setInt(2, seatId);
                insertStmt.executeUpdate();

                updateStmt.setInt(1, seatId);
                updateStmt.executeUpdate();

                conn.commit();
                System.out.println("✅ Seat booked successfully.");
            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}