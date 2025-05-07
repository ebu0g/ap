package model;


import java.sql.*;

public class SeatDAO {

        public static void showAvailableSeats(int movieId) {
            String query = "SELECT id, seat_number FROM seats WHERE movie_id = ? AND is_booked = 0";
    
            try (Connection conn = DBHelper.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
    
                stmt.setInt(1, movieId);
                ResultSet rs = stmt.executeQuery();
    
                System.out.println("Available seats for movie ID " + movieId + ":");
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String seatNumber = rs.getString("seat_number");
                    System.out.println("Seat ID: " + id + ", Seat Number: " + seatNumber);
                }
    
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle exceptions appropriately
            }
        }
}
    
