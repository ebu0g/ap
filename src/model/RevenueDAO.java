package model;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; 
import java.util.HashMap;
import java.util.Map;

public class RevenueDAO {
    public static void showRevenue() {
        try (Connection conn = DBHelper.getConnection();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery("SELECT movies.title, revenue.amount FROM revenue JOIN movies ON revenue.movie_id = movies.id")) {

            while (rs.next()) {
                System.out.println(rs.getString("title") + ": $" + rs.getDouble("amount"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Map<String, Double> getRevenueSummary() {
        Map<String, Double> summary = new HashMap<>();

        String sql = "SELECT m.title, SUM(r.amount) as total " +
                 "FROM revenue r JOIN movies m ON r.movie_id = m.id " +
                 "GROUP BY m.title";

        try (Connection conn = DBHelper.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String title = rs.getString("title");
                double total = rs.getDouble("total");
                summary.put(title, total);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return summary;
    }
}