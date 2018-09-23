package men.brakh.server.commands.impl.agent;

import men.brakh.chat.Message;
import men.brakh.server.Sender;
import men.brakh.server.Server;
import men.brakh.server.commands.Command;
import men.brakh.server.data.TwoPersonChat;
import men.brakh.server.queues.CustomerChatQueue;

public class AgentSendCommand extends Command {
    public AgentSendCommand(Server server, Message message, Sender sender) {
        super(server, message, sender);
    }

    @Override
    public void execute() {
        if ((server.customerChatQueue.searchAgent(message.getUser()) == null)  // Ищем агента в очередях
                && (server.agentsQueue.searchAgent(message.getUser()) == null)){
            server.agentsQueue.add(message.getUser(), sender);
            server.log("Agent " + message.getUser() + " added to the end of the queue");
            server.checkFreeAgents();
        } else {
            CustomerChatQueue chat = server.customerChatQueue;
            if (chat != null) {
                TwoPersonChat currChat = chat.searchAgent(message.getUser());
                if (currChat == null) {
                    return;
                }
                if (!message.getStatus().equals("ok")) {
                    return;
                }
                if (currChat.getCustomer() != null) {
                    currChat.getCustomer().getSender().send(message.getJSON());
                }
                currChat.addMessage(message); // Сохраняем историю сообшений
                server.log("Message from agent " + message.getUser() + " to customer " + currChat.getCustomer() + ": " +
                        message.getMessage() + ". Status: " + message.getStatus());
            }
        }
    }
}
