import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientApp {

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 12345)) {
            Scanner scanner = new Scanner(System.in);
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            // Receive prompt and send username
            System.out.println(dis.readUTF());
            String username = scanner.nextLine();
            dos.writeUTF(username);

            // Start reader thread
            Thread readThread = new Thread(() -> {
                try {
                    while (true) {
                        System.out.println(dis.readUTF());
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected.");
                }
            });
            readThread.start();

            // Read user input and send to server
            while (true) {
                String msg = scanner.nextLine();
                dos.writeUTF(msg);
                if (msg.equalsIgnoreCase("logout")) {
                    break;
                }
            }

        } catch (IOException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }
}
