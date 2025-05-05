import java.sql.Connection;

public class MovieDAO {
    public static void listMovies() {
        try (Connection conn = DBHelper.getConnection();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery("SELECT * FROM movies")) {
            while (rs.next()) {
                System.out.println(rs.getInt("id") + ": " + rs.getString("title") +
                        " | Genre: " + rs.getString("genre") +
                        " | Duration: " + rs.getInt("duration") + " mins" +
                        " | Rating: " + rs.getDouble("rating"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}