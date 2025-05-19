package model;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet; 
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
    String password = passwordField.getText();
    String email = emailField.getText();

    if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
        showAlert(Alert.AlertType.ERROR, "Error", "Please fill all fields.");
        return;
    }

    String confirmPassword = confirmPasswordField.getText();

    if (!password.equals(confirmPassword)) {
        showAlert(Alert.AlertType.ERROR, "Error", "Passwords do not match.");
        return;
    }


    try (Connection conn = DBHelper.getConnection()) {
        // Check for existing username
        String checkUsernameSQL = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (PreparedStatement checkUsernameStmt = conn.prepareStatement(checkUsernameSQL)) {
            checkUsernameStmt.setString(1, username);
            ResultSet rs = checkUsernameStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                showAlert(Alert.AlertType.ERROR, "Username Exists", "Username already exists. Please choose a different one.");
                return;
            }
        }

        // Check for existing email
        String checkEmailSQL = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (PreparedStatement checkEmailStmt = conn.prepareStatement(checkEmailSQL)) {
            checkEmailStmt.setString(1, email);
            ResultSet rs = checkEmailStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                showAlert(Alert.AlertType.ERROR, "Email Exists", "Email already exists. Please use a different one.");
                return;
            }
        }

        // If both unique, insert user
        String insertSQL = "INSERT INTO users(username, password, email, role) VALUES(?, ?, ?, ?)";
        try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {
            insertStmt.setString(1, username);
            insertStmt.setString(2, password);
            insertStmt.setString(3, email);
            insertStmt.setString(4, "customer");

            insertStmt.executeUpdate();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Registration successful!");
        }

    } catch (SQLException e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Database Error", "Could not complete registration.");
    }
});
    }
        
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
        
