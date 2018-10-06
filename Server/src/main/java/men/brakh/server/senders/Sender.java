package men.brakh.server.senders;

import men.brakh.chat.Message;
import men.brakh.chat.User;

/**
 * Интерфейс "отправителя" сообщений клиенту
 */

public interface Sender {
    void send(String msg);
    default void send(Message msg) {
        send(msg.getJSON());
    }
    default void serverSend(String msg, String status) {
        Message message = new Message(new User("Server"),msg, status);
        send(message.getJSON());
    }
    default void serverSend(String msg, String status, int chat_id) {
        Message message = new Message(new User("Server"), msg, status, chat_id);
        send(message.getJSON());
    }
    default void serverSend(String msg, int chat_id) {
        Message message = new Message(new User("Server"),msg, chat_id);
        send(message.getJSON());
    }
    default void serverSend(String msg) {
        serverSend(msg, "ok");
    }
}
