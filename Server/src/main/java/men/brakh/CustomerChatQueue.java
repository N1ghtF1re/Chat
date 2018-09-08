package men.brakh;

import men.brakh.chat.User;

import java.util.ArrayDeque;

public class CustomerChatQueue {
    private ArrayDeque<TwoPersonChat> queue = new ArrayDeque<TwoPersonChat>();

    public void add(User user) {
        queue.addLast(new TwoPersonChat(user));
    }
    public TwoPersonChat getFirst() {
        return queue.getFirst();
    }

    public TwoPersonChat searchCustomer(User customer) {
        for (TwoPersonChat chat : queue) {
            if (chat.getCustomer().equal(customer)) {
                return chat;
            }
        }
        return null;
    }
    public TwoPersonChat searchAgent(User agent) {
        for (TwoPersonChat chat : queue) {
            if (chat.getCustomer().equal(agent)) {
                return chat;
            }
        }
        return null;
    }
}
