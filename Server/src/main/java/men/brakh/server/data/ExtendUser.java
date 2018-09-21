package men.brakh.server.data;

import men.brakh.server.Sender;
import men.brakh.server.ServerSomthing;
import men.brakh.chat.User;

/**
 * Расширенный объект пользователя
 * (User + ServerSomthing)
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