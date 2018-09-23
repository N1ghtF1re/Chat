package men.brakh.server.commands;

import java.util.HashMap;
import java.util.concurrent.Callable;

/**
 * Класс, вызывающий и обрабатывающий комманды клиента
 */
public class CustomerCommandsInvoker {
    private Command regCommand;
    private Command leaveCommand;
    private Command exitCommand;
    private Command sendCommand;

    // Текстовые комманды (статус сообщения) по умолчанию
    // для вызова той или инной функции команды
    private final String defaultRegStringCommand = "reg";
    private final String defaultLeaveStringCommand = "leave";
    private final String defaultExitStringCommand = "exit";
    private final String defaultSendStringCommand = "ok";

    // HASHMAP сопоставления текстовой команды и функции, которую необходимо вызывать
    HashMap<String, Callable> stringCommands = new HashMap<>();

    /**
     * Создание объекта обработки комманд
     * @param regCommand Объект комманды регистрации (status "reg")
     * @param leaveCommand Объект комманды отключения от агента (status "leave")
     * @param exitCommand Объект команды выхода из чата (status "exit")
     * @param sendCommand Объект команды отправки сообщения (status "ok")
     */
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

    /**
     * Изменение таблицы команд (В таблице текстовой команде сопоставялется функция, которую нужно вызвать)
     * @param regStringCommand Текстовая команда регистрации
     * @param leaveStringCommand Текстовая команда отключения от агента
     * @param exitStringCommand Текстовая команда выхода из чата
     * @param okStringCommand Текстовая команда отправки сообщения
     */
    public void setCommandsMap(String regStringCommand, String leaveStringCommand, String exitStringCommand, String okStringCommand) {
        stringCommands.put(regStringCommand, () -> reg());
        stringCommands.put(leaveStringCommand, () -> leave());
        stringCommands.put(exitStringCommand, () -> exit());
        stringCommands.put(okStringCommand, () -> send());
    }

    /**
     * Поиск в таблице команды и исполнение ее
     * @param command Текстовая команда
     * @throws Exception
     */
    public void executeComand(String command) throws Exception {
        stringCommands.get(command).call();
    }

    /**
     * Регистрация клиента
     * @return null
     */
    public Object reg() {
        regCommand.execute();
        return null;
    }

    /**
     * Отключение от агента
     * @return null
     */
    public Object leave() {
        leaveCommand.execute();
        return null;
    }

    /**
     * Выход из чата
     * @return null
     */
    public Object exit() {
        exitCommand.execute();
        return null;
    }

    /**
     * Отправка сообщения
     * @return null
     */
    public Object send() {
        sendCommand.execute();
        return null;
    }
}
