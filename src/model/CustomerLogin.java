package model;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.sql.*;

public class CustomerLogin extends GridPane {

    private TextField usernameField;
    private PasswordField passwordField;
    private Button signInButton;
    private Button signUpButton;

    public CustomerLogin() {
        // Initialize UI elements
        Label welcomeLabel = new Label("Welcome to CINEBOOK!");
        welcomeLabel.setFont(new Font("Arial", 20));

        Label usernameLabel = new Label("Username:");
        usernameField = new TextField();
        usernameField.setPromptText("Enter your username");

        Label passwordLabel = new Label("Password:");
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");

        signInButton = new Button("Sign In");
        signUpButton = new Button("Sign Up");

        // Set the constraints for the form
        setHgap(10);
        setVgap(10);
        setPadding(new Insets(25, 25, 25, 25));
        setStyle("-fx-background-color: #AE445A;");

        // Add the form to the grid
        add(welcomeLabel, 0, 0, 2, 1);
        add(usernameLabel, 0, 1);
        add(usernameField, 1, 1);
        add(passwordLabel, 0, 2);
        add(passwordField, 1, 2);
        add(signInButton, 0, 3);
        add(signUpButton, 1, 3);

        // Set the action for the sign up button
        signUpButton.setOnAction(event -> {
            SignUpScreen signUpScreen = new SignUpScreen();
            Stage signUpStage = new Stage();
            signUpStage.setTitle("Sign Up");
            signUpStage.setScene(new Scene(signUpScreen, 500, 500));
            signUpStage.show();
        });

        // Set the action for the sign in button
        signInButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            // Check if the username and password are valid
            if (isValidCredentials(username, password)) {
                // Open the customer screen
                Stage currentStage = (Stage) signInButton.getScene().getWindow();

                Customer customerScreen = new Customer();
                StackPane customerScreenPane = new StackPane();
                customerScreenPane.getChildren().add(customerScreen);
                customerScreenPane.setPadding(new Insets(10));

                Scene customerScene = new Scene(customerScreenPane, 500, 500);
                currentStage.setScene(customerScene);
                currentStage.setTitle("Customer Screen");

            } else {
                // Show an error message if the credentials are invalid
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("Invalid credentials");
                errorAlert.setContentText("Please enter a valid username and password.");
                errorAlert.showAndWait();
            }
        });
    }

    private boolean isValidCredentials(String username, String password) {
        String url = "jdbc:sqlite:database/moviedb.db";
        String sql = "SELECT username FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            return rs.next(); // Returns true if a record is found

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
