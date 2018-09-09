package men.brakh;

import men.brakh.chat.User;

import java.util.ArrayDeque;


public class AgentsQueue {
    private ArrayDeque<ExtendUser> queue = new ArrayDeque<ExtendUser>();

    public void add(User user, ServerSomthing socket) {
        queue.addLast(new ExtendUser(user, socket));
    }
    public void add(ExtendUser user) {
        queue.addLast(user);
    }
    public ExtendUser getFirst() {
        return queue.peekFirst();
    }
    public User searchAgent(User agent) {
        for (ExtendUser currAgent : queue) {
            if (currAgent.getUser().equal(agent)) {
                return currAgent.getUser();
            }
        }
        return null;
    }
    public ExtendUser poll() {
        return queue.pollFirst();
    }
}
