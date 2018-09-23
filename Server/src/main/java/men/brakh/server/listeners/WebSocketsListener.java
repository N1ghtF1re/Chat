package men.brakh.server.listeners;

import men.brakh.server.Server;
import men.brakh.server.endpoints.ChatEndpoint;

import java.io.BufferedReader;
import java.io.InputStreamReader;


/**
 * "Развертывание" сервера и запуск прослушки вебсокетов
 */
public class WebSocketsListener extends  Thread{
    Server server;
    String host = "localhost";
    int port = 8081;
    public WebSocketsListener(Server server) {
        this.server = server;
        start();
    }

    @Override
    public void run() {
        ChatEndpoint.server = server;
        org.glassfish.tyrus.server.Server server = new org.glassfish.tyrus.server.Server(host, port, "", ChatEndpoint.class);

        try {
            server.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            reader.readLine();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            server.stop();
        }
    }


}
