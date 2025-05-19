package model;

import javafx.beans.property.*;

public class Seat {
    private final IntegerProperty id;
    private final IntegerProperty movieId;
    private final StringProperty seatNumber;
    private final BooleanProperty isBooked;

    public Seat(int id, int movieId, String seatNumber, boolean isBooked) {
        this.id = new SimpleIntegerProperty(id);
        this.movieId = new SimpleIntegerProperty(movieId);
        this.seatNumber = new SimpleStringProperty(seatNumber);
        this.isBooked = new SimpleBooleanProperty(isBooked);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public IntegerProperty movieIdProperty() {
        return movieId;
    }

    public StringProperty seatNumberProperty() {
        return seatNumber;
    }

    public BooleanProperty isBookedProperty() {
        return isBooked;
    }

    // Optional: Getters
    public int getId() {
        return id.get();
    }

    public int getMovieId() {
        return movieId.get();
    }

    public String getSeatNumber() {
        return seatNumber.get();
    }

    public boolean getIsBooked() {
        return isBooked.get();
    }
    
}
