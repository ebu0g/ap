package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Customer extends BorderPane {

    private TableView<List<String>> tableView;
    private ObservableList<List<String>> observableData;
    

    public Customer() {
        
        // Initialize the TableView
        tableView = new TableView<>();

        // Define columns
        TableColumn<List<String>, String> col1 = new TableColumn<>("Title");
        TableColumn<List<String>, String> col2 = new TableColumn<>("Duration (minutes)");
        TableColumn<List<String>, String> col3 = new TableColumn<>("Price (Birr)");
        TableColumn<List<String>, String> col4 = new TableColumn<>("ShowTime");

        // Set cell value factories
        col1.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(0)));
        col2.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(1)));
        col3.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(2)));
        col4.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(3)));

        // Add the columns to the table view
        tableView.getColumns().add(col1);
        tableView.getColumns().add(col2);
        tableView.getColumns().add(col3);
        tableView.getColumns().add(col4);

        // Read data from the database
        List<List<String>> data = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection()){
            String query = "SELECT title, duration, price, showtime FROM movies"; // Example query
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    List<String> row = new ArrayList<>();
                    row.add(rs.getString("title"));
                    row.add(String.valueOf(rs.getInt("duration")));
                    row.add(String.valueOf(rs.getDouble("price")));
                    row.add(rs.getString("showtime"));
                    data.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Set data to the TableView
        observableData = FXCollections.observableArrayList(data);
        tableView.setItems(observableData);

    
        Button selectSeatButton = new Button("Select Seat");
        selectSeatButton.setOnAction(event -> {
    List<String> selectedItem = tableView.getSelectionModel().getSelectedItem();
    if (selectedItem != null) {
        String movieTitle = selectedItem.get(0);
        handleSeatSelection(movieTitle);
    } else {
        showAlert("No Selection", "Please select a movie from the list first.");
    }
});

        
        Button rateMovieButton = new Button("Rate Movie");
        rateMovieButton.setOnAction(event -> handleRating());

        HBox buttonBox = new HBox(10, selectSeatButton, rateMovieButton);
        buttonBox.setPadding(new Insets(10));

        this.setCenter(tableView);
        this.setBottom(buttonBox);
    }

     // Method to handle seat selection for a movie
    public void handleSeatSelection(String movieTitle) {
    int movieId = getMovieIdByTitle(movieTitle);
    if (movieId == -1) {
        showAlert("Movie Not Found", "The selected movie does not exist.");
        return;
    }

    // Fetch available seats
    List<String> availableSeats = new ArrayList<>();
    String query = "SELECT seat_number FROM seats WHERE movie_id = ? AND is_booked = 0";
    try (Connection conn = DBHelper.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, movieId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            availableSeats.add(rs.getString("seat_number"));
        }
    } catch (SQLException e) {
        e.printStackTrace();
        showAlert("Database Error", "Error fetching available seats.");
        return;
    }

    if (availableSeats.isEmpty()) {
        showAlert("No Seats Available", "There are no available seats for this movie.");
        return;
    }

    // Ask the user to select a seat
    ChoiceDialog<String> seatDialog = new ChoiceDialog<>(availableSeats.get(0), availableSeats);
    seatDialog.setTitle("Select a Seat");
    seatDialog.setHeaderText("Available seats for " + movieTitle);
    seatDialog.setContentText("Choose your seat:");

    seatDialog.showAndWait().ifPresent(selectedSeat -> {
        try (Connection conn = DBHelper.getConnection()) {
            String update = "UPDATE seats SET is_booked = 1 WHERE movie_id = ? AND seat_number = ?";
            try (PreparedStatement stmt = conn.prepareStatement(update)) {
                stmt.setInt(1, movieId);
                stmt.setString(2, selectedSeat);
                int updated = stmt.executeUpdate();
                if (updated > 0) {
                    showAlert("Booking Confirmed", "Seat " + selectedSeat + " has been booked successfully!");
                } else {
                    showAlert("Booking Failed", "Could not book the selected seat.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to book the selected seat.");
        }
    });
}

    private void handleRating() {
        List<String> selectedItem = tableView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert("No Movie Selected", "Please select a movie to rate.");
            return;
        }

        String movieTitle = selectedItem.get(0);
        int movieId = getMovieIdByTitle(movieTitle);

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Rate Movie");
        dialog.setHeaderText("Provide a rating between 1 and 5 for " + movieTitle);
        dialog.setContentText("Rating:");

        dialog.showAndWait().ifPresent(input -> {
            try {
                int rating = Integer.parseInt(input);
                if (rating < 1 || rating > 5) {
                    showAlert("Invalid Rating", "Rating must be between 1 and 5.");
                    return;
                }

                try (Connection conn = DBHelper.getConnection()) {
                    String insertRating = "INSERT INTO rating (movie_id, rating) VALUES (?, ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(insertRating)) {
                        stmt.setInt(1, movieId);
                        stmt.setInt(2, rating);
                        stmt.executeUpdate();
                        showAlert("Thank You", "Your rating has been recorded.");
                    }
                }
            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Please enter a valid number.");
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Database Error", "Failed to save your rating.");
            }
        });
    }

    // Method to fetch the movie ID by title
    public int getMovieIdByTitle(String title) {
        String query = "SELECT id FROM movies WHERE title = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, title);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching movie ID: " + e.getMessage());
        }
        return -1; // Return -1 if movie not found
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Main method or other methods for customer interaction
    public static void main(String[] args) {
        Customer customer = new Customer();

        // Example: Handling seat selection for a specific movie
        customer.handleSeatSelection("The Avengers");
    }
}
