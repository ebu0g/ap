package model;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    public static void main(String[] args) {
        DBHelper.connect();
        String url = "jdbc:sqlite:moviedb.db";

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                
                Statement stmt = conn.createStatement();

                // Create users table
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        password TEXT NOT NULL,
                        role TEXT CHECK(role IN ('customer', 'admin')) NOT NULL
                    );
                """);

                // Create movies table
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS movies (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        title TEXT NOT NULL,
                        genre TEXT,
                        duration INTEGER,
                        rating REAL CHECK(rating >= 0 AND rating <= 10)
                    );
                """);

                // Create seats table
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS seats (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        movie_id INTEGER NOT NULL,
                        seat_number TEXT NOT NULL,
                        is_booked BOOLEAN DEFAULT 0,
                        FOREIGN KEY (movie_id) REFERENCES movies(id)
                    );
                """);

                // Create ratings table
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS ratings (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        user_id INTEGER NOT NULL,
                        movie_id INTEGER NOT NULL,
                        score INTEGER CHECK(score >= 1 AND score <= 5),
                        FOREIGN KEY (user_id) REFERENCES users(id),
                        FOREIGN KEY (movie_id) REFERENCES movies(id)
                    );
                """);

                // Create revenue table
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS revenue (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        movie_id INTEGER NOT NULL,
                        amount REAL DEFAULT 0.0,
                        FOREIGN KEY (movie_id) REFERENCES movies(id)
                    );
                """);

                // Create bookings table
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS bookings (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        user_id INTEGER NOT NULL,
                        seat_id INTEGER NOT NULL,
                        timestamp TEXT DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (user_id) REFERENCES users(id),
                        FOREIGN KEY (seat_id) REFERENCES seats(id)
                    );
                """);

                System.out.println("Tables created. Inserting sample data...");

                // Sample users
                stmt.execute("INSERT INTO users (name, password, role) VALUES ('selam', '1234', 'customer');");
                stmt.execute("INSERT INTO users (name, password, role) VALUES ('none', '0987', 'customer');");

                // Sample movies
                stmt.execute("INSERT INTO movies (title, genre, duration, rating) VALUES ('Barbie', 'Family', 120, 7.8);");
                stmt.execute("INSERT INTO movies (title, genre, duration, rating) VALUES ('Lift', 'Action', 100, 5.0);");
                stmt.execute("INSERT INTO movies (title, genre, duration, rating) VALUES ('Oppenhiemer', 'Drama', 100, 9.0);");

                // Sample seats
                stmt.execute("INSERT INTO seats (movie_id, seat_number, is_booked) VALUES (1, 'A1', 1);");
                stmt.execute("INSERT INTO seats (movie_id, seat_number, is_booked) VALUES (1, 'A2', 0);");
                stmt.execute("INSERT INTO seats (movie_id, seat_number, is_booked) VALUES (2, 'B1', 1);");

                // Sample ratings
                stmt.execute("INSERT INTO ratings (user_id, movie_id, score) VALUES (1, 1, 5.0);");
                stmt.execute("INSERT INTO ratings (user_id, movie_id, score) VALUES (1, 2, 4.0);");
                stmt.execute("INSERT INTO ratings (user_id, movie_id, score) VALUES (2, 3, 9.0);");

                // Sample revenue
                stmt.execute("INSERT INTO revenue (movie_id, amount) VALUES (1, 500.0);");
                stmt.execute("INSERT INTO revenue (movie_id, amount) VALUES (2, 900.0);");

                // Sample bookings
                stmt.execute("INSERT INTO bookings (user_id, seat_id) VALUES (1, 1);");
                stmt.execute("INSERT INTO bookings (user_id, seat_id) VALUES (2, 3);");

                System.out.println("Sample data inserted.");

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
