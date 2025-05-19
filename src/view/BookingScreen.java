package view;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.BookingClient;
import model.BookingDAO;

import java.util.Arrays;
import java.util.List;

public class BookingScreen extends Application {

    @Override
    public void start(Stage primaryStage) {
        Label movieLabel = new Label("Movie ID:");
        TextField movieField = new TextField();

        Label nameLabel = new Label("Customer Name:");
        TextField nameField = new TextField();

        Label seatsLabel = new Label("Seat Numbers (comma separated):");
        TextField seatsField = new TextField();

        Button bookButton = new Button("Book Ticket");

        bookButton.setOnAction(e -> {
            int movieId = Integer.parseInt(movieField.getText().trim());
            String customerName = nameField.getText().trim();
            List<String> seatNumbers = Arrays.asList(seatsField.getText().split(","));

            BookingDAO bookingDAO = new BookingDAO();

            Task<Void> bookingTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    bookingDAO.bookTicket(movieId, customerName, seatNumbers);

                    String response = BookingClient.sendBookingConfirmation("Booking by " + customerName);
                    System.out.println("Server Response: " + response);
                    return null;
                }
            };

            bookingTask.setOnSucceeded(ev -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Booking Successful");
                alert.setHeaderText(null);
                alert.setContentText("Booking completed successfully!");
                alert.showAndWait();
            });

            bookingTask.setOnFailed(ev -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Booking Failed");
                alert.setHeaderText(null);
                alert.setContentText("Booking failed. Please try again.");
                alert.showAndWait();
            });

            new Thread(bookingTask).start();
        });

        VBox root = new VBox(10, movieLabel, movieField, nameLabel, nameField, seatsLabel, seatsField, bookButton);
        root.setPadding(new Insets(15));

        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.setTitle("Booking Screen");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
