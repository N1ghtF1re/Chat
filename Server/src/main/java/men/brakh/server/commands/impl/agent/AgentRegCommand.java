package men.brakh.server.commands.impl.agent;

import men.brakh.chat.Message;
import men.brakh.chat.User;
import men.brakh.server.senders.Sender;
import men.brakh.server.Server;
import men.brakh.server.commands.Command;

public class AgentRegCommand extends Command {

    public AgentRegCommand(Server server, Message message, Sender sender) {
        super(server, message, sender);
    }

    @Override
    public void execute() {
        server.log("New registration: " + message.getUser());
        int newId = server.getNewId();
        sender.serverSend(String.valueOf(newId), "reg");
        User agent = message.getUser();
        agent.setId(newId); // Меняем id с -1 на новый
        message.setUser(agent);
        server.checkFreeAgents();
    }
}
