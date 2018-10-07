package men.brakh.server.listeners;

import men.brakh.server.Server;
import men.brakh.server.ServerSomthing;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Прослушка сокетов консольного клиента в отдельном потоке
 * На данный момент класс считается устаревшим всвязи с отказом от
 * использования сокетов и будет удален в ближайшее время.
 */
@Deprecated
public class SocketsListener extends Thread {
    Server server;
    private ExecutorService pool;
    private int port;

    private int threadsCount = 100;

    public SocketsListener(Server server, int port) {
        this.server = server;
        this.port = port;
        start();
    }

    @Override
    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            server.log("Server start job");
            System.out.println(serverSocket.toString());
            try {
                pool = Executors.newFixedThreadPool(threadsCount);
                while (true) {
                    // Блокируется до возникновения нового соединения:
                    Socket socket = serverSocket.accept();
                    try {
                        server.log("New connection: " + socket.toString());
                        pool.execute(new ServerSomthing(socket, server)); // добавить новое соединенние в список

                    } catch (IOException e) {
                        server.log(e);
                        // Если завершится неудачей, закрывается сокет,
                        // в противном случае, нить закроет его при завершении работы:
                        socket.close();
                    }
                }
            } catch (IOException e) {
                server.log(e);
            }
            finally {
                serverSocket.close();
            }
        } catch (IOException e) {
            server.log(e);
        }
    }
}
