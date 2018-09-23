package men.brakh.server.commands.impl.customer;

import men.brakh.chat.Message;
import men.brakh.server.Sender;
import men.brakh.server.Server;
import men.brakh.server.commands.Command;
import men.brakh.server.data.TwoPersonChat;
import men.brakh.server.queues.CustomerChatQueue;

public class CustomerSendCommand extends Command {
    public CustomerSendCommand(Server server, Message message, Sender sender) {
        super(server, message, sender);
    }

    @Override
    public void execute() {
        CustomerChatQueue chat = server.customerChatQueue; // Очередь чатов
        if (chat.searchCustomer(message.getUser()) == null) { // Если в очереди чатов еще нет этого пользователя => создаем чат
            chat.add(message.getUser(), sender);
            server.log("Customer " + message.getUser() +" awaiting response. Message: " + message.getMessage());

            TwoPersonChat userchat = server.customerChatQueue.searchCustomer(message.getUser());
            userchat.addMessage(message);

            String msg = "Ваш запрос принят. Ожидайте подключения специалиста";
            sender.serverSend(msg); // отослать принятое сообщение с
            server.checkFreeAgents();
        } else { // У пользователя уже есть созданный чат
            TwoPersonChat currChat = chat.searchCustomer(message.getUser()); // Получаем текущий чат
            if (currChat.getAgent() != null) { // Если в чате уже есть агент => отправляем ему
                currChat.getAgent().getSender().send(message.getJSON());
            }
            currChat.addMessage(message); // Сохраняем историю сообшений
            server.log("Message from customer " + message.getUser() + " to agent " + currChat.getAgent() + ": " +
                    message.getMessage() + ". Status: " + message.getStatus());
        }
    }
}
