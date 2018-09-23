package men.brakh.server.data;

import men.brakh.server.senders.Sender;
import men.brakh.chat.User;

/**
 * Расширенный объект пользователя
 * (User + объект класса Sender(Отправка сообщения пользователю))
 */
public class ExtendUser {
    private User user;
    private Sender sender;
    public ExtendUser(User user, Sender sender) {
        this.user = user;
        this.sender = sender;
    }
    public User getUser() {
        return user;
    }
    public Sender getSender() {
        return sender;
    }

    @Override
    public String toString() {
        return this.getUser().toString();
    }
}