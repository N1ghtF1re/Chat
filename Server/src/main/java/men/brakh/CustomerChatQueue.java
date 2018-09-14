package men.brakh;

import men.brakh.chat.User;

import java.util.ArrayDeque;

/**
 * Очередь чатов пользователей
 */
public class CustomerChatQueue {
    private ArrayDeque<TwoPersonChat> queue = new ArrayDeque<TwoPersonChat>();

    public void add(User user, ServerSomthing srvSmth) {
        queue.addLast(new TwoPersonChat(user, srvSmth));
    }
    public TwoPersonChat getFirst() {
        if(queue.size() == 0) {
            return null;
        }
        return queue.getFirst();
    }

    public TwoPersonChat searchCustomer(User customer) {
        for (TwoPersonChat chat : queue) {
            if (chat.getCustomer().getUser().equal(customer)) {
                return chat;
            }
        }
        return null;
    }

    public TwoPersonChat getFree() {
        for (TwoPersonChat chat : queue) {
            if (chat.getAgent() == null) {
                return chat;
            }
        }
        return null;
    }

    public TwoPersonChat searchAgent(User agent) {
        for (TwoPersonChat chat : queue) {
            if (chat.getAgent() != null) {
                if (chat.getAgent().getUser().equal(agent)) {
                    return chat;
                }
            }
        }
        return null;
    }
    public void remove(TwoPersonChat chat) {
        queue.remove(chat);
    }
}
