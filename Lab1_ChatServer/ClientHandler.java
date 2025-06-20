import java.io.*;
import java.net.*;

class ClientHandler implements Runnable {
    private final Socket socket;
    private final String username;
    private final DataInputStream dis;
    private final DataOutputStream dos;
    private boolean isLoggedIn;

    public ClientHandler(Socket socket, String username, DataInputStream dis, DataOutputStream dos) {
        this.socket = socket;
        this.username = username;
        this.dis = dis;
        this.dos = dos;
        this.isLoggedIn = true;
    }

    public String getUsername() {
        return username;
    }

    public void sendMessage(String msg) {
        try {
            dos.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            dos.writeUTF("Welcome " + username + "! Use '@recipient your message' to send private messages. Type 'logout' to exit.");

            while (isLoggedIn) {
                String received = dis.readUTF();

                if (received.equalsIgnoreCase("logout")) {
                    isLoggedIn = false;
                    socket.close();
                    ChatServer.removeClient(username);
                    break;
                }

                // Private message format: @username message
                if (received.startsWith("@")) {
                    int spaceIndex = received.indexOf(' ');
                    if (spaceIndex != -1) {
                        String recipient = received.substring(1, spaceIndex);
                        String message = received.substring(spaceIndex + 1);
                        ChatServer.sendPrivateMessage(username, recipient, message);
                    } else {
                        sendMessage("Invalid message format. Use '@username message'.");
                    }
                } else {
                    // Broadcast to all
                    ChatServer.broadcast(username, received);
                }
            }
        } catch (IOException e) {
            ChatServer.removeClient(username);
        }
    }
}
