import SQLRequests.SQLRequests;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.*;


public class ServerNIO {


    private static final char RS = 0x1E;
    private final ArrayList<SocketChannel> clientBase = new ArrayList<>();
    private final Map<SocketChannel, LinkedList<String>> pendingData = new HashMap<>();
    MessagingProtocol messagingProtocol = new MessagingProtocol();
    MessageReadAndWrite messageReadAndWrite = new MessageReadAndWrite();
    SQLRequests sqlRequests = new SQLRequests();
    Properties properties = new Properties();
    FileInputStream config = new FileInputStream("Data.properties");
    private Selector selector;

    public ServerNIO() throws SQLException, IOException {
    }

    public static void main(String[] args) throws IOException, SQLException {
        ServerNIO serverNIO = new ServerNIO();
        serverNIO.startServerNIO();
    }

    public void startServerNIO() throws IOException, SQLException {  //зарегали клиентов чтобы принимать сообщения
        selector = Selector.open();
        properties.load(config);
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        Integer PORT = Integer.valueOf(properties.getProperty("PORT"));
        InetSocketAddress inetSocketAddress = new InetSocketAddress(PORT);
        serverSocketChannel.bind(inetSocketAddress);
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            selector.select();
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
            while (keys.hasNext()) {
                SelectionKey key = keys.next();
                keys.remove();
                if (!key.isValid()) continue;

                if (key.isAcceptable()) {
                    acceptance(key);
                } else if (key.isReadable()) {
                    messageReadAndWrite.reading(key);
                } else if (key.isWritable()) {
                    messageReadAndWrite.writing(key);
                }
            }
        }
    }

    private void acceptance(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel channel = serverSocketChannel.accept();
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ);
        pendingData.put(channel, new LinkedList<>());
        clientBase.add(channel);
        System.out.println("A new connection has been made...");
    }

    public class MessageReadAndWrite {
        private SocketChannel channel;

        private void reading(SelectionKey key) throws IOException {
            channel = (SocketChannel) key.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            int numRead = channel.read(byteBuffer);
            if (numRead == -1) {
                pendingData.remove(channel);
                System.out.println("finished " + channel.socket().getRemoteSocketAddress());
                channel.close();
                key.cancel();
                return;
            }

            byte[] data = new byte[numRead];
            System.arraycopy(byteBuffer.array(), 0, data, 0, numRead);
            String gotData = new String(data);
            LinkedList<String> dataList = pendingData.get(channel);
            dataList.add(gotData);
            pendingData.replace(channel, dataList);
            key = channel.keyFor(selector);
            key.interestOps(SelectionKey.OP_WRITE);
        }

        private void writing(SelectionKey key) throws IOException, SQLException {
            String message;
            channel = (SocketChannel) key.channel();
            LinkedList<String> dataList = pendingData.get(channel);
            while (!dataList.isEmpty()) {
                String data = dataList.get(0);
                var thisAccount = messagingProtocol.parser(data);
                System.out.println("New command received: " + thisAccount[0] + " " + thisAccount[1] + " " + thisAccount[2]);
                message = constructorForMessage(key, thisAccount);
                if (message.equals("User entered incorrect data")) {
                    return;
                }
                sendAll(message);
                dataList.remove(0);
            }
            if (dataList.isEmpty()) {
                key = channel.keyFor(selector);
                key.interestOps(SelectionKey.OP_READ);
            }
        }

        private void sendAll(String messageForAll) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            byteBuffer.clear();
            byteBuffer.put(messageForAll.getBytes(StandardCharsets.UTF_8));
            byteBuffer.flip();
            clientBase.forEach(socketChannel -> {
                try {
                    socketChannel.write(byteBuffer);
                    byteBuffer.flip();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }


        private void sendTwentyMessages(SelectionKey key) throws IOException, SQLException {
            channel = (SocketChannel) key.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            byteBuffer.clear();
            byteBuffer.put(sqlRequests.getMessage(20).getBytes());
            byteBuffer.flip();
            channel.write(byteBuffer);
        }

        private String constructorForMessage(SelectionKey key, String[] msg) throws IOException, SQLException {
            channel = (SocketChannel) key.channel();
            if (msg[0].equals("T_REGISTER")) {
                msg[2] = msg[2].substring(0, msg[2].length() - 2);
                String readyMessage;
                boolean check = sqlRequests.authorisation(msg[1], msg[2]);
                if (check == false) {
                    clientBase.remove(channel);
                    channel.close();
                    key.cancel();
                    return "User entered incorrect data";
                } else {
                    messageReadAndWrite.sendTwentyMessages(key);
                    readyMessage = msg[1] + " was registered" + RS;
                }
                return readyMessage;
            } else if (msg[0].equals("T_MESSAGE")) {
                //    msg[2] = msg[2].replace(RS,' ');
                String readyMessage; // Сконструированное сообщение
                readyMessage = "[ " + new Date() + " ] " + msg[1] + " : " + msg[2];
                sqlRequests.postMessage(readyMessage);
                return readyMessage;
            } else {
                return "Error of construction";
            }

        }


    }
}

