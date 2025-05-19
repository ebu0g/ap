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
                        total_seats INTEGER NOT NULL CHECK (total_seats >= 0),
                        UNIQUE(title, showtime)
                    );
                """);

        stmt.execute("""
                    CREATE TABLE IF NOT EXISTS seats (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        movie_id INTEGER NOT NULL,
                        seat_number TEXT NOT NULL,
                        is_booked INTEGER DEFAULT 0 CHECK (is_booked IN (0, 1)),
                        FOREIGN KEY (movie_id) REFERENCES movies(id),
                        UNIQUE (movie_id, seat_number)
                    );
                """);

        stmt.execute("""
                    CREATE TABLE IF NOT EXISTS review (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        movie_id INTEGER NOT NULL,
                        rating INTEGER NOT NULL,
                        comment TEXT,
                        FOREIGN KEY (movie_id) REFERENCES movies(id)
                    );
                """);

        stmt.execute("""
                    CREATE TABLE IF NOT EXISTS revenue (
                            movie_id INTEGER PRIMARY KEY,
                            total_revenue REAL NOT NULL DEFAULT 0
                    );
                """);

        stmt.execute("""
                    CREATE TABLE IF NOT EXISTS booking (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        movie_id INTEGER NOT NULL,
                        seat_number TEXT NOT NULL,
                        booking_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        UNIQUE (movie_id, seat_number),
                        FOREIGN KEY (movie_id) REFERENCES movies(id)
                    );
                """);

        stmt.execute("""
                    CREATE TABLE IF NOT EXISTS ticket (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        booking_id INTEGER NOT NULL,
                        movie_id INTEGER NOT NULL,
                        seat_number TEXT NOT NULL,
                        issue_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (booking_id) REFERENCES booking(id),
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
                    INSERT OR REPLACE INTO movies (title, genre, duration, showtime, price, total_seats)
                    VALUES
                    ('Barbie', 'Family', '100', '2025-05-11 18:00', 50.0, 50),
                    ('Lift', 'Action', '120', '2025-05-11 20:00', 40.0, 100),
                    ('Oppenhiemer', 'Drama', '100', '2025-05-12 15:00', 60.0, 70);
                """);

        try (var pstmt = conn.prepareStatement(
                "INSERT OR IGNORE INTO seats (movie_id, seat_number, is_booked) VALUES (?, ?, 0)");
                var rs = stmt.executeQuery("SELECT id, total_seats FROM movies")) {

            while (rs.next()) {
                int movieId = rs.getInt("id");
                int totalSeats = rs.getInt("total_seats");

                for (int i = 1; i <= totalSeats; i++) {
                    String seatNumber = "S" + i; // e.g., S1, S2, S3...
                    pstmt.setInt(1, movieId);
                    pstmt.setString(2, seatNumber);
                    pstmt.addBatch();
                }
            }
            pstmt.executeBatch();
        }

        try (var rs = stmt.executeQuery("SELECT id FROM movies");
                var pstmt = conn.prepareStatement(
                        "INSERT OR IGNORE INTO revenue (movie_id, total_revenue) VALUES (?, ?)")) {

            while (rs.next()) {
                int movieId = rs.getInt("id");
                pstmt.setInt(1, movieId);
                pstmt.setDouble(2, 0.0); // Start with 0 revenue
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }

        stmt.execute("""
                    INSERT OR IGNORE INTO booking (movie_id, seat_number)
                    VALUES
                    (1, 'S1'),
                    (1, 'S2');
                """);

        stmt.execute("""
                INSERT OR IGNORE INTO ticket (booking_id, movie_id, seat_number)
                VALUES
                (1, 1, 'S1'),
                (2, 1, 'S3');
                """);

        System.out.println("✅ Sample data inserted successfully.");
    }
}
