package men.brakh.server;

import men.brakh.chat.Message;

import java.io.BufferedWriter;

public interface Sender {
    void send(String msg);
    default void send(Message msg) {
        send(msg.getJSON());
    }
    default void serverSend(String msg, String status) {
        throw new RuntimeException("serverSend don't have realisation");
    }
    default void serverSend(String msg) {
        serverSend(msg, "ok");
    }
}
