package men.brakh.server.data;

import men.brakh.server.ServerSomthing;
import men.brakh.chat.Message;
import men.brakh.chat.User;

import java.util.ArrayList;

public class TwoPersonChat {
    private men.brakh.server.data.ExtendUser customer;
    private men.brakh.server.data.ExtendUser agent;
    private ArrayList<Message> messages = new ArrayList<Message>();

    public TwoPersonChat(User customer, ServerSomthing srvSmth) {
        this.customer = new men.brakh.server.data.ExtendUser(customer, srvSmth);
        this.agent = null;
    }

    public men.brakh.server.data.ExtendUser getCustomer() {
        return customer;

    }

    public men.brakh.server.data.ExtendUser getAgent() {
        return agent;
    }

    public void addMessage(Message message) {
        messages.add(message);
    }
    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setAgent(men.brakh.server.data.ExtendUser agent) {
        this.agent = agent;
    }

}
