package men.brakh.agent;

import men.brakh.chat.UsersTypes;

/**
 * Класс объекта Агента
 */
public class Agent extends men.brakh.chat.User {
    public Agent(String name) {
        super(name);
        setUserType(UsersTypes.AGENT);
    }
}
