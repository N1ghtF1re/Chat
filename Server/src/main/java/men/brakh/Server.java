package men.brakh;

import men.brakh.chat.Message;
import men.brakh.chat.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class Server {

    public final int PORT = 7777;
    public LinkedList<ServerSomthing> serverList = new LinkedList<ServerSomthing>(); // список всех нитей

    public CustomerChatQueue customerChatQueue;
    public AgentsQueue agentsQueue;

    private class checkFreeAgents extends Thread {

        public checkFreeAgents() {
            this.start();
        }
        @Override
        public void run() {
            Timer timer = new Timer();

            timer.schedule( new TimerTask() {
                public void run() {
                    ExtendUser agent = agentsQueue.getFirst();
                    if (agent != null) {
                        TwoPersonChat twoPersonChat = customerChatQueue.getFree();
                        if (twoPersonChat != null) {
                            agent = agentsQueue.poll();
                            System.out.println(agent.getUser());
                            twoPersonChat.setAgent(agent);
                            twoPersonChat.getCustomer().getSrvSom().serverSend("К Вам подключился наш агент - " +
                                    agent.getUser() + ". Пожалуйста, не обижайте его.");
                            agent.getSrvSom().serverSend("Вы подключились к " + twoPersonChat.getCustomer().getUser());
                            ArrayList<Message> messages = twoPersonChat.getMessages();
                            for (Message msg : messages) {
                                agent.getSrvSom().send(msg.getJSON());
                            }

                        }
                    }
                }
            }, 0, 1000);
        }
    }


    public Server() throws IOException {
        customerChatQueue = new CustomerChatQueue();
        agentsQueue = new AgentsQueue();
        ServerSocket server = new ServerSocket(PORT);
        new checkFreeAgents();
        try {
            while (true) {
                // Блокируется до возникновения нового соединения:
                Socket socket = server.accept();
                try {
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