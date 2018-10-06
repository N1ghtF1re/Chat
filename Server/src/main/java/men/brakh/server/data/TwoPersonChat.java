package men.brakh.server.data;

import men.brakh.server.senders.Sender;
import men.brakh.chat.Message;
import men.brakh.chat.User;

import java.util.ArrayList;

/**
 * Класс "комнаты" на два человека - агента и клиента
 */
public class TwoPersonChat {
    private men.brakh.server.data.ExtendUser customer;
    private men.brakh.server.data.ExtendUser agent;
    private ArrayList<Message> messages = new ArrayList<Message>();
    private int id;

    public TwoPersonChat(User customer, Sender sender, int id) {
        this.customer = new men.brakh.server.data.ExtendUser(customer, sender);
        this.agent = null;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public men.brakh.server.data.ExtendUser getCustomer() {
        return customer;

    }

    public men.brakh.server.data.ExtendUser getAgent() {
        return agent;
    }

    public void addMessage(Message message) {
        message.setChat_id(id);
        messages.add(message);
    }
    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setAgent(men.brakh.server.data.ExtendUser agent) {
        this.agent = agent;
    }

}
