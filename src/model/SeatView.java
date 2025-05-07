package model;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

public class SeatView extends Application {

    private TableView<Seat> seatTableView;
    private ObservableList<Seat> seatData;
    private int movieId;
    private String movieTitle;

    public SeatView(int movieId, String movieTitle) {
        this.movieId = movieId;
        this.movieTitle = movieTitle;
    }

    @Override
    public void start(Stage primaryStage) {
        seatTableView = new TableView<>();
        seatData = FXCollections.observableArrayList();

        // Define columns
        TableColumn<Seat, Number> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());

        TableColumn<Seat, Number> movieIdColumn = new TableColumn<>("Movie ID");
        movieIdColumn.setCellValueFactory(cellData -> cellData.getValue().movieIdProperty());

        TableColumn<Seat, String> seatNumberColumn = new TableColumn<>("Seat Number");
        seatNumberColumn.setCellValueFactory(cellData -> cellData.getValue().seatNumberProperty());

        TableColumn<Seat, Boolean> isBookedColumn = new TableColumn<>("Booked");
        isBookedColumn.setCellValueFactory(cellData -> cellData.getValue().isBookedProperty());

        // Add columns individually
        seatTableView.getColumns().add(idColumn);
        seatTableView.getColumns().add(movieIdColumn);
        seatTableView.getColumns().add(seatNumberColumn);
        seatTableView.getColumns().add(isBookedColumn);


        // Load data
        loadSeatData(movieId);

        VBox vbox = new VBox(10, seatTableView);
        vbox.setPadding(new Insets(10));

        Scene scene = new Scene(vbox, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Seats for: " + movieTitle);
        primaryStage.show();
    }

    private void loadSeatData(int selectedMovieId) {
        seatData.clear();
        String query = "SELECT * FROM seats WHERE movie_id = ?";

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, selectedMovieId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                int movieId = rs.getInt("movie_id");
                String seatNumber = rs.getString("seat_number");
                boolean isBooked = rs.getBoolean("is_booked");

                seatData.add(new Seat(id, movieId, seatNumber, isBooked));
            }

            seatTableView.setItems(seatData);

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions appropriately
        }
    }
}
