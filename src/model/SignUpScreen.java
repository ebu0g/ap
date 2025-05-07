package model;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

public class SignUpScreen extends GridPane {

    private TextField usernameField;
    private TextField emailField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private Button signUpButton;

    public SignUpScreen() {
        // Initialize UI elements
        Label welcomeLabel = new Label("Sign Up for CINEBOOK!");
        welcomeLabel.setFont(new Font("Arial", 20));

        Label usernameLabel = new Label("Username:");
        usernameField = new TextField();
        usernameField.setPromptText("Enter your username");

        Label emailLabel = new Label("Email:");
        emailField = new TextField();
        emailField.setPromptText("Enter your email");

        Label passwordLabel = new Label("Password:");
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");

        Label confirmPasswordLabel = new Label("Confirm Password:");
        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm your password");

        signUpButton = new Button("Sign Up");

        // Set the constraints for the form
        setHgap(10);
        setVgap(10);
        setPadding(new Insets(25, 25, 25, 25));
        setStyle("-fx-background-color: #6DA5C0;");

        // Add the form to the grid
        add(welcomeLabel, 0,  0, 2, 1); 
        add(usernameLabel, 0, 1); 
        add(usernameField, 1, 1); 
        add(emailLabel, 0, 2);
        add(emailField, 1, 2); 
        add(passwordLabel, 0, 3);
        add(passwordField, 1, 3); 
        add(confirmPasswordLabel, 0, 4);
        add(confirmPasswordField, 1, 4); 
        add(signUpButton, 0, 5, 2, 1);

        setHgap(10); setVgap(10); setPadding(new Insets(25, 25, 25, 25));
        signUpButton.setOnAction(event -> {
            String username = usernameField.getText();
            String email = emailField.getText();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();
        
            // Validate the user's input
            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Missing Information");
                alert.setContentText("Please fill in all fields.");
                alert.showAndWait();
                return;
            }
        
            if (!password.equals(confirmPassword)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Passwords Do Not Match");
                alert.setContentText("Please ensure that your passwords match.");
                alert.showAndWait();
                return;
            }
        
            saveUserToDatabase(username, email, password);
        
            // Clear the input fields
            usernameField.clear();
            emailField.clear();
            passwordField.clear();
            confirmPasswordField.clear();
        
            // Show a success message
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Success");
            successAlert.setHeaderText("Account Created");
            successAlert.setContentText("Your account has been successfully created. Please sign in.");
            successAlert.showAndWait();
        });}
        private void saveUserToDatabase(String username, String email, String password) {
            String sql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";

            try (Connection conn = DBHelper.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, username);
                pstmt.setString(2, email);
                pstmt.setString(3, password);
                pstmt.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Database Error");
                alert.setHeaderText("Failed to Sign Up");
                alert.setContentText("Error while saving user to database.");
                alert.showAndWait();
            }
        }

    }
        
