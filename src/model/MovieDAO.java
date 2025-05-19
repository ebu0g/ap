package model;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public List<Movie> getAllMovies() {
    List<Movie> movies = new ArrayList<>();
    String sql = "SELECT id, title, genre, duration FROM movies";

    try (Connection conn = DBHelper.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

        while (rs.next()) {
            int id = rs.getInt("id");
            String title = rs.getString("title");
            String genre = rs.getString("genre");
            int duration = rs.getInt("duration");
            double price = rs.getDouble("price"); 


            Movie movie = new Movie(id, title, genre, duration, price);
            movies.add(movie);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return movies;
    }

}
