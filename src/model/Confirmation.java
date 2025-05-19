package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Confirmation {

    private String movieTitle;
    private int numberOfSeats;
    private double totalPrice;

    public Confirmation(String movieTitle, int numberOfSeats, double totalPrice) {
        this.movieTitle = movieTitle;
        this.numberOfSeats = numberOfSeats;
        this.totalPrice = totalPrice;

        Stage primaryStage = new Stage();
        primaryStage.setTitle("Confirmation");
        Image icon = new Image("Logo.jpeg");
        primaryStage.getIcons().add(icon);

        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(20));
        String ticketNumber = generateTicketNumber();

        Label ticketNumberLabel = new Label("Ticket Number: " + ticketNumber);
        Label movieTitleLabel = new Label("Movie Title: " + movieTitle);
        Label numberOfSeatsLabel = new Label("Number of Seats: " + numberOfSeats);
        Label totalPriceLabel = new Label("Total Price: " + totalPrice);
        Button confirmButton = new Button("Confirm");
        confirmButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("CINEBOOK");
            alert.setHeaderText(null);
            alert.setContentText("Ticket Successfully Purchased");
            alert.showAndWait();
            writeTicketToDatabase(ticketNumber);
            updateRevenueDatabase();
        });

        vbox.getChildren().addAll(ticketNumberLabel, movieTitleLabel, numberOfSeatsLabel, totalPriceLabel, confirmButton);

        Scene scene = new Scene(vbox, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private String generateTicketNumber() {
        Random random = new Random();
        int ticketNumber = random.nextInt(100000) + 1;
        return String.format("%06d", ticketNumber);
    }

    private void writeTicketToDatabase(String ticketNumber) {
        String query = "INSERT INTO ticket_purchases (ticket_number, movie_id, number_of_seats, total_price) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            // Get movie_id from the movie title (simplified approach)
            int movieId = getMovieIdByTitle(movieTitle);

            ps.setString(1, ticketNumber);
            ps.setInt(2, movieId);
            ps.setInt(3, numberOfSeats);
            ps.setDouble(4, totalPrice);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateRevenueDatabase() {
        String query = "UPDATE revenue SET amount = amount + ? WHERE movie_id = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            int movieId = getMovieIdByTitle(movieTitle);
            ps.setDouble(1, totalPrice);
            ps.setInt(2, movieId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getMovieIdByTitle(String movieTitle) {
       String query = "SELECT id FROM movies WHERE title = ?";
       try (Connection conn = DBHelper.getConnection(); 
           PreparedStatement ps = conn.prepareStatement(query)) {

          ps.setString(1, movieTitle);

        try (var rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("id"); 
            } else {
                System.err.println("Movie with title '" + movieTitle + "' not found.");
                return -1;  
            }
        }

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;  
        }
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
