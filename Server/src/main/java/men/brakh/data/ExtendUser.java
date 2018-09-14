package men.brakh.data;

import men.brakh.ServerSomthing;
import men.brakh.chat.User;

/**
 * Расширенный объект пользователя
 * (User + ServerSomthing)
 */
public class ExtendUser {
    private User user;
    private ServerSomthing srvSom;
    public ExtendUser(User user, ServerSomthing srvSom) {
        this.user = user;
        this.srvSom = srvSom;
    }
    public User getUser() {
        return user;
    }
    public ServerSomthing getSrvSom() {
        return srvSom;
    }

    @Override
    public String toString() {
        return this.getUser().toString();
    }
}