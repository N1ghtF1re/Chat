package men.brakh.server;

import men.brakh.chat.Message;

/**
 * Интерфейс обработчика сообщений
 */
public interface Handler {
    boolean handle(Message userMessage);
}
