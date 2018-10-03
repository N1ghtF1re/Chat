package men.brakh.server.commands.impl.agent;

import men.brakh.chat.Message;
import men.brakh.chat.User;
import men.brakh.server.senders.Sender;
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
        User agent = message.getUser();
        if (server.isAgentNew(agent)){
            // Агент - новый и не находится в чате с пользвателем и
            // не находится в очереди агентов => добавлеяем его в очередь
            // и ищем ему клиента
            server.agentsQueue.add(agent, sender);
            server.log("Agent " + agent + " added to the end of the queue");
            server.checkFreeAgents();
        } else {
            CustomerChatQueue chat = server.customerChatQueue;

            int chat_id = message.getChatId();

            if(chat_id == -1) return;

            TwoPersonChat currChat = chat.getById(chat_id); // Теперь поиск идет по id чата

            //@Deprecated: TwoPersonChat currChat = chat.searchAgent(message.getUser()); // Ищем чат с агентом

            if (currChat.getCustomer() != null) {
                // Отправляем сообщение собеседнику
                currChat.getCustomer().getSender().send(message.getJSON());
            }

            currChat.addMessage(message); // Сохраняем историю сообшений
            server.log(String.format("Message from agent %s to customer %s: %s. Status: %s.", message.getUser(),
                    currChat.getCustomer(), message.getMessage(), message.getStatus())
            );


        }
    }
}
