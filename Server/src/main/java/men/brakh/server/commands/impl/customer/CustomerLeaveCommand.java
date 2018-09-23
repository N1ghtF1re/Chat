package men.brakh.server.commands.impl.customer;

import men.brakh.chat.Message;
import men.brakh.server.senders.Sender;
import men.brakh.server.Server;
import men.brakh.server.commands.Command;

public class CustomerLeaveCommand extends Command {
    public CustomerLeaveCommand(Server server, Message message, Sender sender) {
        super(server, message, sender);
    }

    @Override
    public void execute() {
        sender.serverSend("Вы отключились от агента. Чтобы подключиться к новому агенту - напишите сообщение в чат", "ok");
        server.removeCustomerChatElement(message.getUser()); // Освобождаем привязанного агента
        server.checkFreeAgents();
    }
}
