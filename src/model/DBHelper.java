package model;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBHelper {
    
    private static final String DB_URL = "jdbc:sqlite:database/cinebook.db";
    private static Connection conn = null;

    // Modified connect() method to return a Connection
    public static Connection connect() {
        try {
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(DB_URL);
                System.out.println("Connected to SQLite database.");
            }
        } catch (SQLException e) {
            System.out.println("Failed to connect: " + e.getMessage());
        }
        return conn;
    }

    // Method to validate manager credentials
    public static boolean validateManagerCredentials(String username, String password) {
        String query = "SELECT * FROM managers WHERE username = ? AND password = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Returns true if a matching record is found
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to retrieve the current connection
    public static Connection getConnection() {
        return conn;
    }

    // Method to close the connection
    public static void close() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
