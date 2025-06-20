package Lab2_FTP;

import java.io.*;
import java.net.*;

public class FTPServer {
    public static void main(String[] args) {
        int port = 2121; 
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("FTP Server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket socket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataOutputStream out = new DataOutputStream(socket.getOutputStream())
        ) {
            String fileName = in.readLine();
            File file = new File("server_files/" + fileName);

            if (file.exists()) {
                out.writeBytes("FOUND\n");
                FileInputStream fileIn = new FileInputStream(file);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileIn.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                fileIn.close();
                System.out.println("Sent file: " + fileName);
            } else {
                out.writeBytes("NOT_FOUND\n");
                System.out.println("File not found: " + fileName);
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
