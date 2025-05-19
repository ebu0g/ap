package model;

public class Movie {
    private int id;
    private String title;
    private String genre;
    private int duration;
    private double price;  // add price field

    public Movie(int id, String title, String genre, int duration, double price) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public int getDuration() {
        return duration;
    }

    public double getPrice() {   // add getter
        return price;
    }
}
