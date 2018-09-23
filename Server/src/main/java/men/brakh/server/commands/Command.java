package men.brakh.server.commands;

import men.brakh.chat.Message;
import men.brakh.server.senders.Sender;
import men.brakh.server.Server;

/**
 * Абстрактный класс для
 * Комманд пользвателей чата
 */
public abstract class Command {
    protected Server server;
    protected Sender sender;
    protected Message message;

    public Command(Server server, Message message, Sender sender) {
        this.sender = sender;
        this.server = server;
        this.message = message;
    }

    public abstract void execute();
}
