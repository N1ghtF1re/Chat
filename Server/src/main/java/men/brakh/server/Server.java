package men.brakh.server;

import men.brakh.chat.Message;
import men.brakh.chat.User;
import men.brakh.server.data.ExtendUser;
import men.brakh.server.data.TwoPersonChat;
import men.brakh.logger.Logger;
import men.brakh.server.queues.AgentsQueue;
import men.brakh.server.queues.CustomerChatQueue;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private int id = 0;
    private int threadsCount = 100;
    public static final int PORT = 7777;
    public CustomerChatQueue customerChatQueue;
    public AgentsQueue agentsQueue;
    private Logger logger;
    private ExecutorService pool;


    public Server() throws IOException {
        customerChatQueue = new CustomerChatQueue();
        agentsQueue = new AgentsQueue();
        logger = new Logger(true);
        log("Server start job");
    }
    public Server(int port) throws IOException {
        this();
        startSocket(port);
    }

    /**
     * Запуск сервера
     * @param port порт
     * @throws IOException
     */
    public void startSocket(int port) throws IOException {
        ServerSocket server = new ServerSocket(port);
        pool = Executors.newFixedThreadPool(threadsCount);
        try {
            while (true) {
                // Блокируется до возникновения нового соединения:
                Socket socket = server.accept();
                try {
                    log("New connection: " + socket.toString());
                    pool.execute(new ServerSomthing(socket, this)); // добавить новое соединенние в список

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

    /**
     * Поиск свободных агентов
     */
    public synchronized void checkFreeAgents() {
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
                twoPersonChat.getCustomer().getSender().serverSend("К Вам подключился наш агент - " +
                        agent.getUser() + ". Пожалуйста, не обижайте его.");

                // Сообщаем агенту что нашли ему пользователя
                agent.getSender().serverSend("Вы подключились к " + twoPersonChat.getCustomer().getUser());

                // Отправляем агенту все предыдущие сообщения чата
                ArrayList<Message> messages = twoPersonChat.getMessages();
                for (Message msg : messages) {
                    agent.getSender().send(msg.getJSON());
                }

            }
        }
    }

    /**
     * Удаляем чат с пользователем
     * @param user объект пользователя
     */
    public void removeCustomerChatElement(User user) {
        TwoPersonChat chat = customerChatQueue.searchCustomer(user); // Получаем чат пользователя
        if (chat == null) {
            return;
        }
        ExtendUser agent = chat.getAgent();
        chat.setAgent(null);

        customerChatQueue.remove(chat); // Удаляем объект чата из очереди
        if (agent != null) {
            agentsQueue.add(agent); // Освобождаем агента (добавляем в конец очереди агентов)
            log("Agent " + agent.getUser() + " added to the end of the queue");

            agent.getSender().serverSend(user.getName() + " отключился от Вас :C"); // Сообщаем агенту что его пользователь отключился
            log(user.getName() + " the user has disconnected from agent " + agent.getUser());
        }

    }

    /**
     * Удаление агента из чата с пользователем и добавление его в общую очередь агентов
     * @param agent Объект агента
     */
    public void removeAgentFromChat(User agent) {
        TwoPersonChat chat = customerChatQueue.searchAgent(agent);
        if (chat == null) {
            return;
        }
        Sender sender = chat.getAgent().getSender();
        chat.setAgent(null);
        chat.getCustomer().getSender().serverSend("Агент " + agent + " отключился от Вас. Ждите, пока подключится следующий агент");
        log("Agent " + agent + " has disconnected from " + chat.getCustomer());
        agentsQueue.add(agent, sender);
        log("Agent " + agent + " added to the end of the queue");
    }

    /**
     * Удаление агента из очереди
     * @param agent Объект агента
     */
    public void removeAgentFromQueue(User agent) {
        agentsQueue.remove(agent);
    }

    /**
     * Получем ID нового пользователя
     * @return ID
     */
    public synchronized int getNewId() {
        return ++id;
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
