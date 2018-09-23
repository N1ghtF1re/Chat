package men.brakh.server.commands;

import java.util.HashMap;
import java.util.concurrent.Callable;

public class AgentCommandsInvoker {
    private Command regCommand;
    private Command skipCommand;
    private Command exitCommand;
    private Command sendCommand;

    private final String defaultRegStringCommand = "reg";
    private final String defaultSkipStringCommand = "skip";
    private final String defaultExitStringCommand = "exit";
    private final String defaultSendStringCommand = "ok";

    HashMap<String, Callable> stringCommands = new HashMap<>();

    public AgentCommandsInvoker(Command regCommand, Command leaveCommand, Command exitCommand, Command sendCommand) {
        this.regCommand = regCommand;
        this.skipCommand = leaveCommand;
        this.exitCommand = exitCommand;
        this.sendCommand = sendCommand;
        setCommandsMap(
                defaultRegStringCommand,
                defaultSkipStringCommand,
                defaultExitStringCommand,
                defaultSendStringCommand
        );
    }

    public void setCommandsMap(String regStringCommand, String skipStringCommand, String exitStringCommand, String okStringCommand) {
        stringCommands.put(regStringCommand, () -> reg());
        stringCommands.put(skipStringCommand, () -> skip());
        stringCommands.put(exitStringCommand, () -> exit());
        stringCommands.put(okStringCommand, () -> send());
    }

    public void executeComand(String command) throws Exception {
        stringCommands.get(command).call();
    }

    public Object reg() {
        regCommand.execute();
        sendCommand.execute();
        return null;
    }

    public Object skip() {
        skipCommand.execute();
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
