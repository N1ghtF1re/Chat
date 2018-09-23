package men.brakh.server.impl;

import men.brakh.chat.Message;
import men.brakh.server.Handler;
import men.brakh.server.Sender;
import men.brakh.server.Server;
import men.brakh.server.data.TwoPersonChat;
import men.brakh.server.queues.CustomerChatQueue;

/**
 * Обработчик сообщений клиента
 */
public class CustomersHandler implements Handler {
    Server server;
    Sender sender;

    public CustomersHandler(Server server, Sender sender) {
        this.server = server;
        this.sender = sender;
    }

    @Override
    public boolean handle(Message userMessage) {
        if (userMessage.getStatus().equals("exit")) { // Пользователь захотел отключиться
            try {
                sender.serverSend("Вы отключились от сервера", "exit");
            } catch (IllegalStateException ignore) {}
            server.removeCustomerChatElement(userMessage.getUser()); // Освобождаем привязанного агента
            server.checkFreeAgents();
            server.log(userMessage.getUser() + " has disconnected from the server");
            return false;
        } else if(userMessage.getStatus().equals("leave")) { // Пользователь захотел отключиться
            sender.serverSend("Вы отключились от агента. Чтобы подключиться к новому агенту - напишите сообщение в чат", "ok");
            server.removeCustomerChatElement(userMessage.getUser()); // Освобождаем привязанного агента
            server.checkFreeAgents();
            return true;
        } else if(userMessage.getStatus().equals("reg")) {
            server.log("New registration: " + userMessage.getUser());
            sender.serverSend(String.valueOf(server.getNewId()), "reg");
            return true;
        } else if(userMessage.getStatus().equals("reg")) {
            server.log("New registration: " + userMessage.getUser());
            sender.serverSend(String.valueOf(server.getNewId()), "reg");
            return true;
        }

        CustomerChatQueue chat = server.customerChatQueue; // Очередь чатов
        if (chat.searchCustomer(userMessage.getUser()) == null) { // Если в очереди чатов еще нет этого пользователя => создаем чат
            chat.add(userMessage.getUser(), sender);
            server.log("Customer " + userMessage.getUser() +" awaiting response. Message: " + userMessage.getMessage());

            TwoPersonChat userchat = server.customerChatQueue.searchCustomer(userMessage.getUser());
            userchat.addMessage(userMessage);

            String msg = "Ваш запрос принят. Ожидайте подключения специалиста";
            sender.serverSend(msg); // отослать принятое сообщение с
            server.checkFreeAgents();
        } else { // У пользователя уже есть созданный чат
            TwoPersonChat currChat = chat.searchCustomer(userMessage.getUser()); // Получаем текущий чат
            if (currChat.getAgent() != null) { // Если в чате уже есть агент => отправляем ему
                currChat.getAgent().getSender().send(userMessage.getJSON());
            }
            currChat.addMessage(userMessage); // Сохраняем историю сообшений
            server.log("Message from customer " + userMessage.getUser() + " to agent " + currChat.getAgent() + ": " +
                    userMessage.getMessage() + ". Status: " + userMessage.getStatus());
        }
        return true;
    }
}
