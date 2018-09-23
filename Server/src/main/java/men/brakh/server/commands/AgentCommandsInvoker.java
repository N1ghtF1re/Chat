package men.brakh.server.commands;

public class AgentCommandsInvoker {
    private Command regCommand;
    private Command skipCommand;
    private Command exitCommand;

    public AgentCommandsInvoker(Command regCommand, Command skipCommand, Command exitCommand) {
        this.regCommand = regCommand;
        this.skipCommand = skipCommand;
        this.exitCommand = exitCommand;
    }

    public void reg() {
        regCommand.execute();
    }

    public void skip() {
        skipCommand.execute();
    }

    public void exit() {
        exitCommand.execute();
    }
}
