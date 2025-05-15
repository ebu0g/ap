package model;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;


import java.sql.*;
import java.util.List;

public class Manager extends Application {
    private BorderPane borderPane;
    private TableView<Customer> customerTable;
    private Connection connection;

    @Override
    public void start(Stage primaryStage) {
        connection = DBHelper.connect();

        // Buttons
        Button addButton = new Button("Add Movies");
        Button deleteButton = new Button("Delete Movies");
        Button showSummaryBtn = new Button("Show Seat Summary");
        Button generateRevenueBtn = new Button("Generate Revenue");
        Button viewReviewsBtn = new Button("View Reviews");
        Button loadButton = new Button("Customer Management");
        Button exit = new Button("Exit");

        // Button actions
        addButton.setOnAction(e -> showAddMovieForm(primaryStage));
        deleteButton.setOnAction(e -> showDeleteMovieForm());
        showSummaryBtn.setOnAction(e -> showSeatSummary((Stage) borderPane.getScene().getWindow()));
        generateRevenueBtn.setOnAction(e -> new RevenueView().showRevenueWindow());
        viewReviewsBtn.setOnAction(e -> showReviewList()); 
        loadButton.setOnAction(e -> showCustomerList());
        exit.setOnAction(e -> primaryStage.close());


        VBox menuBox = new VBox(10);
        menuBox.setPadding(new Insets(20));
        menuBox.getChildren().addAll(addButton, deleteButton, showSummaryBtn, generateRevenueBtn, viewReviewsBtn, loadButton, exit);

        borderPane = new BorderPane();
        borderPane.setCenter(menuBox);
        BorderPane.setMargin(menuBox, new Insets(20));

        Scene scene = new Scene(borderPane, 999, 700);
        primaryStage.setTitle("Manager Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showDashboard() {
        VBox menuBox = new VBox(10);
        menuBox.setPadding(new Insets(20));

        Button addButton = new Button("Add Movies");
        Button deleteButton = new Button("Delete Movies");
        Button showSummaryBtn = new Button("Show Seat Summary");
        Button generateRevenueButton = new Button("Generated Revenue");
        Button viewReviewsBtn = new Button("View Reviews");
        Button loadButton = new Button("Customer Management");
        Button exit = new Button("Exit");

        addButton.setOnAction(e -> showAddMovieForm((Stage) borderPane.getScene().getWindow()));
        deleteButton.setOnAction(e -> showDeleteMovieForm());
        showSummaryBtn.setOnAction(e -> showSeatSummary((Stage) borderPane.getScene().getWindow()));
        generateRevenueButton.setOnAction(e -> showRevenue());
        viewReviewsBtn.setOnAction(e -> showReviewList()); 
        loadButton.setOnAction(e -> showCustomerList());
        exit.setOnAction(e -> ((Stage) borderPane.getScene().getWindow()).close());
        menuBox.getChildren().addAll(addButton, deleteButton, showSummaryBtn, generateRevenueButton, viewReviewsBtn, loadButton, exit);
        borderPane.setCenter(menuBox);
    }

    private void showAddMovieForm(Stage stage) {
        TextField titleField = new TextField();
        titleField.setPromptText("Title");

        // Genre choice box with a default prompt label
        ChoiceBox<String> genreChoiceBox = new ChoiceBox<>();
        genreChoiceBox.getItems().addAll("Action", "Comedy", "Drama", "Horror", "Romance", "Sci-Fi");
    
        ComboBox<String> genreComboBox = new ComboBox<>();
        genreComboBox.setPromptText("Select Genre");

        genreChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // If the user selects an option, replace the placeholder
            if ("Genre".equals(newValue)) {
                genreChoiceBox.setValue(null); // Optional: clear placeholder if needed
            }
        });

        TextField priceField = new TextField();
        priceField.setPromptText("Price");

        TextField durationField = new TextField();
        durationField.setPromptText("Duration (minutes)");

        TextField showtimeField = new TextField();
        showtimeField.setPromptText("Showtime (yyyy-MM-dd HH:mm)");

        // ✅ New seat count field
        TextField seatCountField = new TextField();
        seatCountField.setPromptText("Total Seats");

        Button submit = new Button("Add Movie");
        submit.setOnAction(e -> {
        String title = titleField.getText();
        String genre = genreChoiceBox.getValue();
        String price = priceField.getText();
        String duration = durationField.getText();
        String showtime = showtimeField.getText();
        String seatCount = seatCountField.getText();

        addMovieToDatabase(title, genre, price, duration, showtime, seatCount);
    });

        Button back = new Button("Back to Dashboard");
        back.setOnAction(e -> showDashboard());

        VBox form = new VBox(10, new Label("Add Movie"), titleField, genreChoiceBox, priceField, 
                        durationField, showtimeField, seatCountField, submit, back);
        form.setPadding(new Insets(20));
        borderPane.setCenter(form);
    }

    private void addMovieToDatabase(String title, String genre, String priceText, String durationText, String showtimeText, String seatCountText) {
    if (title.isEmpty() || genre == null || priceText.isEmpty() || durationText.isEmpty() || showtimeText.isEmpty() || seatCountText.isEmpty()) {
        showAlert(Alert.AlertType.ERROR, "Missing fields", "All fields must be filled.");
        return;
    }

    try {
        double price = Double.parseDouble(priceText);
        int duration = Integer.parseInt(durationText);
        int totalSeats = Integer.parseInt(seatCountText);

        // Verify connection before proceeding
        if (connection == null || connection.isClosed()) {
            showAlert(Alert.AlertType.ERROR, "Database Connection Error", "Failed to connect to the database.");
            return;
        }

        // Check if movie already exists with the same title and showtime
        String checkMovieSql = "SELECT COUNT(*) FROM movies WHERE title = ? AND showtime = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkMovieSql)) {
            checkStmt.setString(1, title);
            checkStmt.setString(2, showtimeText);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                showAlert(Alert.AlertType.WARNING, "Duplicate Movie", "A movie with the same title and showtime already exists.");
                return;  // Return early if the movie already exists
            }
        }

         // Insert movie record without specifying the ID (let the DB handle it)
        String insertMovieSql = "INSERT INTO movies (title, genre, duration, showtime, price, total_seats) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(insertMovieSql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, title);
            stmt.setString(2, genre);
            stmt.setInt(3, duration);
            stmt.setString(4, showtimeText);
            stmt.setDouble(5, price);
            stmt.setInt(6, totalSeats);
            stmt.executeUpdate();


             // Get the movie ID for seat insertion
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int movieId = generatedKeys.getInt(1); // The generated ID of the inserted movie
                insertSeatsForMovie(movieId, totalSeats); // Insert seats after movie
                showAlert(Alert.AlertType.INFORMATION, "Success", "Movie and seats added successfully.");
            }
        }
    } catch (NumberFormatException e) {
        showAlert(Alert.AlertType.ERROR, "Invalid input", "Price, duration, and seat count must be numbers.");
    } catch (SQLException e) {
        // Handle unique constraint failure for the movie
        if (e.getMessage().contains("UNIQUE constraint failed")) {
            showAlert(Alert.AlertType.WARNING, "Duplicate Movie", "A movie with the same title and showtime already exists.");
        } else {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add movie.");
        }
    }
}

    private void insertSeatsForMovie(int movieId, int totalSeats) {
        String sql = "INSERT INTO seats (movie_id, seat_number, is_booked) VALUES (?, ?, 0)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (int i = 1; i <= totalSeats; i++) {
                stmt.setInt(1, movieId);
                stmt.setString(2, "S" + i); // Seat numbers like S1, S2...
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            // Print the detailed exception to debug
            System.err.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error adding seat", "Error adding seat. Make sure the database is connected.");
        }
    }


    private void showDeleteMovieForm() {
        TextField idField = new TextField();
        idField.setPromptText("Movie ID to delete");

        Button deleteBtn = new Button("Delete");
        deleteBtn.setOnAction(e -> {
            try {
                int id = Integer.parseInt(idField.getText());
                String sql = "DELETE FROM movies WHERE id = ?";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setInt(1, id);
                    int rows = stmt.executeUpdate();
                    if (rows > 0) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Movie deleted.");
                    } else {
                        showAlert(Alert.AlertType.WARNING, "Not Found", "No movie found with ID " + id);
                    }
                }
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid ID.");
            }
        });

        Button back = new Button("Back to Dashboard");
        back.setOnAction(e -> showDashboard());

        VBox form = new VBox(10, new Label("Delete Movie"), idField, deleteBtn, back);
        form.setPadding(new Insets(20));
        borderPane.setCenter(form);
    }

    
    
    private void showSeatSummary(Stage stage) {
    String sql = """
        SELECT m.id AS movie_id, m.title,
               COUNT(DISTINCT s.seat_number) AS total_seats,
               COUNT(DISTINCT t.seat_number) AS booked_seats
        FROM movies m
        LEFT JOIN seats s ON m.id = s.movie_id
        LEFT JOIN booking b ON m.id = b.movie_id
        LEFT JOIN ticket t ON b.id = t.booking_id
        GROUP BY m.id, m.title
    """;

    try (PreparedStatement stmt = connection.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        TableView<ObservableList<String>> table = new TableView<>();

        String[] headers = { "Movie ID", "Title", "Total Seats", "Booked Seats", "Available Seats" };
        for (int i = 0; i < headers.length; i++) {
            final int colIndex = i;
            TableColumn<ObservableList<String>, String> col = new TableColumn<>(headers[i]);
            col.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(colIndex)));
            table.getColumns().add(col);
        }

        while (rs.next()) {
            int total = rs.getInt("total_seats");
            int booked = rs.getInt("booked_seats");
            int available = total - booked;
            ObservableList<String> row = FXCollections.observableArrayList(
                String.valueOf(rs.getInt("movie_id")),
                rs.getString("title"),
                String.valueOf(total),
                String.valueOf(booked),
                String.valueOf(available)
            );
            table.getItems().add(row);
        }

        
        stage.setTitle("Seat Summary");
        stage.setScene(new Scene(new VBox(10, table), 600, 400));
        stage.show();

    } catch (SQLException e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Database Error", "Could not load seat summary.");
    }
}


    private void showRevenue() {
        MovieDAO movieDAO = new MovieDAO();
        RevenueDAO revenueDAO = new RevenueDAO();

        List<Movie> movies = movieDAO.getAllMovies();
        double totalRevenue = 0.0;

        for (Movie movie : movies) {
            double revenue = revenueDAO.calculateRevenueForMovie(movie.getId());
            totalRevenue += revenue;
            revenueDAO.insertRevenue(movie.getId(), revenue);
        }

        showAlert(Alert.AlertType.INFORMATION, "Total Revenue", "Total revenue from all booked tickets: $" + totalRevenue);
        new RevenueView().showRevenueWindow();
    }

    private void showCustomerList() {
    customerTable = new TableView<>();
    customerTable.setPrefHeight(400);

    TableColumn<Customer, Integer> idCol = new TableColumn<>("ID");
    idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
    idCol.setPrefWidth(50);

    TableColumn<Customer, String> nameCol = new TableColumn<>("Name");
    nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
    nameCol.setPrefWidth(150);

    TableColumn<Customer, String> emailCol = new TableColumn<>("Email");
    emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
    emailCol.setPrefWidth(200);

    TableColumn<Customer, String> usernameCol = new TableColumn<>("Username");
    usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
    usernameCol.setPrefWidth(150);

        customerTable.getColumns().add(idCol);
        customerTable.getColumns().add(nameCol);
        customerTable.getColumns().add(emailCol);
        customerTable.getColumns().add(usernameCol);
    
    ObservableList<Customer> customers = loadCustomers();
    if (customers != null) {
        customerTable.setItems(customers);
    }

    // Sort by ID by default
    customerTable.getSortOrder().add(idCol);
    idCol.setSortType(TableColumn.SortType.ASCENDING);

    Button back = new Button("Back to Dashboard");
    back.setOnAction(e -> showDashboard());

    Label title = new Label("Customer List");
    title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

    VBox view = new VBox(15, title, customerTable, back);
    view.setPadding(new Insets(20));
    view.setAlignment(Pos.TOP_CENTER);
    VBox.setVgrow(customerTable, Priority.ALWAYS);

    borderPane.setCenter(view);
}

    private ObservableList<Customer> loadCustomers() {
        ObservableList<Customer> list = FXCollections.observableArrayList();
        String sql = "SELECT id, username AS name, email, username FROM users WHERE role = 'customer'";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Customer(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("username"),
                        ""  // skip password
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

   private void showReviewList() {
    TableView<Review> reviewTable = new TableView<>();

    TableColumn<Review, Integer> idCol = new TableColumn<>("Review ID");
    idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

    TableColumn<Review, String> movieCol = new TableColumn<>("Movie");
    movieCol.setCellValueFactory(new PropertyValueFactory<>("movieTitle"));

    TableColumn<Review, String> reviewCol = new TableColumn<>("Review");
    reviewCol.setCellValueFactory(new PropertyValueFactory<>("reviewText"));

    reviewTable.getColumns().add(idCol);
    reviewTable.getColumns().add(movieCol);
    reviewTable.getColumns().add(reviewCol);

    ObservableList<Review> reviews = loadReviews();
    reviewTable.setItems(reviews);

    Button back = new Button("Back to Dashboard");
    back.setOnAction(e -> showDashboard());

    VBox layout = new VBox(10, new Label("Movie Reviews"), reviewTable, back);
    layout.setPadding(new Insets(20));

    borderPane.setCenter(layout);
}


    private ObservableList<Review> loadReviews() {
    ObservableList<Review> reviews = FXCollections.observableArrayList();
    String sql = "SELECT r.id, m.title AS movie, r.comment " +
                 "FROM review r " +
                 "JOIN movies m ON r.movie_id = m.id";

    try (Statement stmt = connection.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        while (rs.next()) {
            int id = rs.getInt("id");
            String movieTitle = rs.getString("movie");
            String reviewText = rs.getString("comment");

            reviews.add(new Review(id, movieTitle, reviewText));
        }
    } catch (SQLException e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Error", "Could not load reviews.");
    }
    return reviews;
}

    private void showAlert(Alert.AlertType alertType, String title, String message) {
    Alert alert = new Alert(alertType);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
}



    public static void main(String[] args) {
        launch(args);
    }

    public static class Customer {
        private final int id;
        private final String name;
        private final String email;
        private final String username;
        private final String password; // optional, currently unused

        public Customer(int id, String name, String email, String username, String password) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.username = username;
            this.password = password;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }

    // Review model class
    public class Review {
    private int id;
    private String movieTitle;
    private String reviewText;

    // Add constructor with these 3 parameters
    public Review(int id, String movieTitle, String reviewText) {
        this.id = id;
        this.movieTitle = movieTitle;
        this.reviewText = reviewText;
    }

    // getters and setters here
    public int getId() { return id; }
    public String getMovieTitle() { return movieTitle; }
    public String getReviewText() { return reviewText; }

    public void setId(int id) { this.id = id; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }
}

}