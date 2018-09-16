package men.brakh.customer;

import men.brakh.chat.User;
import men.brakh.chat.UsersTypes;

/**
 * Класс пользователя
 */
public class Customer extends User {
    public Customer(String name) {
        super(name);
        setUserType(UsersTypes.CUSTOMER);
    }
}
