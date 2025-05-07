import model.MovieDAO;
import model.SeatDAO;
import model.BookingDAO;
import model.RatingDAO;
import model.RevenueDAO;
import model.UserDAO;


public class Main {
    public static void main(String[] args) {
        // Test login
        boolean login = UserDAO.authenticate("Alice", "password123");
        System.out.println("Login success: " + login);

        // List all movies
        System.out.println("\nAvailable movies:");
        MovieDAO.listMovies();

        // Show available seats for movie ID 1
        System.out.println("\nAvailable seats for movie ID 1:");
        SeatDAO.showAvailableSeats(1);

        // Book a seat (user 1 books seat 1)
        System.out.println("\nBooking seat:");
        BookingDAO.bookSeat(1, 1);

        // Rate a movie
        System.out.println("\nSubmitting rating:");
        RatingDAO.rateMovie(1, 1, 4.5);

        // Show revenue
        System.out.println("\nMovie revenue:");
        RevenueDAO.showRevenue();
    }
}
