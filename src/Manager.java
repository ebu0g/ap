import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


public class Manager {
    
    public BorderPane getRootPane() {
        return borderPane;
    }    

    BorderPane borderPane;
    TableView<List<String>> tableView;
    ObservableList<List<String>> observableData;
    TextField field1, field2, field3, field4;

    public Manager() {
        // Create a table view
        tableView = new TableView<>();
        tableView.setPrefWidth(400);
        tableView.setPrefHeight(300);

        // Create table columns
        TableColumn<List<String>, String> col1 = new TableColumn<>("Title");
        TableColumn<List<String>, String> col2 = new TableColumn<>("Duration (minutes)");
        TableColumn<List<String>, String> col3 = new TableColumn<>("Price (Birr)");
        TableColumn<List<String>, String> col4 = new TableColumn<>("ShowTime");

        // Set cell value factories for the columns
        col1.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(0)));
        col2.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(1)));
        col3.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(2)));
        col4.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(3)));

        col1.setMaxWidth(300);

        // Add the columns to the table view
        tableView.getColumns().add(col1);
        tableView.getColumns().add(col2);
        tableView.getColumns().add(col3);
        tableView.getColumns().add(col4);


        tableView.setRowFactory(tv -> new TableRow<List<String>>() {
            @Override
            protected void updateItem(List<String> item, boolean empty) {
                super.updateItem(item, empty);
                if (getIndex() % 2 == 0) {
                    setStyle("-fx-background-color: #f2f2f2;");
                } else {
                    setStyle("");
                }
            }
        });

        tableView.setRowFactory(tv -> {
            TableRow<List<String>> row = new TableRow<>();
            row.setOnMouseEntered(event -> {
                if (!row.isEmpty()) {
                    row.setStyle("-fx-background-color: #e6e6e6;");
                }
            });
            row.setOnMouseExited(event -> {
                if (!row.isEmpty()) {
                    row.setStyle("");
                }
            });
            return row;
        });

        // Read data from a CSV file
        List<List<String>> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("JAVA//movies.csv"))) {
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

        // Set the items of the table view
        observableData = FXCollections.observableArrayList(data);
        tableView.setItems(observableData);

        // Create a button to add data
        Button addButton = new Button("Add Movies");
        addButton.setOnAction(event -> {
            // Create a dialog to get the new data
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add Movies");

            // Create a grid pane to hold the form fields
            GridPane gridPane = new GridPane();
            gridPane.setHgap(10);
            gridPane.setVgap(10);
            gridPane.setPadding(new Insets(20, 150, 10, 10));

            // Create form fields
            field1 = new TextField();
            field1.setPromptText("Peaky Blinders");
            field2 = new TextField();
            field2.setPromptText("90");
            field3 = new TextField();
            field3.setPromptText("250");
            field4 = new TextField();
            field4.setPromptText("12:00 AM");
            // Add form fields to the grid pane
            gridPane.add(new Label("Movie Title:"), 0, 0);
            gridPane.add(field1, 1, 0);
            gridPane.add(new Label("Duration:"), 0, 1);
            gridPane.add(field2, 1, 1);
            gridPane.add(new Label("Price:"), 0, 2);
            gridPane.add(field3, 1, 2);
            gridPane.add(new Label("ShowTime:"), 0, 3);
            gridPane.add(field4, 1, 3);

            // Create a button to add the data
            Button addDataButton = new Button("Add Movie");
            addDataButton.setOnAction(event1 -> {
                // Add the data to the table and the CSV file
                List<String> newData = List.of(field1.getText(), field2.getText(), field3.getText(), field4.getText());
                observableData.add(newData);

                // Write the new data to the CSV file
                try (BufferedWriter bw = new BufferedWriter(new FileWriter("JAVA//movies.csv", true))) {
                    bw.write(String.join(",", newData));
                    bw.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Clear the form fields
                field1.clear();
                field2.clear();
                field3.clear();
                field4.clear();

                // Close the dialog
                dialog.close();
            });

            // Add the button to the grid pane
            gridPane.add(addDataButton, 1, 4);

            // Set the dialog content
            dialog.getDialogPane().setContent(gridPane);

            // Create a button type for the "Add Data" button
            ButtonType addDataButtonType = new ButtonType("Add Data", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().add(addDataButtonType);

            // Show the dialog and wait for the user to respond
            Optional<String> result = dialog.showAndWait();

            // If the user clicked OK, add the new data to the table and the CSV file
            if (result.isPresent()) {
                List<String> newData = List.of(result.get(), "", "", "");
                observableData.add(newData);

                // Write the new data to the CSV file
                try (BufferedWriter bw = new BufferedWriter(new FileWriter("JAVA//movies.csv", true))) {
                    bw.write(String.join(",", newData));
                    bw.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Button deleteButton = new Button("Delete Movies");
        deleteButton.setOnAction(event -> {
            List<List<String>> selectedData = tableView.getSelectionModel().getSelectedItems();
            if (!selectedData.isEmpty()) {
                observableData.removeAll(selectedData);

                // Write the updated data to the CSV file
                try (BufferedWriter bw = new BufferedWriter(new FileWriter("JAVA//movies.csv"))) {
                    for (List<String> row : observableData) {
                        bw.write(String.join(",", row));
                        bw.newLine();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Button updateSeatsButton = new Button("Update Seats");
updateSeatsButton.setOnAction(event -> {
    // Clear the seat CSV file
    try (PrintWriter writer = new PrintWriter("JAVA//seat_selections.csv")) {
        writer.print("");
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    }
    // Show an alert with "Yes" message
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Seat Status");
    alert.setHeaderText(null);
    alert.setContentText("All Seats are set to available");
    alert.showAndWait();
});

Button generateRevenueButton = new Button("Generated Revenue");
generateRevenueButton.setOnAction(event -> {
    // Read revenue values from the file
    List<Double> revenues = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader("JAVA//revenue.txt"))) {
        String line;
        while ((line = br.readLine()) != null) {
            if (!line.isEmpty()) {
                double revenue = Double.parseDouble(line);
                revenues.add(revenue);
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    // Calculate the total revenue
    double totalRevenue = 0;
    for (Double revenue : revenues) {
        totalRevenue += revenue;
    }

    // Display an alert with the result
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Generated Revenue");
    alert.setHeaderText(null);
    alert.setContentText("Total Revenue: " + totalRevenue);
    alert.showAndWait();
});
        
        Button loadButton = new Button("Customer Management");
        loadButton.setOnAction(event -> {
            List<String[]> details = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader("JAVA//CustomerDetail.csv"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] row = line.split(",");
                    if (row.length == 4) { // Adjust the length according to the number of columns in your CSV file
                        details.add(row);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            

    // Create a TableView to display the customer details
    TableView<String[]> tableView = new TableView<>();

    // Create TableColumn instances for each column in the CSV file
    TableColumn<String[], String> ticketNumberColumn = new TableColumn<>("Ticket Number");
    ticketNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[0]));

    TableColumn<String[], String> movieTitleColumn = new TableColumn<>("Movie Title");
    movieTitleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[1]));

    TableColumn<String[], String> numberOfSeatsColumn = new TableColumn<>("Number of Seats");
    numberOfSeatsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[2]));

    TableColumn<String[], String> totalPriceColumn = new TableColumn<>("Total Price");
    totalPriceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[3]));

    // Create a list of TableColumn instances
    List<TableColumn<String[], ?>> columns = Arrays.asList(
        ticketNumberColumn,
        movieTitleColumn,
        numberOfSeatsColumn,
        totalPriceColumn
    );

    // Add all columns to the TableView
    tableView.getColumns().addAll(columns);

    // Add the customer details to the TableView
    tableView.getItems().addAll(details);

    // Set the selection model to select the entire row
    tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    tableView.getSelectionModel().selectFirst();

    Button remove = new Button("Checked");
    remove.setOnAction(e -> {
        String[] selectedRow = tableView.getSelectionModel().getSelectedItem();
    
        // Remove the selected row from the TableView
        tableView.getItems().remove(selectedRow);
    
        // Remove the selected row from the details list
        details.remove(selectedRow);
    
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("JAVACustomerDetail.csv"))) {
            // Write the updated details list back to the CSV file
            for (String[] row : details) {
                String line = String.join(",", row);
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    });

    VBox cusBox = new VBox();
    cusBox.setSpacing(10);

    // Clear the existing content of the VBox
    cusBox.getChildren().clear();

    // Add the TableView and delete button to the VBox
    cusBox.getChildren().addAll(tableView, remove);

    Button closeButton = new Button("Close");
    closeButton.setOnAction(e -> borderPane.setCenter(tableView));

    // Add the close button to the VBox
    cusBox.getChildren().add(closeButton);

    // Set the VBox as the center content of the border pane
    borderPane.setCenter(cusBox);
});

        

        Button customerOpinion= new Button("Customer's Review");
        customerOpinion.setOnAction(event ->{
                List<String> revenues = new ArrayList<>();
                try (BufferedReader br = new BufferedReader(new FileReader("JAVA//rating.txt"))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        revenues.add(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Create a VBox to display the revenue values
            VBox revenueBox = new VBox();
            revenueBox.setSpacing(10);

            // Clear the existing content of the VBox
            revenueBox.getChildren().clear();

            // Add the revenue values as labels to the VBox
            for (String revenue : revenues) {
                Label revenueLabel = new Label(revenue);
                revenueBox.getChildren().add(revenueLabel);
            }
            

            Button closeButton = new Button("Close");
            closeButton.setOnAction(e -> borderPane.setCenter(tableView));

            // Add the close button to the VBox
            revenueBox.getChildren().add(closeButton);

            // Set the VBox as the center content of the border pane
            borderPane.setCenter(revenueBox);
            });
        Button exit= new Button("Exit");
        exit.setOnAction(event ->{
            System.exit(0);
        });

        // Create a border pane and add the table view and button to it
        borderPane = new BorderPane();
        borderPane.setCenter(tableView);
        HBox buttonBox = new HBox(addButton, deleteButton, updateSeatsButton, generateRevenueButton, customerOpinion, loadButton, exit);
        buttonBox.setSpacing(10);
        BorderPane.setMargin(addButton, new Insets(10, 0, 0, 0));
        borderPane.setBottom(buttonBox);

        // Create a scene and add the border pane to it
        Scene scene = new Scene(borderPane, 999, 700);

        // Set the stage title and scene
        Stage primaryStage = new Stage();
        primaryStage.setTitle("MOVIES");
        primaryStage.setScene(scene);

        // Show the stage
        primaryStage.show();

        String buttonStyle = "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 120px; -fx-min-height: 40px;";
addButton.setStyle(buttonStyle);
deleteButton.setStyle(buttonStyle);
updateSeatsButton.setStyle(buttonStyle);
generateRevenueButton.setStyle(buttonStyle);
customerOpinion.setStyle(buttonStyle);
loadButton.setStyle(buttonStyle);
exit.setStyle(buttonStyle);



String hoverStyle = "-fx-background-color: #45A049;"; // Define the hover effect style

addButton.setStyle(buttonStyle);
deleteButton.setStyle(buttonStyle);
updateSeatsButton.setStyle(buttonStyle);
generateRevenueButton.setStyle(buttonStyle);
customerOpinion.setStyle(buttonStyle);
loadButton.setStyle(buttonStyle);
exit.setStyle(buttonStyle);

// Add hover effect to the buttons
addButton.setOnMouseEntered(event -> addButton.setStyle(buttonStyle + hoverStyle));
addButton.setOnMouseExited(event -> addButton.setStyle(buttonStyle));

deleteButton.setOnMouseEntered(event -> deleteButton.setStyle(buttonStyle + hoverStyle));
deleteButton.setOnMouseExited(event -> deleteButton.setStyle(buttonStyle));

updateSeatsButton.setOnMouseEntered(event -> updateSeatsButton.setStyle(buttonStyle + hoverStyle));
updateSeatsButton.setOnMouseExited(event -> updateSeatsButton.setStyle(buttonStyle));

generateRevenueButton.setOnMouseEntered(event -> generateRevenueButton.setStyle(buttonStyle + hoverStyle));
generateRevenueButton.setOnMouseExited(event -> generateRevenueButton.setStyle(buttonStyle));

customerOpinion.setOnMouseEntered(event -> customerOpinion.setStyle(buttonStyle + hoverStyle));
customerOpinion.setOnMouseExited(event -> customerOpinion.setStyle(buttonStyle));

loadButton.setOnMouseEntered(event -> loadButton.setStyle(buttonStyle + hoverStyle));
loadButton.setOnMouseExited(event -> loadButton.setStyle(buttonStyle));

exit.setOnMouseEntered(event -> exit.setStyle(buttonStyle + hoverStyle));
exit.setOnMouseExited(event -> exit.setStyle(buttonStyle));
    }


   


    public static void main(String[] args) {
        Application.launch(args);
    }
}
