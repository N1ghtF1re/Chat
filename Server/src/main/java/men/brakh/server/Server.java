package men.brakh.server;

import men.brakh.chat.Message;
import men.brakh.chat.User;
import men.brakh.server.data.ExtendUser;
import men.brakh.server.data.TwoPersonChat;
import men.brakh.logger.Logger;
import men.brakh.server.listeners.SocketsListener;
import men.brakh.server.listeners.WebSocketsListener;
import men.brakh.server.queues.AgentsQueue;
import men.brakh.server.queues.CustomerChatQueue;
import men.brakh.server.senders.Sender;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

@Configuration
public class Server {
    private int id = 0;
    public static final int PORT = 7777;
    public CustomerChatQueue customerChatQueue;
    public AgentsQueue agentsQueue;
    private Logger logger;


    public Server() throws IOException {
        customerChatQueue = new CustomerChatQueue();
        agentsQueue = new AgentsQueue();
        logger = new Logger(true);
    }
    public Server(int port) throws IOException {
        this();
        startServer(port);
    }

    /**
     * Запуск сервера
     * @param port порт
     * @throws IOException
     */
    public void startServer(int port){
        SocketsListener socketsListener = new SocketsListener(this, port);
        WebSocketsListener webSocketsListener = new WebSocketsListener(this);

    }

    /**
     * Поиск свободных агентов
     */
    public void checkFreeAgents() {
        synchronized (agentsQueue) { // На время проверки синхронизируем очередь агентов
            ExtendUser agent = agentsQueue.getFirst(); // Получаем первого свободного агента
            if (agent != null) { // Если есть свободный агент
                synchronized (customerChatQueue) {
                    TwoPersonChat twoPersonChat = customerChatQueue.getFree(); // Ищем чат с пользователем, которому нужна помощь
                    if (twoPersonChat != null) { // Если для агента есть работа (есть "скучающий" пользователь)
                        agent = agentsQueue.poll(); // Извлекаем агента из очереди с удалением

                        twoPersonChat.setAgent(agent); // Привязываем агента к пользователю

                        log("Agent " + agent.getUser() + " connected to customer " + twoPersonChat.getCustomer().getUser());

                        // Сообщаем пользователю что нашли ему агента
                        twoPersonChat.getCustomer().getSender().serverSend("К Вам подключился наш агент - " +
                                agent.getUser() + ". Пожалуйста, не обижайте его.");

                        // Сообщаем агенту что нашли ему пользователя
                        agent.getSender().serverSend(String.valueOf(twoPersonChat.getId()), "chat");
                        agent.getSender().serverSend("Вы подключились к " + twoPersonChat.getCustomer().getUser(), twoPersonChat.getId());

                        // Отправляем агенту все предыдущие сообщения чата
                        ArrayList<Message> messages = twoPersonChat.getMessages();
                        for (Message msg : messages) {
                            agent.getSender().send(msg.getJSON());
                        }

                    }
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

        int chat_id = chat.getId();

        customerChatQueue.remove(chat); // Удаляем объект чата из очереди
        if (agent != null) {
            log("Agent " + agent.getUser() + " added to the end of the queue");

            agent.getSender().serverSend(user.getName() + " отключился от Вас :C", chat_id); // Сообщаем агенту что его пользователь отключился
            agent.getSender().serverSend("", "user-leave", chat_id);
            agentsQueue.add(agent); // Освобождаем агента (добавляем в конец очереди агентов)

            log(user.getName() + " the user has disconnected from agent " + agent.getUser());
        }

    }

    /**
     * Удаление агента из чата с пользователем и добавление его в общую очередь агентов
     * @param id ID чата
     */
    public void removeAgentFromChat(int id) {
        TwoPersonChat chat = customerChatQueue.getById(id);
        if (chat == null) {
            return;
        }
        User agent = chat.getAgent().getUser();
        Sender sender = chat.getAgent().getSender();
        chat.setAgent(null);
        chat.getCustomer().getSender().serverSend("Агент " + agent + " отключился от Вас. Ждите, пока подключится следующий агент");
        log("Agent " + agent + " has disconnected from " + chat.getCustomer());
        agentsQueue.add(agent, sender);
        log("Agent " + agent + " added to the end of the queue");
    }

    public void removeAgentsFromAllChats(User user) {
        TwoPersonChat chat;
        while ((chat = customerChatQueue.searchAgent(user)) != null) {
            removeAgentFromChat(chat.getId());
        }
    }

    /**
     * Удаление агента из очереди
     * @param agent Объект агента
     */
    public void removeAgentFromQueue(User agent) {
        agentsQueue.remove(agent);
    }

    /**
     * Проверка, находится ли агент в одной из очередей (В чате с пользователем или очереди агентов)
     * @param agent Объект агента
     * @return True если агент новый и не находится в очередях
     */
    public Boolean isAgentNew(User agent) {
        return (customerChatQueue.searchAgent(agent) == null)  // Ищем агента в очередях
                && (agentsQueue.searchAgent(agent) == null);
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
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.write("[ERROR] RECEIVED EXCEPTION: " + e.toString() + "\nStackTrace: " + sw.toString());

        } catch (IOException e2) {
            e.printStackTrace();
        }


    }


    public static void main(String[] args) throws IOException {
        new Server(PORT);
    }
}
