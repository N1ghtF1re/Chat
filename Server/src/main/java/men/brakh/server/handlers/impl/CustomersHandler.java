package men.brakh.server.handlers.impl;

import men.brakh.chat.Message;
import men.brakh.server.handlers.Handler;
import men.brakh.server.senders.Sender;
import men.brakh.server.Server;
import men.brakh.server.commands.CustomerCommandsInvoker;
import men.brakh.server.commands.impl.customer.CustomerExitCommand;
import men.brakh.server.commands.impl.customer.CustomerLeaveCommand;
import men.brakh.server.commands.impl.customer.CustomerRegCommand;
import men.brakh.server.commands.impl.customer.CustomerSendCommand;

/**
 * Обработчик сообщений клиента
 */
public class CustomersHandler implements Handler {
    private Server server;
    private Sender sender;

    public CustomersHandler(Server server, Sender sender) {
        this.server = server;
        this.sender = sender;
    }

    @Override
    public boolean handle(Message userMessage) {
        CustomerCommandsInvoker invoker = new CustomerCommandsInvoker(
                new CustomerRegCommand(server, userMessage, sender),
                new CustomerLeaveCommand(server,userMessage,sender),
                new CustomerExitCommand(server,userMessage,sender),
                new CustomerSendCommand(server, userMessage, sender)
        );

        try {
            invoker.executeComand(userMessage.getStatus());
        } catch (Exception e) {
            server.log(e);
        }


        if (userMessage.getStatus().equals("exit")) {
            return false;
        }

        return true;
    }
}
