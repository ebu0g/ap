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

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}
