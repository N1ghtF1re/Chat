package men.brakh.server.senders.impl;

import men.brakh.chat.Message;
import men.brakh.server.senders.Sender;

import java.util.LinkedList;
import java.util.List;

public class JsonSender implements Sender {
    List<Message> messages = new LinkedList<>();

    @Override
    public void send(String msg) {
        messages.add(Message.decodeJSON(msg));
    }

    @Override
    public void send(Message msg) {
        messages.add(msg);
    }

    public Message getLast() {
        return messages.get(0);
    }
}
