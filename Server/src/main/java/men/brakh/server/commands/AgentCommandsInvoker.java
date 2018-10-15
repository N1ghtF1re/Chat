package men.brakh.server.commands;

import java.util.HashMap;
import java.util.concurrent.Callable;


/**
 * Класс, вызывающий и обрабатывающий комманды агента
 */
public class AgentCommandsInvoker {
    private Command regCommand;
    private Command skipCommand;
    private Command exitCommand;
    private Command sendCommand;
    private Command addSessionCommand;
    private Command rmSessionCommand;

    // Текстовые комманды (статус сообщения) по умолчанию
    // для вызова той или инной функции команды
    private final String defaultRegStringCommand = "reg";
    private final String defaultSkipStringCommand = "skip";
    private final String defaultExitStringCommand = "exit";
    private final String defaultSendStringCommand = "ok";
    private final String defaultAddSessionStringCommand = "add-session";
    private final String defaultRmSessionStringCommand = "rm-session";

    // HASHMAP сопоставления текстовой команды и функции, которую необходимо вызывать
    HashMap<String, Callable> stringCommands = new HashMap<>();

    /**
     * Создание объекта обработки комманд
     * @param regCommand Объект комманды регистрации (status "reg")
     * @param skipCommand Объект комманды "пропуска" клиента (status "skip")
     * @param exitCommand Объект команды выхода из чата (status "exit")
     * @param sendCommand Объект команды отправки сообщения (status "ok")
     * @param addSessionCommand Объект команды создания новой "сессии", для общения с несколькими клиентами
     *                          (status: "add-session")
     * @param rmSessionCommand Объект команды удаления новой "сессии", для общения с несколькими клиентами
     *                         (status: "rm-session")
     */
    public AgentCommandsInvoker(Command regCommand, Command skipCommand, Command exitCommand,
                                Command sendCommand, Command addSessionCommand, Command rmSessionCommand) {
        this.regCommand = regCommand;
        this.skipCommand = skipCommand;
        this.exitCommand = exitCommand;
        this.sendCommand = sendCommand;
        this.addSessionCommand = addSessionCommand;
        this.rmSessionCommand = rmSessionCommand;
        setCommandsMap(
                defaultRegStringCommand,
                defaultSkipStringCommand,
                defaultExitStringCommand,
                defaultSendStringCommand,
                defaultAddSessionStringCommand,
                defaultRmSessionStringCommand
        );
    }

    /**
     * Изменение таблицы команд (В таблице текстовой команде сопоставялется функция, которую нужно вызвать)
     * @param regStringCommand Текстовая команда регистрации
     * @param skipStringCommand Текстовая команда "пропуска" клиента
     * @param exitStringCommand Текстовая команда выхода из чата
     * @param okStringCommand Текстовая команда отправки сообщения
     * @param addSessionStringCommand Текстовая команда создания новой "сессии" (для общения с несколькими клиентами)
     * @param rmSessionStringCommand Текстовая команда удаления новой "сессии" (для общения с несколькими клиентами)
     */
    public void setCommandsMap(String regStringCommand, String skipStringCommand,
                               String exitStringCommand, String okStringCommand,
                               String addSessionStringCommand, String rmSessionStringCommand) {
        stringCommands.put(regStringCommand, () -> reg());
        stringCommands.put(skipStringCommand, () -> skip());
        stringCommands.put(exitStringCommand, () -> exit());
        stringCommands.put(okStringCommand, () -> send());
        stringCommands.put(addSessionStringCommand, () -> addSession());
        stringCommands.put(rmSessionStringCommand, () -> removeSession());
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
     * Регистрация агента
     * @return null
     */
    public Object reg() {
        regCommand.execute();
        sendCommand.execute();
        return null;
    }

    /**
     * Пропуск клиента
     * @return null
     */
    public Object skip() {
        skipCommand.execute();
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

    /**
     * Создания новой "сессии" агента (для общения с несколькими пользователями)
     * @return null
     */
    public Object addSession() {
        addSessionCommand.execute();
        return null;
    }

    /**
     * Удаление "сессии" агента
     * @return
     */
    public Object removeSession() {
        rmSessionCommand.execute();
        return null;
    }
}
