package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RevenueView {
    public void showRevenueWindow() {
        Stage stage = new Stage();
        stage.setTitle("Revenue Report");

        TableView<Revenue> table = new TableView<>();

        TableColumn<Revenue, String> movieCol = new TableColumn<>("Movie Title");
        movieCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getMovieTitle())
        );

        TableColumn<Revenue, Double> revenueCol = new TableColumn<>("Total Revenue");
        revenueCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getTotalRevenue())
        );

        table.getColumns().addAll(Arrays.asList(movieCol, revenueCol));

        RevenueDAO revenueDAO = new RevenueDAO();
        ObservableList<Revenue> data = FXCollections.observableArrayList(revenueDAO.getAllRevenue());

        table.setItems(data);

        VBox vbox = new VBox(table);
        Scene scene = new Scene(vbox, 400, 300);
        stage.setScene(scene);
        stage.show();
    }

    public void insertRevenue(int movieId, double totalRevenue) {
    String sql = "INSERT INTO revenue(movie_id, total_revenue) VALUES (?, ?)";

    try (Connection conn = DBHelper.getConnection(); // or however you get your DB connection
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setInt(1, movieId);
        pstmt.setDouble(2, totalRevenue);

        pstmt.executeUpdate();

    } catch (SQLException e) {
        e.printStackTrace();
        // Handle error or show alert
    }
  }
}
