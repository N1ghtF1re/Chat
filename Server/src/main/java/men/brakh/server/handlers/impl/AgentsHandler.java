package men.brakh.server.handlers.impl;

import men.brakh.chat.Message;
import men.brakh.server.commands.impl.agent.*;
import men.brakh.server.handlers.Handler;
import men.brakh.server.senders.Sender;
import men.brakh.server.Server;
import men.brakh.server.commands.AgentCommandsInvoker;

/**
 * Обработчик сообщений агента
 */
public class AgentsHandler implements Handler {
    private Server server;
    private Sender sender;

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
                new AgentSendCommand(server, userMessage, sender),
                new AgentAddSessionCommand(server, userMessage, sender),
                new AgentRemoveSessionCommand(server, userMessage, sender)
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
