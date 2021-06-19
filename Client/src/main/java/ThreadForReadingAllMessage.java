import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

public class ThreadForReadingAllMessage extends Thread {

    Socket socket;
    Scanner in;
    MessagingProtocol messagingProtocol = new MessagingProtocol();

    ThreadForReadingAllMessage(Socket socket) throws IOException {
        this.socket = socket;

    }

    @Override
    public void run() {
        InputStream is;
        try {
            is = socket.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(is));

            while (true) {
                String line = in.readLine();
                line = messagingProtocol.lineBreak(line);
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
