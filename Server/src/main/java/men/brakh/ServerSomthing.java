package men.brakh;

import men.brakh.chat.Message;
import men.brakh.chat.User;
import men.brakh.chat.UsersTypes;

import java.io.*;
import java.net.Socket;
import java.util.ArrayDeque;

class ServerSomthing extends Thread {

    private Socket socket; // сокет, через который сервер общается с клиентом,
    // кроме него - клиент и сервер никак не связаны
    private BufferedReader in; // поток чтения из сокета
    private BufferedWriter out; // поток записи в сокет
    private Server server;




    private ArrayDeque<User> agentsList = new ArrayDeque<User>();

    public ServerSomthing(Socket socket, Server server) throws IOException {
        this.socket = socket;
        // если потоку ввода/вывода приведут к генерированию исключения, оно проброситься дальше
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.server = server;


        start(); // вызываем run()
    }

    /**
     * Удаляем чат с пользователем
     * @param user объект пользователя
     */
    void removeCustomerChatElement(User user) {
        TwoPersonChat chat = server.customerChatQueue.searchCustomer(user); // Получаем чат пользователя
        if (chat == null) {
            return;
        }
        ExtendUser agent = chat.getAgent();
        chat.setAgent(null);

        server.customerChatQueue.remove(chat); // Удаляем объект чата из очереди
        if (agent != null) {
            server.agentsQueue.add(agent); // Освобождаем агента (добавляем в конец очереди агентов)
            server.log("Agent " + agent.getUser() + " added to the end of the queue");

            agent.getSrvSom().serverSend(user.getName() + " отключился от Вас :C"); // Сообщаем агенту что его пользователь отключился
            server.log(user.getName() + " the user has disconnected from agent " + agent.getUser());
        }

    }

    /**
     * Обработчик сообщений клиента
     * @param userMessage сообщение пользователя
     * @return false если надо разорвать соединение
     */
    Boolean usersHandler(Message userMessage) {

        if (userMessage.getStatus().equals("exit")) { // Пользователь захотел отключиться
            serverSend("Вы отключились от сервера", "exit");
            removeCustomerChatElement(userMessage.getUser()); // Освобождаем привязанного агента
            server.log(userMessage.getUser() + " has disconnected from the server");
            return false;
        } else if(userMessage.getStatus().equals("leave")) { // Пользователь захотел отключиться
            serverSend("Вы отключились от агента. Чтобы подключиться к новому агенту - напишите сообщение в чат", "ok");
            removeCustomerChatElement(userMessage.getUser()); // Освобождаем привязанного агента
            return true;
        } else if(userMessage.getStatus().equals("reg")) {
            server.log("New registration: " + userMessage.getUser());
            serverSend(String.valueOf(server.getNewId()), "reg");
            return true;
        }

        CustomerChatQueue chat = server.customerChatQueue; // Очередь чатов
        if (chat.searchCustomer(userMessage.getUser()) == null) { // Если в очереди чатов еще нет этого пользователя => создаем чат
            chat.add(userMessage.getUser(), this);
            server.log("Customer " + userMessage.getUser() +" awaiting response. Message: " + userMessage.getMessage());

            TwoPersonChat userchat = server.customerChatQueue.searchCustomer(userMessage.getUser());
            userchat.addMessage(userMessage);

            String msg = "Ваш запрос принят. Ожидайте подключения специалиста";
            this.serverSend(msg); // отослать принятое сообщение с

        } else { // У пользователя уже есть созданный чат
            TwoPersonChat currChat = chat.searchCustomer(userMessage.getUser()); // Получаем текущий чат
            if (currChat.getAgent() != null) { // Если в чате уже есть агент => отправляем ему
                currChat.getAgent().getSrvSom().send(userMessage.getJSON());
            }
            currChat.addMessage(userMessage); // Сохраняем историю сообшений
            server.log("Message from customer " + userMessage.getUser() + " to agent " + currChat.getAgent() + ": " +
                    userMessage.getMessage() + ". Status: " + userMessage.getStatus());
        }
        return true;
    }

    /**
     * Удаление агента из чата с пользователем и добавление его в общую очередь агентов
     * @param agent Объект агента
     */
    void removeAgentFromChat(User agent) {
        TwoPersonChat chat = server.customerChatQueue.searchAgent(agent);
        if (chat == null) {
            return;
        }
        chat.setAgent(null);
        chat.getCustomer().getSrvSom().serverSend("Агент " + agent + " отключился от Вас. Ждите, пока подключится следующий агент");
        server.log("Agent " + agent + " has disconnected from " + chat.getCustomer());
        server.agentsQueue.add(agent, this);
        server.log("Agent " + agent + " added to the end of the queue");
    }

    /**
     * Удаление агента из очереди
     * @param agent Объект агента
     */
    void removeAgentFromQueue(User agent) {
        server.agentsQueue.remove(agent);
    }
    /**
     * Обработчик сообщений агента
     * @param userMessage сообщение
     * @return false если надо разорвать соединение
     */
    Boolean agentsHandler(Message userMessage){

        if (userMessage.getStatus().equals("exit")) { // Агент выходит из ВСЕГО чата
            serverSend("Вы отключились от сервера", "exit");
            synchronized (server.agentsQueue) { // Защищаемся от того, что таймер поиска свободных агентов может "излвечь" и перенаправить "выходящего" агента
                removeAgentFromChat(userMessage.getUser());
                removeAgentFromQueue(userMessage.getUser());
            }
            server.log("Agent " + userMessage.getUser() + " has disconnected from the server");
            return false;
        }else if(userMessage.getStatus().equals("skip")) {
            serverSend("Вы отключились от пользователя.");
            synchronized (server.agentsQueue) {
                removeAgentFromChat(userMessage.getUser());
            }
            server.log("Agent " + userMessage.getUser() + " skip customer");
            return true;
        }else if(userMessage.getStatus().equals("reg")) {
            server.log("New registration: " + userMessage.getUser());
            int newId = server.getNewId();
            serverSend(String.valueOf(newId), "reg");
            User agent = userMessage.getUser();
            agent.setId(newId); // Меняем id с -1 на новый
            userMessage.setUser(agent);

        }

        if ((server.customerChatQueue.searchAgent(userMessage.getUser()) == null)  // Ищем агента в очередях
                && (server.agentsQueue.searchAgent(userMessage.getUser()) == null)){
            server.agentsQueue.add(userMessage.getUser(), this);
            server.log("Agent " + userMessage.getUser() + " added to the end of the queue");
        } else {
            CustomerChatQueue chat = server.customerChatQueue;
            if (chat != null) {
                TwoPersonChat currChat = chat.searchAgent(userMessage.getUser());
                if (currChat == null) {
                    return true;
                }
                if (!userMessage.getStatus().equals("ok")) {
                    return true;
                }
                if (currChat.getCustomer() != null) {
                    currChat.getCustomer().getSrvSom().send(userMessage.getJSON());
                }
                currChat.addMessage(userMessage); // Сохраняем историю сообшений
                server.log("Message from agent " + userMessage.getUser() + " to customer " + currChat.getCustomer() + ": " +
                        userMessage.getMessage() + ". Status: " + userMessage.getStatus());
            }
        }
        return true;
    }

    @Override
    public void run() {
        String word;
        try {

            while (true) {
                word = in.readLine();
                if (word != null) {
                    Message userMessage = Message.decodeJSON(word);

                    if (userMessage.getUser().getUserType() == UsersTypes.CUSTOMER) { // На сервер написал клиент
                        if (!usersHandler(userMessage)) {
                            break;
                        }
                    } else if (userMessage.getUser().getUserType() == UsersTypes.AGENT) { // На сервер написал агент
                        if (!agentsHandler(userMessage)) {
                            break;
                        }
                    }
                    // System.out.println(word);

                }
            }

        } catch (IOException e) {
        }
    }

    /**
     * Отправка сообщения клиенту
     * @param msg JSON сообщения
     */
    synchronized void send(String msg) {
        try {
            out.write(msg + "\n");
            out.flush();
        } catch (IOException ignored) {}
    }

    /**
     * Отправка сообщения клиенту
     * @param msg Объект сообщения
     */
    void send(Message msg) {
        send(msg.getJSON());
    }

    /**
     * Отправка сообщения от имени сервера
     * @param strMsg Сообщение
     * @param status Статус (по умолчанию "ok")
     */
    void serverSend(String strMsg, String status) {
        Message message = new Message(new User("Server"),strMsg, status);
        send(message.getJSON());
    }

    /**
     * Отправка сообщения от имени сервера
     * @param strMsg Сообщение
     */
    void serverSend(String strMsg) {
        serverSend(strMsg, "ok");
    }
}