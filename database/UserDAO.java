import java.sql.Connection;

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
}