package model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Confirmation{

    
    private String movieTitle;
    private int numberOfSeats;
    private double totalPrice;

    public Confirmation(String movieTitle, int numberOfSeats, double totalPrice){
     this.movieTitle= movieTitle;
     this.numberOfSeats=numberOfSeats;
     this.totalPrice=totalPrice;

    

       Stage primaryStage= new Stage();
          primaryStage.setTitle("Confirmation");
          Image icon= new Image("Logo.jpeg");
          primaryStage.getIcons().add(icon);

        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(20));
        String ticketNumber = generateTicketNumber();

        Label ticketNumberLabel = new Label("Ticket Number: " + ticketNumber);      
        Label movieTitleLabel = new Label("Movie Title: " + movieTitle);
        Label numberOfSeatsLabel = new Label("Number of Seats: " + numberOfSeats);
        Label totalPriceLabel = new Label("Total Price: " + totalPrice);
        Button confirmButton = new Button("Confirm");
        confirmButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("CINEBOOK");
    alert.setHeaderText(null);
    alert.setContentText("Ticket Successfully Purchased");
    alert.showAndWait();
    writeTotalPriceToRevenueFile(totalPrice);
    writeToCustomerDetail(ticketNumber);
            
        });

        vbox.getChildren().addAll(ticketNumberLabel, movieTitleLabel, numberOfSeatsLabel, totalPriceLabel, confirmButton);

        Scene scene = new Scene(vbox, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();

       
        
    }
    private String generateTicketNumber() {
        Random random = new Random();
        int ticketNumber = random.nextInt(100000) + 1;
        return String.format("%06d", ticketNumber);
    }

    private void writeToCustomerDetail(String ticketNumber) {
    try (FileWriter fileWriter = new FileWriter("JAVA//CustomerDetail.csv", true);
         BufferedWriter writer = new BufferedWriter(fileWriter)) {
        String line = ticketNumber + "," + movieTitle + "," + numberOfSeats + "," + totalPrice;
        writer.write(line);
        writer.newLine();
    } catch (IOException e) {
        e.printStackTrace();
    }
}
   


    private void writeTotalPriceToRevenueFile(double totalPrice2) {
       try (FileWriter fileWriter = new FileWriter("JAVA//revenue.txt", true)) {
            fileWriter.write(totalPrice + "\n");
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    

    public static void main(String[] args) {
        

        Application.launch(args);
    }
}
