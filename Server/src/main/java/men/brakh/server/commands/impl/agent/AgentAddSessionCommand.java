package men.brakh.server.commands.impl.agent;

import men.brakh.chat.Message;
import men.brakh.server.Server;
import men.brakh.server.commands.Command;
import men.brakh.server.senders.Sender;

public class AgentAddSessionCommand extends Command {
    public AgentAddSessionCommand(Server server, Message message, Sender sender) {
        super(server, message, sender);
    }

    @Override
    public void execute() {
        server.agentsQueue.add(message.getUser(), sender);
        server.log("Agent " + message.getUser() + " added to the end of the queue");
        server.checkFreeAgents();
    }
}
