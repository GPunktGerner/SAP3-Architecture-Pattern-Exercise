package server;

import java.io.*;
import java.net.*;

public class ServerApplication {

    public static void main(String[] args) {
        System.out.println("Server started...");

        try (ServerSocket serverSocket = new ServerSocket(5000)) {

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);

                new ClientHandler(clientSocket).start();
            }

        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    private static class ClientHandler extends Thread {
        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                String message;

                while ((message = in.readLine()) != null) {
                    System.out.println("Received from " + socket + ": " + message);
                    out.println("Server response: " + message);
                }

            } catch (IOException e) {
                System.out.println("Client disconnected: " + socket);
            }
        }
    }
}
