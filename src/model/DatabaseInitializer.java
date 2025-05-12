package model;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class DatabaseInitializer {

    private static final String DB_URL = "jdbc:sqlite:database/moviedb.db";

    public static void main(String[] args) {

        // Ensure 'database' directory exists
        File dbDir = new File("database");
        if (!dbDir.exists()) {
            dbDir.mkdir();
        }

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            if (conn != null) {
                createTables(conn);
                insertSampleData(conn);
            }
        } catch (SQLException e) {
            System.err.println("Database initialization failed:");
            e.printStackTrace();
        }
    }

    private static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();

        stmt.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        username TEXT UNIQUE NOT NULL,
                        email TEXT NOT NULL,
                        password TEXT NOT NULL,
                        role TEXT CHECK(role IN ('customer', 'admin')) NOT NULL
                    );
                """);

        stmt.execute("""
                    CREATE TABLE IF NOT EXISTS movies (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        title TEXT NOT NULL,
                        genre TEXT,
                        duration INTEGER NOT NULL,
                        showtime TEXT NOT NULL,
                        price REAL NOT NULL,
                        total_seats INTEGER NOT NULL
                    );
                """);

        stmt.execute("""
                    CREATE TABLE IF NOT EXISTS seats (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        movie_id INTEGER NOT NULL,
                        seat_number TEXT NOT NULL,
                        is_booked BOOLEAN DEFAULT 0,
                        UNIQUE(movie_id, seat_number),
                        FOREIGN KEY (movie_id) REFERENCES movies(id)
                    );
                """);

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

        stmt.execute("""
                    CREATE TABLE IF NOT EXISTS reviews (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        user_id INTEGER NOT NULL,
                        movie_id INTEGER NOT NULL,
                        review TEXT NOT NULL,
                        FOREIGN KEY (user_id) REFERENCES users(id),
                        FOREIGN KEY (movie_id) REFERENCES movies(id)
                    );
                """);

        stmt.execute("""
                    CREATE TABLE IF NOT EXISTS revenue (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        movie_id INTEGER NOT NULL,
                        amount REAL DEFAULT 0.0,
                        FOREIGN KEY (movie_id) REFERENCES movies(id)
                    );
                """);

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

        stmt.execute("""
                    CREATE TABLE IF NOT EXISTS ticket_purchases (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        ticket_number TEXT NOT NULL,
                        movie_id INTEGER NOT NULL,
                        number_of_seats INTEGER,
                        total_price REAL,
                        FOREIGN KEY (movie_id) REFERENCES movies(id)
                    );
                """);

        System.out.println("✅ Tables created successfully.");
    }

    private static void insertSampleData(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();

        stmt.execute("""
                    INSERT OR IGNORE INTO users (username, email, password, role)
                    VALUES
                    ('selam01', 'selam@example.com', '1234', 'customer'),
                    ('none01', 'none@example.com', '0987', 'customer'),
                    ('eb', 'gebawak@gmail.com', '12345678', 'admin');
                """);

        stmt.execute("""
                    INSERT OR REPLACE INTO movies (id, title, genre, duration, showtime, price, total_seats)
                    VALUES
                    (1, 'Barbie', 'Family', '100', '2025-05-11 18:00', 50.0, 50),
                    (2, 'Lift', 'Action', '120', '2025-05-11 20:00', 40.0, 100),
                    (3, 'Oppenhiemer', 'Drama', '100', '2025-05-12 15:00', 60.0, 70);
                """);


        stmt.execute("""
                    INSERT OR IGNORE INTO seats (movie_id, seat_number, is_booked)
                    VALUES
                    (1, 'A1', 1),
                    (1, 'A2', 0),
                    (2, 'B1', 1);
                """);

        stmt.execute("""
                    INSERT OR IGNORE INTO ratings (user_id, movie_id, score)
                    VALUES
                    (1, 1, 5),
                    (1, 2, 4),
                    (2, 3, 5);
                """);

        stmt.execute("""
                    INSERT OR IGNORE INTO revenue (movie_id, amount)
                    VALUES
                    (1, 500.0),
                    (2, 900.0);
                """);

        stmt.execute("""
                    INSERT OR IGNORE INTO bookings (user_id, seat_id)
                    VALUES
                    (1, 1),
                    (2, 3);
                """);

        stmt.execute("""
                    INSERT OR IGNORE INTO ticket_purchases (ticket_number, movie_id, number_of_seats, total_price)
                    VALUES
                    ('TCKT001', 1, 2, 100.0);
            """);

        System.out.println("✅ Sample data inserted successfully.");
    }
}