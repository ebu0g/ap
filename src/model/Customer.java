package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;

public class Customer extends BorderPane {

    private TableView<List<String>> tableView;
    private ObservableList<List<String>> observableData;

    public Customer() {

        tableView = new TableView<>();

        TableColumn<List<String>, String> col1 = new TableColumn<>("Title");
        TableColumn<List<String>, String> col2 = new TableColumn<>("Duration (minutes)");
        TableColumn<List<String>, String> col3 = new TableColumn<>("Price (Birr)");
        TableColumn<List<String>, String> col4 = new TableColumn<>("ShowTime");

        col1.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(0)));
        col2.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(1)));
        col3.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(2)));
        col4.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(3)));

        tableView.getColumns().add(col1);
        tableView.getColumns().add(col2);
        tableView.getColumns().add(col3);
        tableView.getColumns().add(col4);

        loadDataFromDatabase();

        Button bookTicketButton = new Button("Book Ticket");
        bookTicketButton.setOnAction(event -> {
            List<String> selectedItem = tableView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                String movieTitle = selectedItem.get(0);
                handleSeatSelection(movieTitle);
            } else {
                showAlert("No Selection", "Please select a movie to book a ticket.");
            }
        });

        Button reviewMovieButton = new Button("Review Movie");
        reviewMovieButton.setOnAction(event -> handleRating());

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(event -> {
            Stage stage = (Stage) this.getScene().getWindow();
            stage.close();
        });

        HBox buttonBox = new HBox(10, bookTicketButton, reviewMovieButton, exitButton);
        buttonBox.setPadding(new Insets(10));

        this.setCenter(tableView);
        this.setBottom(buttonBox);
    }

    private void loadDataFromDatabase() {
        List<List<String>> data = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection()) {
            String query = "SELECT title, duration, price, showtime FROM movies";
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
        observableData = FXCollections.observableArrayList(data);
        tableView.setItems(observableData);
    }

    public void handleSeatSelection(String movieTitle) {
        int movieId = getMovieIdByTitle(movieTitle);
        if (movieId == -1) {
            showAlert("Movie Not Found", "The selected movie does not exist.");
            return;
        }

        String selectedSeat = null;
        String selectAvailableSeatSQL = """
            SELECT seat_number
            FROM seats
            WHERE movie_id = ?
            AND seat_number NOT IN (
                SELECT seat_number FROM booking WHERE movie_id = ?
            )
            ORDER BY CAST(SUBSTR(seat_number, 2) AS INTEGER)
            LIMIT 1
        """;

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(selectAvailableSeatSQL)) {
            stmt.setInt(1, movieId);
            stmt.setInt(2, movieId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                selectedSeat = rs.getString("seat_number");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to find available seat.");
            return;
        }

        if (selectedSeat == null) {
            showAlert("No Seats Available", "All seats for this movie are already booked.");
            return;
        }

        try (Connection conn = DBHelper.getConnection()) {
            conn.setAutoCommit(false);

            String insertBookingSQL = "INSERT OR IGNORE INTO booking (movie_id, seat_number) VALUES (?, ?)";
            try (PreparedStatement bookingStmt = conn.prepareStatement(insertBookingSQL, Statement.RETURN_GENERATED_KEYS)) {
                bookingStmt.setInt(1, movieId);
                bookingStmt.setString(2, selectedSeat);
                bookingStmt.executeUpdate();

                ResultSet keys = bookingStmt.getGeneratedKeys();
                if (keys.next()) {
                    int bookingId = keys.getInt(1);

                    String insertTicketSQL = "INSERT INTO ticket (booking_id, movie_id, seat_number) VALUES (?, ?, ?)";
                    try (PreparedStatement ticketStmt = conn.prepareStatement(insertTicketSQL)) {
                        ticketStmt.setInt(1, bookingId);
                        ticketStmt.setInt(2, movieId);
                        ticketStmt.setString(3, selectedSeat);
                        ticketStmt.executeUpdate();
                    }

                    conn.commit();
                    showAlert("Booking Confirmed", "Seat " + selectedSeat + " successfully booked.");
                } else {
                    conn.rollback();
                    showAlert("Booking Failed", "Seat was already booked.");
                }
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                showAlert("Booking Failed", "Seat might have been already booked.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleRating() {
        List<String> selectedItem = tableView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert("No Movie Selected", "Please select a movie to rate.");
            return;
        }

        String movieTitle = selectedItem.get(0);
        int movieId = getMovieIdByTitle(movieTitle);

        TextInputDialog ratingDialog = new TextInputDialog();
        ratingDialog.setTitle("Review Movie");
        ratingDialog.setHeaderText("Provide a rating between 1 and 5 for " + movieTitle);
        ratingDialog.setContentText("Rating:");

        ratingDialog.showAndWait().ifPresent(ratingInput -> {
            try {
                int rating = Integer.parseInt(ratingInput);
                if (rating < 1 || rating > 5) {
                    showAlert("Invalid Rating", "Rating must be between 1 and 5.");
                    return;
                }

                TextInputDialog commentDialog = new TextInputDialog();
                commentDialog.setTitle("Review Movie");
                commentDialog.setHeaderText("Leave a comment for " + movieTitle);
                commentDialog.setContentText("Comment:");

                commentDialog.showAndWait().ifPresent(comment -> {
                    try (Connection conn = DBHelper.getConnection()) {
                        String insertReview = "INSERT INTO review (movie_id, rating, comment) VALUES (?, ?, ?)";
                        try (PreparedStatement stmt = conn.prepareStatement(insertReview)) {
                            stmt.setInt(1, movieId);
                            stmt.setInt(2, rating);
                            stmt.setString(3, comment);
                            stmt.executeUpdate();
                            showAlert("Thank You", "Your review has been recorded.");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        showAlert("Database Error", "Failed to save your review.");
                    }
                });

            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Please enter a valid number for rating.");
            }
        });
    }

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
        return -1;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
