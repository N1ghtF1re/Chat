package men.brakh.server.endpoints;

import men.brakh.server.Server;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint("/chat")
public class ChatEndpoint {
    public static Server server;


    public ChatEndpoint() {
        System.out.println("class loaded " + this.getClass());
    }

    @OnOpen
    public void onOpen(Session session) {
        server.log("Session opened, id: %s%n" + session.getId());
        try {
            session.getBasicRemote().sendText("Hi there, we are successfully connected.");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.printf("Message received. Session id: %s Message: %s%n",
                session.getId(), message);
        try {
            session.getBasicRemote().sendText(String.format("We received your message: %s%n", message));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @OnError
    public void onError(Throwable e) {
        e.printStackTrace();
    }

    @OnClose
    public void onClose(Session session) {
        System.out.printf("Session closed with id: %s%n", session.getId());
    }

}