package men.brakh;

import men.brakh.chat.User;

public class TwoPersonChat {
    private User customer;
    private User agent;

    public TwoPersonChat(User customer) {
        this.customer = customer;
    }

    public User getCustomer() {
        return customer;
    }

    public User getAgent() {
        return agent;
    }

    public void setAgent(User agent) {
        this.agent = agent;
    }
}
