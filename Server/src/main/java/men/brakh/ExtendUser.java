package men.brakh;

import men.brakh.chat.User;

class ExtendUser {
    private User user;
    private ServerSomthing srvSom;
    ExtendUser(User user, ServerSomthing srvSom) {
        this.user = user;
        this.srvSom = srvSom;
    }
    public User getUser() {
        return user;
    }
    public ServerSomthing getSrvSom() {
        return srvSom;
    }
}