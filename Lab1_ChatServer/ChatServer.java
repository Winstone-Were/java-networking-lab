import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {

    // Stores all connected clients
    static Map<String, ClientHandler> clients = new HashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Server started on port 12345...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                // Setup streams
                DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());

                // Get username
                dos.writeUTF("Enter your username: ");
                String username = dis.readUTF().trim();

                // Check for duplicate usernames
                synchronized (clients) {
                    if (clients.containsKey(username)) {
                        dos.writeUTF("Username already taken. Connection closed.");
                        clientSocket.close();
                        continue;
                    }
                }

                // Create handler and start thread
                ClientHandler handler = new ClientHandler(clientSocket, username, dis, dos);
                synchronized (clients) {
                    clients.put(username, handler);
                }
                new Thread(handler).start();

                broadcast("Server", username + " has joined the chat.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void sendPrivateMessage(String sender, String recipient, String message) {
        ClientHandler receiver;
        synchronized (clients) {
            receiver = clients.get(recipient);
        }

        if (receiver != null) {
            receiver.sendMessage(sender + " (private): " + message);
        } else {
            ClientHandler senderHandler = clients.get(sender);
            if (senderHandler != null) {
                senderHandler.sendMessage("Server: User '" + recipient + "' not found.");
            }
        }
    }

    static void broadcast(String sender, String message) {
        synchronized (clients) {
            for (ClientHandler handler : clients.values()) {
                if (!handler.getUsername().equals(sender)) {
                    handler.sendMessage(sender + ": " + message);
                }
            }
        }
    }

    static void removeClient(String username) {
        synchronized (clients) {
            clients.remove(username);
        }
        broadcast("Server", username + " has left the chat.");
    }
}
