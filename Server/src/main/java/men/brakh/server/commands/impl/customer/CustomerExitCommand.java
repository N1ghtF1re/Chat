package men.brakh.server.commands.impl.customer;

import men.brakh.chat.Message;
import men.brakh.server.Sender;
import men.brakh.server.Server;
import men.brakh.server.commands.Command;

public class CustomerExitCommand extends Command {
    public CustomerExitCommand(Server server, Message message, Sender sender) {
        super(server, message, sender);
    }

    @Override
    public void execute() {
        server.removeCustomerChatElement(message.getUser()); // Освобождаем привязанного агента
        server.checkFreeAgents();
        server.log(message.getUser() + " has disconnected from the server");
    }
}
