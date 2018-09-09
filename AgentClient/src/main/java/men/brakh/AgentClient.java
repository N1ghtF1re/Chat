package men.brakh;

import men.brakh.chat.Message;
import men.brakh.chat.User;

import java.io.IOException;

/**
 * Класс клиента агента поддержки
 */
public class AgentClient extends men.brakh.chat.Client{
    final static String ip = "localhost";
    public static int port = 7777;

    public AgentClient(String ip, int port) throws IOException {
        super(ip, port);
    }

    /**
     * Обработка сообщения пользователя
     * @param message Объект сообщения
     */
    public void checkAnswer(Message message) {
        String strMessage = message.getMessage();
        if ((strMessage.length() != 0) && (strMessage.charAt(0) == '!')) { // Команды начинаются с "!"
            String[] words = strMessage.split(" ");
            if ((words.length == 2) && (words[0].equals("!register"))) { // Регистрация агента в клиенте
                registerUser(words[1]);

                showMessage(new User("System"), "Hello, " + getUser());
            } else if (getUser() != null) {
                if(words[0].equals("!exit")) { // Выход из приложения
                    quit();
                } else if(words[0].equals("!skip")) { // Переключение на другого пользователя
                    try {
                        sendMessage(new Message(this.getUser(), "", "skip").getJSON());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (getUser() != null) { // Если это команда - просто отправляем сообщение собеседнику
            try {
                sendMessage(message.getJSON());
            } catch (IOException e) {
                e.printStackTrace();
            }
            showMessage(message.getUser(), message.getMessage()); // Отображение отправленного сообщения
        }
    }

    /**
     * Регистрация агента в клиенте
     * @param username
     */
    @Override
    public void registerUser(String username) {
        setUser(new Agent(username));
        try {
            sendMessage(new Message(getUser(), "", "reg").getJSON()); // Отправляем сообщение
            // Что агент открыт для "подключений" на сервер
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        try {
            new AgentClient(ip, port);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
