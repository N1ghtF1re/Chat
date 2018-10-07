package men.brakh.server.senders.impl;

import men.brakh.chat.Message;
import men.brakh.chat.User;
import men.brakh.server.senders.Sender;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * "Отправитель" сообщений консольному клиенту
 * На данный момент класс считается устаревшим всвязи с отказом от
 * использования сокетов и будет удален в ближайшее время.
 */
@Deprecated
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

}
