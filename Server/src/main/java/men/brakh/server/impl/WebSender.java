package men.brakh.server.impl;

import men.brakh.chat.Message;
import men.brakh.chat.User;
import men.brakh.server.Sender;

import javax.websocket.Session;
import java.io.IOException;

public class WebSender implements Sender {
    private Session session;

    WebSender(Session session) {
        this.session = session;
    }

    @Override
    public void send(String msg) {
        try {
            session.getBasicRemote().sendText(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void serverSend(String msg, String status) {
        Message message = new Message(new User("Server"),msg, status);
        send(message.getJSON());
    }
}
