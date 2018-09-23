package men.brakh.server;

import men.brakh.chat.Message;
import men.brakh.chat.User;
import men.brakh.chat.UsersTypes;
import men.brakh.server.handlers.impl.AgentsHandler;
import men.brakh.server.senders.impl.ConsoleSender;
import men.brakh.server.handlers.impl.CustomersHandler;
import men.brakh.server.senders.Sender;

import java.io.*;
import java.net.Socket;
import java.util.ArrayDeque;

public class ServerSomthing extends Thread {

    private Socket socket; // сокет, через который сервер общается с клиентом,
    // кроме него - клиент и сервер никак не связаны
    private BufferedReader in; // поток чтения из сокета
    private BufferedWriter out; // поток записи в сокет
    private Server server;

    private Sender sender;
    private AgentsHandler agentsHandler;
    private CustomersHandler customersHandler;




    private ArrayDeque<User> agentsList = new ArrayDeque<User>();

    public ServerSomthing(Socket socket, Server server) throws IOException {
        this.socket = socket;
        // если потоку ввода/вывода приведут к генерированию исключения, оно проброситься дальше
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        sender = new ConsoleSender(out);
        this.server = server;

        agentsHandler = new AgentsHandler(server, sender);
        customersHandler = new CustomersHandler(server, sender);
    }

    public boolean usersHandler(Message msg) {
        return customersHandler.handle(msg);
    }

    public boolean agentsHandler(Message msg) {
        return agentsHandler.handle(msg);
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
                        if (!customersHandler.handle(userMessage)) {
                            break;
                        }
                    } else if (userMessage.getUser().getUserType() == UsersTypes.AGENT) { // На сервер написал агент
                        if (!agentsHandler.handle(userMessage)) {
                            break;
                        }
                    }

                }
            }

        } catch (IOException e) {
        }
    }

}