package model;

public class Revenue {
    private String movieTitle;
    private double totalRevenue;

    public Revenue(String movieTitle, double totalRevenue) {
        this.movieTitle = movieTitle;
        this.totalRevenue = totalRevenue;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }
}
