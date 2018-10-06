package men.brakh.server.endpoints;

import men.brakh.chat.Message;
import men.brakh.chat.User;
import men.brakh.server.handlers.MessagesHandler;
import men.brakh.server.Server;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;


/**
 * Класс "endpoint" вебсокета, занимается
 * обработкой приходящих запросов
 */
@ServerEndpoint("/chat")
public class ChatEndpoint {
    public static Server server;

    private HashMap<Session, User> users = new HashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        server.log("Session opened, id: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        Message msg = Message.decodeJSON(message);
        users.put(session, msg.getUser());
        new MessagesHandler(msg, session, server);
    }

    @OnError
    public void onError(Throwable e) {
        server.log((Exception) e);
    }

    @OnClose
    public void onClose(Session session) {
        server.log("Session closed with id: " + session.getId());
        if(!users.containsKey(session)) {
            return;
        }
        Message msg = new Message(users.get(session), "", "exit");
        new MessagesHandler(msg, session, server);

    }

}