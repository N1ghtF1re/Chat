package men.brakh.server;

import men.brakh.chat.Message;

public interface Handler {
    boolean handle(Message userMessage);
}
