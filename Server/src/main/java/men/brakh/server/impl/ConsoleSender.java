package men.brakh.server.impl;

import men.brakh.chat.Message;
import men.brakh.chat.User;
import men.brakh.server.Sender;

import java.io.BufferedWriter;
import java.io.IOException;

public class ConsoleSender implements Sender {
    private BufferedWriter out; // поток записи в сокет

    public ConsoleSender(BufferedWriter out) {
        this.out = out;
    }

    @Override
    public void send(String msg) {
        try {
            out.write(msg + "\n");
            out.flush();
        } catch (IOException ignored) {}
    }

    @Override
    public void serverSend(String msg, String status) {
        Message message = new Message(new User("Server"),msg, status);
        send(message.getJSON());
    }
}
