package men.brakh;

import men.brakh.chat.UsersTypes;

public class Agent extends men.brakh.chat.User {
    public Agent(String name) {
        super(name);
        setUserType(UsersTypes.AGENT);
    }
}
