package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BookingDAO {
    public boolean bookSeat(int seatId, int userId) {
    String query = "UPDATE seats SET is_booked = 1, booked_by = ? WHERE id = ? AND is_booked = 0";
    try (Connection conn = DBHelper.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, userId);
        stmt.setInt(2, seatId);
        int rowsUpdated = stmt.executeUpdate();
        return rowsUpdated > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
}