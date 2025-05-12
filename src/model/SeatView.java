package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.*;

public class SeatView {
    private TableView<Seat> seatTableView;
    private ObservableList<Seat> seatData;
    private int movieId;

    public VBox getViewForMovie(int movieId, String movieTitle) {
        this.movieId = movieId;
        seatTableView = new TableView<>();
        seatData = FXCollections.observableArrayList();

        // Table columns
        TableColumn<Seat, Number> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cell -> cell.getValue().idProperty());

        TableColumn<Seat, String> seatNumberColumn = new TableColumn<>("Seat Number");
        seatNumberColumn.setCellValueFactory(cell -> cell.getValue().seatNumberProperty());

        TableColumn<Seat, Boolean> isBookedColumn = new TableColumn<>("Booked");
        isBookedColumn.setCellValueFactory(cell -> cell.getValue().isBookedProperty());

        // Add columns individually
        seatTableView.getColumns().add(idColumn);
        seatTableView.getColumns().add(seatNumberColumn);
        seatTableView.getColumns().add(isBookedColumn);

        loadSeatData();

        // Buttons
        Button deleteSeatBtn = new Button("Delete Selected");
        Button updateSeatBtn = new Button("Update Seat Number");
        Button refreshBtn = new Button("Refresh");

        deleteSeatBtn.setOnAction(e -> deleteSelectedSeat());
        updateSeatBtn.setOnAction(e -> updateSeatDialog());
        refreshBtn.setOnAction(e -> loadSeatData());

        HBox buttonBox = new HBox(10, deleteSeatBtn, updateSeatBtn, refreshBtn);

        VBox vbox = new VBox(10,
                new Label("Available Seats for Movie: " + movieTitle),
                seatTableView,
                buttonBox
        );
        vbox.setPadding(new Insets(10));

        return vbox;
    }

    private void loadSeatData() {
        seatData.clear();
        String query = "SELECT * FROM seats WHERE movie_id = ? AND is_booked = 0";

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, movieId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String seatNumber = rs.getString("seat_number");
                boolean isBooked = rs.getBoolean("is_booked");

                seatData.add(new Seat(id, movieId, seatNumber, isBooked));
            }

            seatTableView.setItems(seatData);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error loading seat data.");
        }
    }

    private void deleteSelectedSeat() {
        Seat selected = seatTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No seat selected.");
            return;
        }

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM seats WHERE id = ?")) {

            stmt.setInt(1, selected.getId());
            stmt.executeUpdate();
            loadSeatData();  // Reload seat data after deletion

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error deleting seat.");
        }
    }

    private void updateSeatDialog() {
        Seat selected = seatTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No seat selected.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(selected.getSeatNumber());
        dialog.setTitle("Update Seat Number");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter new seat number:");

        dialog.showAndWait().ifPresent(newSeatNumber -> {
            if (newSeatNumber.trim().isEmpty()) {
                showAlert("Seat number cannot be empty.");
                return;
            }

            try (Connection conn = DBHelper.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("UPDATE seats SET seat_number = ? WHERE id = ?")) {

                stmt.setString(1, newSeatNumber);
                stmt.setInt(2, selected.getId());
                stmt.executeUpdate();
                loadSeatData();

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error updating seat number.");
            }
        });
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }
}
