package men.brakh;

import men.brakh.chat.Message;
import men.brakh.chat.User;

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

    @Override
    public void run() {
        String word;
        try {

            while (true) {
                word = in.readLine();
                if (word != null) {
                    Message userMessage = Message.decodeJSON(word);
                    if (server.customerChatQueue.searchAgent(userMessage.getUser()) == null) {
                        server.customerChatQueue.add(userMessage.getUser());
                        System.out.println("Added: " + userMessage.getUser());
                    }
                    System.out.println(word);
                    Message msg = new Message(new User("Server"), "Ответ сервера на " + userMessage.getMessage());
                    this.send(msg.getJSON()); // отослать принятое сообщение с
                }
            }

        } catch (IOException e) {
        }
    }

    synchronized  private void send(String msg) {
        try {
            out.write(msg + "\n");
            out.flush();
        } catch (IOException ignored) {}
    }
}