package model;

import java.io.*;
import java.net.Socket;

public class BookingClient {
    public static String sendBookingConfirmation(String message) {
        try (Socket socket = new Socket("localhost", 12345);
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.write(message);
            out.newLine();
            out.flush();

            return in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to send booking confirmation.";
        }
    }
}
