package model;



import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

        // Read data from CSV
        List<List<String>> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("JAVA/movies.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 4) {
                    List<String> row = new ArrayList<>();
                    for (String value : values) {
                        row.add(value);
                    }
                    data.add(row);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Set data to the TableView
        observableData = FXCollections.observableArrayList(data);
        tableView.setItems(observableData);

        // Create Select Seat button
        Button selectSeatButton = new Button("Select Seat");
        selectSeatButton.setOnAction(event -> {
            List<String> selectedItem = tableView.getSelectionModel().getSelectedItem();
            if (selectedItem == null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("No Movie Selected");
                alert.setHeaderText(null);
                alert.setContentText("Please select a movie from the table.");
                alert.showAndWait();
            } else {
                // Extract necessary data from the selected row
                int id = 1; // Assign an appropriate ID
                int movieId = 101; // Assign the relevant movie ID
                String seatNumber = "A1"; // Specify the seat number
                boolean isBooked = false; // Set the booking status

                // Create a new Seat object
                Seat seat = new Seat(id, movieId, seatNumber, isBooked);

                // Proceed with your logic using the 'seat' object
                System.out.println("Selected Seat: " + seat.getSeatNumber());
            }
        });

        // Layout setup
        HBox buttonBox = new HBox(selectSeatButton);
        buttonBox.setPadding(new Insets(10));

        this.setCenter(tableView);
        this.setBottom(buttonBox);
    }
}
