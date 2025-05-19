package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    public static boolean authenticate(String name, String password) {
        try (Connection conn = DBHelper.getConnection();
             var stmt = conn.prepareStatement("SELECT * FROM users WHERE name = ? AND password = ?")) {
            stmt.setString(1, name);
            stmt.setString(2, password);
            var rs = stmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void showAllCustomers() {
        String query = "SELECT id, username, email FROM users WHERE role = 'customer'";
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database/moviedb.db");
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery()) {

            System.out.println("=== Registered Customers ===");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") +
                    ", Username: " + rs.getString("username") +
                    ", Email: " + rs.getString("email"));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching customer list: " + e.getMessage());
        }
    }
}
