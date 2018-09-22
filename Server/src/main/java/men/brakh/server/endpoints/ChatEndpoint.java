package men.brakh.server.endpoints;

import men.brakh.chat.Message;
import men.brakh.chat.User;
import men.brakh.server.HandlerThread;
import men.brakh.server.Server;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;

@ServerEndpoint("/chat")
public class ChatEndpoint {
    public static Server server;

    private HashMap<Session, User> users = new HashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        server.log("Session opened, id: " + session.getId());
        try {
            session.getBasicRemote().sendText(new Message(new User("Server"),  "Hi there, we are successfully connected.").getJSON());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        Message msg = Message.decodeJSON(message);
        users.put(session, msg.getUser());
        new HandlerThread(msg, session, server);
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
        new HandlerThread(msg, session, server);

    }

}