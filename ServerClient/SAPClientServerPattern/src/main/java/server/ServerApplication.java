package server;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerApplication {

    private static final Map<String, PrintWriter> clients =
            Collections.synchronizedMap(new HashMap<>());

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
        private PrintWriter out;
        private BufferedReader in;
        private String clientName;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                out.println("Enter your name:");
                clientName = in.readLine();

                if (clientName == null || clientName.trim().isEmpty()) {
                    out.println("Invalid name. Disconnecting.");
                    socket.close();
                    return;
                }

                synchronized (clients) {
                    clients.put(clientName, out);
                }

                out.println("Welcome, " + clientName + "!");

                String msg;
                while ((msg = in.readLine()) != null) {
                    System.out.println(clientName + " says: " + msg);

                    if (msg.contains(":")) {
                        String[] parts = msg.split(":", 2);
                        String target = parts[0].trim();
                        String text   = parts[1].trim();

                        PrintWriter targetWriter = clients.get(target);

                        if (targetWriter != null) {
                            targetWriter.println(clientName + " → you: " + text);
                        } else {
                            out.println("Server: Client '" + target + "' not found.");
                        }

                    } else {
                        out.println("Server echo: " + msg);
                    }
                }

            } catch (IOException e) {
                System.out.println("Connection with " + clientName + " lost.");
            } finally {
                try { socket.close(); } catch (IOException ignored) {}

                synchronized (clients) {
                    clients.remove(clientName);
                }

                System.out.println("Client disconnected: " + clientName);
            }
        }
    }
}
