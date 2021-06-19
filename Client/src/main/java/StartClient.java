import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;

public class StartClient {
    Scanner in;
    PrintWriter out;
    String message;
    MessagingProtocol messagingProtocol = new MessagingProtocol();
    Scanner scanner = new Scanner(System.in);
    Properties properties = new Properties();
    FileInputStream config = new FileInputStream("Data.properties");
    private String username;
    private String password;

    public StartClient() throws FileNotFoundException {
    }

    public static void main(String[] args) throws IOException {
        StartClient startClient = new StartClient();
        startClient.startingConnection();

    }

    public void startingConnection() throws IOException {
        properties.load(config);
        Integer PORT = Integer.valueOf(properties.getProperty("PORT"));
        String IP = properties.getProperty("IP");
        try (Socket socket = new Socket(IP, PORT))  //Создаём сокет соединения
        {
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);
            new ThreadForReadingAllMessage(socket).start(); //Поток постоянно читающий сообщения
            out.println(getLogin()); //Метод регистрации пользователя

            System.out.println("You have successfully registered. Welcome to the chat.");


            while (true) //Постоянно пишем сообщение
            {
                out.println(getMessage()); //Отправляем сконструированное сообщение на сервер
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            in.close();
            out.close();
        }
    }

    public char[] getLogin() {
        System.out.print("Enter your name: ");
        while (true) {
            username = scanner.nextLine();
            if (username != "" && username != null) {
                break;
            }
        }
        System.out.print("Enter your password: ");
        while (true) {
            password = scanner.nextLine();
            if (password != "" && password != null) {
                break;
            }
        }
        return messagingProtocol.T_REGISTER_constructor(username, password); //Возвращаем сконструированное сообщение
    }

    public char[] getMessage() {

        while (true) {
            message = scanner.nextLine();
            if (message != "" && message != null) {
                break;
            } else {
                System.out.println("Try again...(Message doesn't be empty");
            }
        }
        return messagingProtocol.T_MESSAGE_constructor(message, username);  //Возвращаем сконструированное сообщение

    }
}
