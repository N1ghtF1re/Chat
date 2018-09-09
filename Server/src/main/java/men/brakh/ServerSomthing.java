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

    void removeCustomerChatElement(User user) {
        TwoPersonChat chat = server.customerChatQueue.searchCustomer(user);
        ExtendUser agent = chat.getAgent();
        chat.setAgent(null);

        server.customerChatQueue.remove(chat);
        server.agentsQueue.add(agent);

        agent.getSrvSom().send(new Message(new User("Server"), user.getName() + " disconnected").getJSON());
    }

    void usersHandler(Message userMessage) {

        if (userMessage.getStatus().equals("exit")) {
            send(new Message(new User("System"), "Вы отключились от сервера").getJSON());
            removeCustomerChatElement(userMessage.getUser());
        }

        CustomerChatQueue chat = server.customerChatQueue; // Очередь чатов
        if (chat.searchCustomer(userMessage.getUser()) == null) { // Если в очереди чатов еще нет этого пользователя => создаем чат
            chat.add(userMessage.getUser(), this);

            TwoPersonChat userchat = server.customerChatQueue.searchCustomer(userMessage.getUser());
            userchat.addMessage(userMessage);

            Message msg = new Message(new User("Server"), "Ваш запрос принят. Ожидайте подключения специалиста");
            this.send(msg.getJSON()); // отослать принятое сообщение с

            System.out.println("Added: " + userMessage.getUser());
        } else { // У пользователя уже есть созданный чат
            TwoPersonChat currChat = chat.searchCustomer(userMessage.getUser()); // Получаем текущий чат
            if (currChat.getAgent() != null) { // Если в чате уже есть агент => отправляем ему
                currChat.getAgent().getSrvSom().send(userMessage.getJSON());
            }
            currChat.addMessage(userMessage); // Сохраняем историю сообшений
        }
    }

    void agentsHandler(Message userMessage){
        if ((server.customerChatQueue.searchAgent(userMessage.getUser()) == null)  // Ищем агента в очередях
                && (server.agentsQueue.searchAgent(userMessage.getUser()) == null)){
            server.agentsQueue.add(userMessage.getUser(), this);
        } else {
            CustomerChatQueue chat = server.customerChatQueue;
            if (chat != null) {
                TwoPersonChat currChat = chat.searchAgent(userMessage.getUser());
                if (currChat == null) {
                    return;
                }
                if (!userMessage.getStatus().equals("ok")) {
                    return;
                }
                if (currChat.getCustomer() != null) {
                    currChat.getCustomer().getSrvSom().send(userMessage.getJSON());
                }
                currChat.addMessage(userMessage); // Сохраняем историю сообшений
            }
        }
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
                        usersHandler(userMessage);
                    } else if (userMessage.getUser().getUserType() == UsersTypes.AGENT) {
                        agentsHandler(userMessage);
                    }
                    System.out.println(word);

                }
            }

        } catch (IOException e) {
        }
    }

    synchronized void send(String msg) {
        try {
            out.write(msg + "\n");
            out.flush();
        } catch (IOException ignored) {}
    }
}