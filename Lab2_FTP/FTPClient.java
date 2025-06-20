package Lab2_FTP;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class FTPClient {
    public static void main(String[] args) {
        String server = "localhost";
        int port = 2121;
        String fileName = "example.txt";

        try (
            Socket socket = new Socket(server, port);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        ) {
            out.writeBytes(fileName + "\n");

            String response = in.readLine();
            if ("FOUND".equals(response)) {
                FileOutputStream fileOut = new FileOutputStream("client_files/" + fileName);
                InputStream inputStream = socket.getInputStream();
                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fileOut.write(buffer, 0, bytesRead);
                }

                fileOut.close();
                System.out.println("File received: " + fileName);
            } else {
                System.out.println("File not found on server.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
