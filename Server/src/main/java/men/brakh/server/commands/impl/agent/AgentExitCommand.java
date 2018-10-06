package men.brakh.server.commands.impl.agent;

import men.brakh.chat.Message;
import men.brakh.server.senders.Sender;
import men.brakh.server.Server;
import men.brakh.server.commands.Command;

public class AgentExitCommand extends Command {
    public AgentExitCommand(Server server, Message message, Sender sender) {
        super(server, message, sender);
    }

    @Override
    public void execute() {
        try {
            sender.serverSend("Вы отключились от сервера", "exit");
        } catch (IllegalStateException ignore) {}
        synchronized (server.agentsQueue) {
            server.removeAgentsFromAllChats(message.getUser()); // Удаляем агента из чата с пользователем
            server.removeAgentFromQueue(message.getUser()); // Удаляем агента из очереди
        }
        server.log("Agent " + message.getUser() + " has disconnected from the server");
        server.checkFreeAgents(); // Проверяем свободных агентов для пользователя, которого покинул агент
    }
}
