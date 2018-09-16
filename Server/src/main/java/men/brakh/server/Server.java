package men.brakh.server;

import men.brakh.chat.Message;
import men.brakh.server.data.ExtendUser;
import men.brakh.server.data.TwoPersonChat;
import men.brakh.logger.Logger;
import men.brakh.server.queues.AgentsQueue;
import men.brakh.server.queues.CustomerChatQueue;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;

public class Server {
    private int id = 0;
    public static final int PORT = 7777;
    public LinkedList<ServerSomthing> serverList = new LinkedList<ServerSomthing>(); // список всех нитей

    public CustomerChatQueue customerChatQueue;
    public AgentsQueue agentsQueue;
    private Logger logger;


    /**
     * Поиск свободных агентов
     */
    synchronized void checkFreeAgents() {
        ExtendUser agent = agentsQueue.getFirst(); // Получаем первого свободного агента
        if (agent != null) {
            TwoPersonChat twoPersonChat = customerChatQueue.getFree(); // Ищем чат с пользователем, которому нужна помощь
            if (twoPersonChat != null) {
                synchronized (agentsQueue) {
                    agent = agentsQueue.poll(); // Извлекаем агента из очереди с удалением
                }

                twoPersonChat.setAgent(agent); // Привязываем агента к пользователю

                log("Agent " + agent.getUser() + " connected to customer " + twoPersonChat.getCustomer().getUser());

                // Сообщаем пользователю что нашли ему агента
                twoPersonChat.getCustomer().getSrvSom().serverSend("К Вам подключился наш агент - " +
                        agent.getUser() + ". Пожалуйста, не обижайте его.");

                // Сообщаем агенту что нашли ему пользователя
                agent.getSrvSom().serverSend("Вы подключились к " + twoPersonChat.getCustomer().getUser());

                // Отправляем агенту все предыдущие сообщения чата
                ArrayList<Message> messages = twoPersonChat.getMessages();
                for (Message msg : messages) {
                    agent.getSrvSom().send(msg.getJSON());
                }

            }
        }
    }

    public synchronized int getNewId() {
        return ++id;
    }

    public Server() throws IOException {
        customerChatQueue = new CustomerChatQueue();
        agentsQueue = new AgentsQueue();
        logger = new Logger(true);
        // new checkFreeAgents();
        log("Server start job");
    }

    public void startSocket(int port) throws IOException {
        ServerSocket server = new ServerSocket(port);
        try {
            while (true) {
                // Блокируется до возникновения нового соединения:
                Socket socket = server.accept();
                try {
                    log("New connection: " + socket.toString());
                    serverList.add(new ServerSomthing(socket, this)); // добавить новое соединенние в список

                } catch (IOException e) {
                    log(e);
                    // Если завершится неудачей, закрывается сокет,
                    // в противном случае, нить закроет его при завершении работы:
                    socket.close();
                }
            }
        } finally {
            server.close();
        }
    }

    public Server(int port) throws IOException {
        this();
        startSocket(port);
    }

    synchronized public void log(String message) {
        try {
            logger.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    synchronized public void log(Exception e) {
        try {
            logger.write("[ERROR] RECEIVED EXCEPTION: " + e.toString() + "\nStackTrace: " + e.getStackTrace());
        } catch (IOException e2) {
            e.printStackTrace();
        }


    }


    public static void main(String[] args) throws IOException {
        new Server(PORT);
    }
}
