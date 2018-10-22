package men.brakh.server.queues;

import men.brakh.chat.UsersTypes;
import men.brakh.server.senders.Sender;
import men.brakh.chat.User;
import men.brakh.server.data.TwoPersonChat;

import java.util.ArrayDeque;

/**
 * Очередь чатов пользователей
 */
public class CustomerChatQueue {
    private ArrayDeque<TwoPersonChat> queue = new ArrayDeque<TwoPersonChat>();
    private int currId = 0;

    synchronized int getCurrId() {
        return ++currId;
    }


    public void add(User user, Sender sender) {
        user.setUserType(UsersTypes.CUSTOMER);
        queue.addLast(new TwoPersonChat(user, sender, getCurrId()));
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

    public TwoPersonChat searchCustomer(int id) {
        for (TwoPersonChat chat : queue) {
            if (chat.getCustomer().getUser().getId() == id) {
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

    public TwoPersonChat searchAgent(int id) {
        for (TwoPersonChat chat : queue) {
            if (chat.getAgent() != null) {
                if (chat.getAgent().getUser().getId() == id) {
                    return chat;
                }
            }
        }
        return null;
    }

    public TwoPersonChat getById(int id) {
        for (TwoPersonChat chat : queue) {
            if(chat.getId() == id) {
                return chat;
            }
        }
        return null;
    }

    public TwoPersonChat[] getAll() {
        TwoPersonChat[] result = new TwoPersonChat[queue.size()];
        result = queue.toArray(result);
        return result;
    }

    public void remove(TwoPersonChat chat) {
        queue.remove(chat);
    }
}
