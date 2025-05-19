package model;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

        public static boolean addSeat(Seat seat) {
            String sql = "INSERT INTO seats (movie_id, seat_number, is_booked) VALUES (?, ?, ?)";

            try (Connection conn = DBHelper.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, seat.getMovieId());
                stmt.setString(2, seat.getSeatNumber());
                stmt.setBoolean(3, seat.getIsBooked());
                stmt.executeUpdate();
                return true;

            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        public static boolean addSeat(int movieId, String seatNumber) {
            return addSeat(new Seat(0, movieId, seatNumber, false)); // defaults isBooked to false
        }

        public List<Seat> getSeatsByMovieId(int movieId) {
    List<Seat> seats = new ArrayList<>();
    String sql = "SELECT * FROM seats WHERE movie_id = ? ORDER BY CAST(SUBSTR(seat_number, 2) AS INTEGER) ASC";

    try (Connection conn = DBHelper.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, movieId);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            int id = rs.getInt("id");
            int mId = rs.getInt("movie_id");
            String seatNumber = rs.getString("seat_number");
            boolean isBooked = rs.getBoolean("is_booked");

            Seat seat = new Seat(id, mId, seatNumber, isBooked);
            seats.add(seat);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return seats;
}




        public class SeatTest {
            public static void main(String[] args) {
                boolean success = SeatDAO.addSeat(1, "s3");  // Try adding seat A3 for movie ID 1
                if (success) {
                    System.out.println("Seat added.");
                } else {
                    System.out.println("Failed to add seat.");
                }
            }
        }
}
    
