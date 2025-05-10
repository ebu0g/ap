package model;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Rating {

    public Rating() {
        Label ratingLabel = new Label("Rating (1-5):"); // Adjusted to fit the 1-5 range for ratings
        TextField ratingTextField = new TextField();
        Label reviewLabel = new Label("Review:");
        TextArea reviewTextArea = new TextArea();
        Button submitButton = new Button("Submit");

        submitButton.setOnAction(e -> {
            double rating = Double.parseDouble(ratingTextField.getText());
            String review = reviewTextArea.getText();
            int userId = 1; // This should be dynamically fetched based on logged-in user
            int movieId = 1; // This should be dynamically fetched based on selected movie
            saveRatingAndReviewToDatabase(userId, movieId, rating, review);
            System.exit(0);
        });

        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(20));
        vbox.getChildren().addAll(ratingLabel, ratingTextField, reviewLabel, reviewTextArea, submitButton);

        Stage primaryStage = new Stage();
        Scene scene = new Scene(vbox, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setTitle("Rating and Review");
        Image icon = new Image("Logo.jpeg");
        primaryStage.getIcons().add(icon);
    }

    // Method to save rating and review to the database
    private void saveRatingAndReviewToDatabase(int userId, int movieId, double rating, String review) {
        try (Connection conn = DBHelper.getConnection()) {
            String query = "INSERT INTO ratings (user_id, movie_id, score) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, movieId);
                stmt.setDouble(3, rating);
                stmt.executeUpdate();
            }

            String reviewQuery = "INSERT INTO reviews (user_id, movie_id, review) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(reviewQuery)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, movieId);
                stmt.setString(3, review);
                stmt.executeUpdate();
            }

            System.out.println("Rating and Review saved successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
