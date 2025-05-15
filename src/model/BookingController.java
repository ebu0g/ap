package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.scene.control.Alert;
import javafx.application.Platform;

public class BookingController {

    public void bookTicket(Movie selectedMovie, String seatNumber, double ticketPrice) {
    if (selectedMovie == null || seatNumber == null || seatNumber.isEmpty()) {
        showAlert("Booking Error", "Please select a movie and seat.");
        return;
    }
    int movieId = selectedMovie.getId();

    try (Connection conn = DBHelper.getConnection()) {
        conn.setAutoCommit(false);

        String bookingSql = "INSERT INTO bookings (movie_id, seat_number) VALUES (?, ?)";
        try (PreparedStatement bookingStmt = conn.prepareStatement(bookingSql)) {
            bookingStmt.setInt(1, movieId);
            bookingStmt.setString(2, seatNumber);
            bookingStmt.executeUpdate();
        }

        RevenueDAO revenueDAO = new RevenueDAO();
        revenueDAO.insertOrUpdateRevenue(conn, movieId, ticketPrice);

        conn.commit();

        showAlert("Success", "Ticket booked successfully!");
    } catch (SQLException ex) {
        ex.printStackTrace();
        showAlert("Database Error", "Failed to book ticket.");
    }
}

    // Simple alert helper method using JavaFX Alert
    private void showAlert(String title, String message) {
        // Run on JavaFX Application Thread to avoid threading issues
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }


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

}
