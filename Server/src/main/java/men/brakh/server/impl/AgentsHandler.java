package men.brakh.server.impl;

import men.brakh.chat.Message;
import men.brakh.server.Sender;
import men.brakh.server.Handler;
import men.brakh.server.Server;
import men.brakh.server.commands.AgentCommandsInvoker;
import men.brakh.server.commands.impl.agent.AgentSendCommand;
import men.brakh.server.commands.impl.agent.AgentExitCommand;
import men.brakh.server.commands.impl.agent.AgentRegCommand;
import men.brakh.server.commands.impl.agent.AgentSkipCommand;

/**
 * Обработчик сообщений агента
 */
public class AgentsHandler implements Handler {
    Server server;
    Sender sender;

    public AgentsHandler(Server server, Sender sender) {
        this.server = server;
        this.sender = sender;
    }



    @Override
    public boolean handle(Message userMessage) {
        AgentCommandsInvoker invoker = new AgentCommandsInvoker(
                new AgentRegCommand(server, userMessage, sender),
                new AgentSkipCommand(server,userMessage,sender),
                new AgentExitCommand(server,userMessage,sender),
                new AgentSendCommand(server, userMessage, sender)
        );

        try {
            invoker.executeComand(userMessage.getStatus());
        } catch (Exception e) {
            server.log(e);
        }

        if(userMessage.getStatus().equals("exit")) {
            return false;
        }
        return true;
    }
}
