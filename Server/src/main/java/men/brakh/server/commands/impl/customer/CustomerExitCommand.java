package men.brakh.server.commands.impl.customer;

import men.brakh.chat.Message;
import men.brakh.server.senders.Sender;
import men.brakh.server.Server;
import men.brakh.server.commands.Command;

public class CustomerExitCommand extends Command {
    public CustomerExitCommand(Server server, Message message, Sender sender) {
        super(server, message, sender);
    }

    @Override
    public void execute() {
        try {
            sender.serverSend("Вы отключились от сервера", "exit");
        } catch (IllegalStateException ignore) {}
        server.removeCustomerChatElement(message.getUser()); // Освобождаем привязанного агента

        server.checkFreeAgents();
        server.log(message.getUser() + " has disconnected from the server");
    }
}
