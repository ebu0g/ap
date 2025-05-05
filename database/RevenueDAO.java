import java.sql.Connection;

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
}