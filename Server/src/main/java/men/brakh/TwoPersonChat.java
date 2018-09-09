package men.brakh;

import men.brakh.chat.Message;
import men.brakh.chat.User;

import java.util.ArrayList;

public class TwoPersonChat {
    private ExtendUser customer;
    private ExtendUser agent;
    private ArrayList<Message> messages = new ArrayList<Message>();

    public TwoPersonChat(User customer, ServerSomthing srvSmth) {
        this.customer = new ExtendUser(customer, srvSmth);
        this.agent = null;
    }

    public ExtendUser getCustomer() {
        return customer;

    }

    public ExtendUser getAgent() {
        return agent;
    }

    public void addMessage(Message message) {
        messages.add(message);
    }
    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setAgent(ExtendUser agent) {
        this.agent = agent;
    }

}
