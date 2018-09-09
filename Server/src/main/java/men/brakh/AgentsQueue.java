package men.brakh;

import men.brakh.chat.User;

import java.util.ArrayDeque;

/**
 * Очередь агентов
 */
public class AgentsQueue {
    private ArrayDeque<ExtendUser> queue = new ArrayDeque<ExtendUser>();

    /**
     * Добавление нового агента в очередь
     * @param user Объект агента
     * @param socket Объект соединения с пользователем
     */
    public void add(User user, ServerSomthing socket) {
        queue.addLast(new ExtendUser(user, socket));
    }

    /**
     * Добавление нового агента в очередь
     * @param user Расширенный объект агента (Объект агента + объект соединения)
     */
    public void add(ExtendUser user) {
        queue.addLast(user);
    }

    /**
     * Получечение первого агета в очереди (без удаления из очереди)
     * @return объкт агента
     */
    public ExtendUser getFirst() {
        return queue.peekFirst();
    }

    /**
     * Поиск агента в очереди
     * @param agent Объект агента
     * @return Объект агента если он найден, иначе - null
     */
    public User searchAgent(User agent) {
        for (ExtendUser currAgent : queue) {
            if (currAgent.getUser().equal(agent)) {
                return currAgent.getUser();
            }
        }
        return null;
    }

    /**
     * Удаление агента из списка
     * @param agent Объект агента
     */
    public void remove(User agent) {
        for (ExtendUser currAgent : queue) {
            if (currAgent.getUser().equal(agent)) {
                queue.remove(currAgent);
            }
        }
    }

    /**
     * Получечение первого агета в очереди (с удалением из очереди)
     * @return объкт агента
     */
    public ExtendUser poll() {
        return queue.pollFirst();
    }
}
