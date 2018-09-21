package men.brakh.server.impl;

import men.brakh.chat.Message;
import men.brakh.chat.User;
import men.brakh.server.Sender;
import men.brakh.server.Handler;
import men.brakh.server.Server;
import men.brakh.server.data.TwoPersonChat;
import men.brakh.server.queues.CustomerChatQueue;

public class AgentsHandler implements Handler {
    Server server;
    Sender sender;

    public AgentsHandler(Server server, Sender sender) {
        this.server = server;
        this.sender = sender;
    }



    @Override
    public boolean handle(Message userMessage) {
        if (userMessage.getStatus().equals("exit")) { // Агент выходит из ВСЕГО чата
            sender.serverSend("Вы отключились от сервера", "exit");
            synchronized (server.agentsQueue) {
                server.removeAgentFromChat(userMessage.getUser());
                server.removeAgentFromQueue(userMessage.getUser());
            }
            server.log("Agent " + userMessage.getUser() + " has disconnected from the server");
            server.checkFreeAgents();
            return false;
        }else if(userMessage.getStatus().equals("skip")) {
            sender.serverSend("Вы отключились от пользователя.");
            synchronized (server.agentsQueue) {
                server.removeAgentFromChat(userMessage.getUser());
            }
            server.log("Agent " + userMessage.getUser() + " skip customer");
            server.checkFreeAgents();
            return true;
        }else if(userMessage.getStatus().equals("reg")) {
            server.log("New registration: " + userMessage.getUser());
            int newId = server.getNewId();
            sender.serverSend(String.valueOf(newId), "reg");
            User agent = userMessage.getUser();
            agent.setId(newId); // Меняем id с -1 на новый
            userMessage.setUser(agent);

        }

        if ((server.customerChatQueue.searchAgent(userMessage.getUser()) == null)  // Ищем агента в очередях
                && (server.agentsQueue.searchAgent(userMessage.getUser()) == null)){
            server.agentsQueue.add(userMessage.getUser(), sender);
            server.log("Agent " + userMessage.getUser() + " added to the end of the queue");
            server.checkFreeAgents();
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
                    currChat.getCustomer().getSender().send(userMessage.getJSON());
                }
                currChat.addMessage(userMessage); // Сохраняем историю сообшений
                server.log("Message from agent " + userMessage.getUser() + " to customer " + currChat.getCustomer() + ": " +
                        userMessage.getMessage() + ". Status: " + userMessage.getStatus());
            }
        }
        return true;
    }
}
