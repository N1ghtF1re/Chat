package men.brakh.server.commands.impl.customer;

import men.brakh.chat.Message;
import men.brakh.server.senders.Sender;
import men.brakh.server.Server;
import men.brakh.server.commands.Command;

public class CustomerRegCommand extends Command {

    public CustomerRegCommand(Server server, Message message, Sender sender) {
        super(server, message, sender);
    }

    @Override
    public void execute() {
        server.log("New registration: " + message.getUser());
        sender.serverSend(String.valueOf(server.getNewId()), "reg");
    }
}
