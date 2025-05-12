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


public class Rating {

    public Rating() {
        Label ratingLabel = new Label("Rating (1-5):");
        TextField ratingTextField = new TextField();
        Label reviewLabel = new Label("Review:");
        TextArea reviewTextArea = new TextArea();
        Button submitButton = new Button("Submit");

        submitButton.setOnAction(e -> {
            double rating = Double.parseDouble(ratingTextField.getText());
            String review = reviewTextArea.getText();
            int userId = 1; // This should be dynamically fetched based on logged-in user
            int movieId = 1; // This should be dynamically fetched based on selected movie

            // Save rating using RatingDAO
            RatingDAO.rateMovie(userId, movieId, rating);

            // Save review using ReviewDAO
            ReviewDAO.saveReview(userId, movieId, review);

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
}
