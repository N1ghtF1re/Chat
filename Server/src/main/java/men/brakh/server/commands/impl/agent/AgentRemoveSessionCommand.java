package men.brakh.server.commands.impl.agent;

import men.brakh.chat.Message;
import men.brakh.server.Server;
import men.brakh.server.commands.Command;
import men.brakh.server.senders.Sender;

public class AgentRemoveSessionCommand extends Command {
    public AgentRemoveSessionCommand(Server server, Message message, Sender sender) {
        super(server, message, sender);
    }

    @Override
    public void execute() {
        server.removeAgentFromChat(message.getChatId());
        server.agentsQueue.removeOneAgent(message.getUser());
    }
}
