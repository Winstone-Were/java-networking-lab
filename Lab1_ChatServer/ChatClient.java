
<<<<<<< HEAD
=======
import java.io.*;
import java.util.*;
import java.net.*;

public class ChatClient implements Runnable {
    Scanner scn = new Scanner(System.in);
    private String name;
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket s;
    boolean isloggedin;

    public ChatClient(Socket s, String name, DataInputStream dis, DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        this.name = name;
        this.s = s;
        this.isloggedin = true;
    }

    @Override
    public void run() {
        String received;
        while (true) {
            try {
                received = dis.readUTF();
                System.out.println(received);

                if (received.equals("logout")) {
                    this.isloggedin = false;
                    this.s.close();
                    break;
                }

                StringTokenizer st = new StringTokenizer(received, "#");
                String MsgToSend = st.nextToken();
                String recipient = st.nextToken();

                for ( ChatClient mc : ChatServer.ar){
                    if(mc.name.equals(recipient) && mc.isloggedin==true){
                        mc.dos.writeUTF(this.name + " : " + MsgToSend);
                        break;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            try{
                this.dis.close();
                this.dos.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
>>>>>>> 3f5a195 (✨ feat(ftp): implement basic ftp server and client)
