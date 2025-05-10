package model;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.sql.*;
import java.util.*;

public class Manager extends Application {

    public BorderPane borderPane = new BorderPane();
    private final TableView<List<String>> tableView = new TableView<>();
    private final ObservableList<List<String>> observableData = FXCollections.observableArrayList();
    private static final String DB_URL = "jdbc:sqlite:database/moviedb.db";

    @Override
    public void start(Stage primaryStage) {
        initializeMovieTable(); // Initializes the table
        loadMovieDataFromDB(); // Loads the movie data from the database
        setupUI(); // Sets up the UI with buttons and layout

        primaryStage.setTitle("Manager Dashboard - MOVIES");
        primaryStage.setScene(new Scene(borderPane, 999, 700));
        primaryStage.show();
    }

    private void initializeMovieTable() {
        tableView.setPrefWidth(800);
        tableView.setPrefHeight(400);

        TableColumn<List<String>, String> col1 = new TableColumn<>("Title");
        TableColumn<List<String>, String> col2 = new TableColumn<>("Genre");
        TableColumn<List<String>, String> col3 = new TableColumn<>("Duration (min)");
        TableColumn<List<String>, String> col4 = new TableColumn<>("Showtime");
        TableColumn<List<String>, String> col5 = new TableColumn<>("Price ($)");

        col1.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(0)));
        col2.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(1)));
        col3.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(2)));
        col4.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(3)));
        col5.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(4)));

        List<TableColumn<List<String>, String>> columns = new ArrayList<>();
        columns.add(col1);
        columns.add(col2);
        columns.add(col3);
        columns.add(col4);

        tableView.getColumns().addAll(columns);
        tableView.setItems(observableData);
    }

    private void loadMovieDataFromDB() {
        observableData.clear();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT title, genre, duration, showtime, price FROM movies")) {

            while (rs.next()) {
                List<String> row = List.of(
                        rs.getString("title"),
                        rs.getString("genre"),
                        String.valueOf(rs.getInt("duration")),
                        rs.getString("showtime"),
                        String.format("%.2f", rs.getDouble("price"))
                );
                observableData.add(row);
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load movie data.");
            e.printStackTrace();
        }
    }

    private void setupUI() {
        Button addButton = createStyledButton("Add Movie", event -> showAddMovieDialog());
        Button exit = createStyledButton("Exit", event -> System.exit(0));

        HBox buttonBox = new HBox(10, addButton, exit);
        BorderPane.setMargin(buttonBox, new Insets(10));

        borderPane.setCenter(tableView);
        borderPane.setBottom(buttonBox);
    }

    private Button createStyledButton(String text, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        button.setOnAction(handler);
        return button;
    }

    private void showAddMovieDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add Movie");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField();
        ComboBox<String> genreCombo = new ComboBox<>(
                FXCollections.observableArrayList("Action", "Drama", "Comedy", "Family", "Horror", "Romance"));
        TextField durationField = new TextField();
        TextField showtimeField = new TextField();
        showtimeField.setPromptText("Enter showtime (e.g., 7:00 PM)");
        TextField priceField = new TextField();

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Genre:"), 0, 1);
        grid.add(genreCombo, 1, 1);
        grid.add(new Label("Duration (min):"), 0, 2);
        grid.add(durationField, 1, 2);
        grid.add(new Label("Showtime:"), 0, 3);
        grid.add(showtimeField, 1, 3);
        grid.add(new Label("Price ($):"), 0, 4);
        grid.add(priceField, 1, 4);

        Button addMovieButton = new Button("Add Movie");
        addMovieButton.setOnAction(event -> {
            String title = titleField.getText();
            String genre = genreCombo.getValue();
            String showtime = showtimeField.getText();
            int duration;
            double price;

            try {
                duration = Integer.parseInt(durationField.getText());
                price = Double.parseDouble(priceField.getText());
            } catch (NumberFormatException e) {
                showAlert("Validation Error", "Duration and Price must be valid numbers.");
                return;
            }

            if (title.isEmpty() || genre == null || showtime == null || showtime.isBlank() || duration <= 0 || price <= 0) {
                showAlert("Validation Error", "All fields must be filled correctly.");
                return;
            }

            try (Connection conn = DriverManager.getConnection(DB_URL)) {
                String sql = "INSERT INTO movies (title, genre, duration, showtime, price) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, title);
                pstmt.setString(2, genre);
                pstmt.setInt(3, duration);
                pstmt.setString(4, showtime);
                pstmt.setDouble(5, price);
                pstmt.executeUpdate();

                showAlert("Success", "Movie added successfully.");
                dialog.close();
                loadMovieDataFromDB();

            } catch (SQLException e) {
                showAlert("Database Error", "Failed to add movie:\n" + e.getMessage());
                e.printStackTrace();
            }
        });

        grid.add(addMovieButton, 1, 5);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
