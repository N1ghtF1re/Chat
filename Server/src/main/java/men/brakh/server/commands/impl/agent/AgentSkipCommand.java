package men.brakh.server.commands.impl.agent;

import men.brakh.chat.Message;
import men.brakh.server.senders.Sender;
import men.brakh.server.Server;
import men.brakh.server.commands.Command;

public class AgentSkipCommand extends Command {
    public AgentSkipCommand(Server server, Message message, Sender sender) {
        super(server, message, sender);
    }

    @Override
    public void execute() {
        sender.serverSend("Вы отключились от пользователя.");
        synchronized (server.agentsQueue) {
            server.removeAgentFromChat(message.getChatId());
        }
        server.log("Agent " + message.getUser() + " skip customer");
        server.checkFreeAgents();
    }
}
