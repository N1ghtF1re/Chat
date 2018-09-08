package men.brakh;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class Server {

    public final int PORT = 9999;
    public LinkedList<ServerSomthing> serverList = new LinkedList<ServerSomthing>(); // список всех нитей

    public CustomerChatQueue customerChatQueue;


    public Server() throws IOException {
        customerChatQueue = new CustomerChatQueue();
        ServerSocket server = new ServerSocket(PORT);
        try {
            while (true) {
                // Блокируется до возникновения нового соединения:
                Socket socket = server.accept();
                try {
                    System.out.println("Kek");
                    serverList.add(new ServerSomthing(socket, this)); // добавить новое соединенние в список

                } catch (IOException e) {
                    // Если завершится неудачей, закрывается сокет,
                    // в противном случае, нить закроет его при завершении работы:
                    socket.close();
                }
            }
        } finally {
            server.close();
        }
    }

    public static void main(String[] args) throws IOException {
        new Server();
    }
}