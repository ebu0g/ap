# CINEBOOK - Movie Ticketing System Project Using Java and JavaFX

## Overview

CINEBOOK is a Java-based Movie Ticketing System using JavaFX for an interactive and user-friendly interface, with an SQLite database backend for reliable data management. This system offers a smooth and convenient ticket booking experience for customers and efficient management tools for theater managers.

Customers can browse available movies, select seats with different luxury levels, and purchase tickets within their preferred timeframe. Managers can easily handle movie listings, seat allocations, revenue tracking, and customer management, all from a centralized application.

## Features

### Customer Features

- Register a new account or log in with existing credentials.
- Browse movies and available seats based on preferred times.
- Book tickets with seat selection for regular or VIP seats.
- View booking history and manage their profiles.

### Manager Features

- Manage movies: add, update, and delete movie details.
- Manage seats: update seat availability and types.
- View and manage customer data.
- Track revenue generated from ticket sales.
- Access customer feedback and ratings for service improvement.

## Technologies Used

- Java
- JavaFX for UI
- SQLite database for data storage and retrieval

## Project Structure

- "model" package: Contains backend logic classes like "UserDAO", "DBHelper", "CustomerLogin", "ManagerLogin", and data access objects.
- "App.java": Main application class launching the JavaFX interface and role selection.
- FXML and resources: UI layout and images stored under "/resources/".
- Database: SQLite database file stored under "database/moviedb.db".

## Requirements

To run this project, you need:

- JDK 17 or higher installed on your system.
- JavaFX SDK 17.0.15 (or compatible) configured in your IDE.
- SQLite JDBC driver included in your project dependencies.
- The "database/moviedb.db" SQLite file with preloaded schema and sample data.
- Proper project folder structure to locate resource files such as images ("/resources/Movie.jpg" and "/resources/Logo.jpeg").

## Running the Project

1. Clone or download the repository.
2. Ensure JavaFX libraries are added to your project's build path.
3. Verify the SQLite database file is located at "database/moviedb.db".
4. Run the "App.jav" class as a Java Application.
5. On launch, select your role (Customer or Manager) to access respective login screens and functionalities.

## Notes

- Passwords are currently stored in plaintext for simplicity. For production, implement password hashing.
- The UI is designed using JavaFX GridPane and StackPane layouts for flexibility.
- Database connection handling is managed through the "DBHelper" class.
- Error handling is included to alert users on invalid inputs or database errors.
