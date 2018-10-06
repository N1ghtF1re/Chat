package men.brakh.server.handlers;

import men.brakh.chat.Message;
import men.brakh.chat.UsersTypes;
import men.brakh.server.handlers.impl.AgentsHandler;
import men.brakh.server.handlers.impl.CustomersHandler;
import men.brakh.server.senders.Sender;
import men.brakh.server.Server;
import men.brakh.server.senders.impl.WebSender;

import javax.websocket.Session;

/**
 * Обработка сообщений в отдельном потоке (для вебсоектов)
 */
public class MessagesHandler {
    private Message message;

    private AgentsHandler agentsHandler;
    private CustomersHandler customersHandler;

    public MessagesHandler(Message message, Session session, Server server) {
        agentsHandler = new AgentsHandler(server, new WebSender(session));
        customersHandler = new CustomersHandler(server, new WebSender(session));
        if (message.getUser().getUserType() == UsersTypes.CUSTOMER) { // На сервер написал клиент
            customersHandler.handle(message);
        } else if (message.getUser().getUserType() == UsersTypes.AGENT) { // На сервер написал агент
            agentsHandler.handle(message);
        }
    }

}
