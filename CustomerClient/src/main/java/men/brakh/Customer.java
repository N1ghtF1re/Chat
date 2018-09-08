package men.brakh;

import men.brakh.chat.User;
import men.brakh.chat.UsersTypes;

public class Customer extends User {
    public Customer(String name) {
        super(name);
        setUserType(UsersTypes.CUSTOMER);
    }
}
