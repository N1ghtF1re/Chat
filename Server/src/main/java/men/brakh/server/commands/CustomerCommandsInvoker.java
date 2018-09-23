package men.brakh.server.commands;

import java.util.HashMap;
import java.util.concurrent.Callable;

public class CustomerCommandsInvoker {
    private Command regCommand;
    private Command leaveCommand;
    private Command exitCommand;
    private Command sendCommand;

    private final String defaultRegStringCommand = "reg";
    private final String defaultLeaveStringCommand = "leave";
    private final String defaultExitStringCommand = "exit";
    private final String defaultSendStringCommand = "ok";

    HashMap<String, Callable> stringCommands = new HashMap<>();

    public CustomerCommandsInvoker(Command regCommand, Command leaveCommand, Command exitCommand, Command sendCommand) {
        this.regCommand = regCommand;
        this.leaveCommand = leaveCommand;
        this.exitCommand = exitCommand;
        this.sendCommand = sendCommand;
        setCommandsMap(
                defaultRegStringCommand,
                defaultLeaveStringCommand,
                defaultExitStringCommand,
                defaultSendStringCommand
        );
    }

    public void setCommandsMap(String regStringCommand, String leaveStringCommand, String exitStringCommand, String okStringCommand) {
        stringCommands.put(regStringCommand, () -> reg());
        stringCommands.put(leaveStringCommand, () -> leave());
        stringCommands.put(exitStringCommand, () -> exit());
        stringCommands.put(okStringCommand, () -> send());
    }

    public void executeComand(String command) throws Exception {
        stringCommands.get(command).call();
    }

    public Object reg() {
        regCommand.execute();
        return null;
    }

    public Object leave() {
        leaveCommand.execute();
        return null;
    }

    public Object exit() {
        exitCommand.execute();
        return null;
    }
    public Object send() {
        sendCommand.execute();
        return null;
    }
}
