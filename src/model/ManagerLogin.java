package model;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ManagerLogin extends GridPane {

    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton;

    public ManagerLogin() {
        // Initialize UI elements
        Label welcomeLabel = new Label("Welcome to CINEBOOK!");
        welcomeLabel.setFont(new Font("Arial", 20));
        welcomeLabel.setTextFill(Color.WHITE);

        Label usernameLabel = new Label("Username:");
        usernameField = new TextField();
        usernameLabel.setTextFill(Color.BEIGE);

        Label passwordLabel = new Label("Password:");
        passwordField = new PasswordField();
        passwordLabel.setTextFill(Color.BEIGE);

        loginButton = new Button("Login");
        loginButton.setOnAction(event -> checkCredentials());
        loginButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");

        // Add UI elements to the grid
        add(welcomeLabel, 0, 0, 2, 1);
        add(usernameLabel, 0, 1);
        add(usernameField, 1, 1);
        add(passwordLabel, 0, 2);
        add(passwordField, 1, 2);
        add(loginButton, 1, 3);

        // Set the grid's constraints
        setHgap(10);
        setVgap(10);
        setPadding(new Insets(25));
        setStyle("-fx-background-color: #333333;");
        setAlignment(Pos.CENTER);
    }

    private void checkCredentials() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try (java.sql.Connection conn = java.sql.DriverManager.getConnection("jdbc:sqlite:database/moviedb.db");
            java.sql.PreparedStatement stmt = conn.prepareStatement("SELECT role FROM users WHERE username = ? AND password = ?")) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            java.sql.ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                if ("admin".equalsIgnoreCase(role)) {
                    Stage currentStage = (Stage) loginButton.getScene().getWindow();
                    Manager managerApp = new Manager();
                    managerApp.start(currentStage); // ✅ FIXED: call start(Stage)
                } else {
                    showAlert("Access Denied", "You are not authorized to access the manager dashboard.");
                }
            } else {
                showAlert("Login Failed", "Incorrect username or password.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Something went wrong while checking credentials.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


    public void showCustomerList(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/managerDashboard.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setTitle("Manager Dashboard");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("[ERROR] Could not load Manager Dashboard.");
        }
    }
}
