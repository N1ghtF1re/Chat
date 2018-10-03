package men.brakh.server.commands.impl.customer;

import men.brakh.chat.Message;
import men.brakh.server.senders.Sender;
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
            server.log(String.format("Customer %s awaiting response. Message: %s", message.getUser(), message.getMessage()));

            TwoPersonChat userchat = server.customerChatQueue.searchCustomer(message.getUser());
            userchat.addMessage(message);

            String msg = "Ваш запрос принят. Ожидайте подключения специалиста";
            sender.serverSend(msg);
            sender.serverSend(String.valueOf(userchat.getId()), "chat");
            server.checkFreeAgents(); // Пытаемся найти агента

        } else { // У пользователя уже есть созданный чат
            int chat_id = message.getChatId();

            if(chat_id == -1) return;

            TwoPersonChat currChat = chat.getById(chat_id);

            // @Deprecated: TwoPersonChat currChat = chat.searchCustomer(message.getUser()); // Получаем текущий чат
            if (currChat.getAgent() != null) { // Если в чате уже есть агент => отправляем ему
                currChat.getAgent().getSender().send(message.getJSON());
            }
            currChat.addMessage(message); // Сохраняем историю сообшений

            server.log(String.format("Message from customer %s to agent %s: %s. Status: %s.", message.getUser(),
                    currChat.getAgent(), message.getMessage(), message.getStatus())
            );

        }
    }
}
