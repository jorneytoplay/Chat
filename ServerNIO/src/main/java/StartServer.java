import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class StartServer {

    private static Set<PrintWriter> writers = new HashSet<>();
    MessagingProtocol messagingProtocol = new MessagingProtocol();
    Properties properties = new Properties();
    FileInputStream config = new FileInputStream("Data.properties");

    public StartServer() throws FileNotFoundException {
    }

    public static void main(String[] args) throws IOException {
        StartServer startServer = new StartServer();
        startServer.startingServer();
    }

    public void startingServer() throws IOException {
        properties.load(config);
        Integer PORT = Integer.valueOf(properties.getProperty("PORT"));
        System.out.println("Server is running...");
        try {
            //Отсдеживаем входящие клиентские запросы по порту 1488
            ServerSocket serverSocket = new ServerSocket(PORT);

            //Цикл постоянного ожидания клиентского подключения
            while (true) {
                //Создаем новый сокет для общения с клиентом
                Socket clientSocket = serverSocket.accept();

                Thread t = new Thread(new ClientHandler(clientSocket));
                t.start();

                System.out.println("new connection...");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public class ClientHandler implements Runnable {
        Scanner in;
        PrintWriter out;
        Socket socket;
        private String input;

        public ClientHandler(Socket socket) {
            try {
                this.socket = socket;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {

            try {
                in = new Scanner(socket.getInputStream());
                out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);


                while (true)    //Регистрация нового пользователя
                {
                    input = in.nextLine();
                    if (input != null) //Ждём сообщения от пользователя об регистрации
                    {
                        break;
                    }
                }

                writers.add(out); //Добавляем пользователя

                for (PrintWriter writer : writers) {
                    writer.println(messagingProtocol.parser(input)); //Засовываем сообщение в парсер и рассылаем сообщение об успешной регистрации
                }

                while (true) {
                    input = in.nextLine();

                    if (input.toLowerCase().startsWith("/quit")) {
                        return;
                    }

                    for (PrintWriter writer : writers) {
                        writer.println("[ " + new Date() + " ] " + messagingProtocol.parser(input)); //Засовываем сообщение в парсер и рассылаем сообщение
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                for (PrintWriter writer : writers) {
                    writer.println("User with name " + input + " was disconnected");
                }
            }
        }
    }
}