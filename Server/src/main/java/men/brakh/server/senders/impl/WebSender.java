package men.brakh.server.senders.impl;

import men.brakh.chat.Message;
import men.brakh.chat.User;
import men.brakh.server.senders.Sender;

import javax.websocket.Session;
import java.io.IOException;

/**
 * "Отправитель" сообщений веб-клиенту
 */

public class WebSender implements Sender {
    private Session session;

    public WebSender(Session session) {
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

}
