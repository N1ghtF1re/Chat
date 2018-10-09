package men.brakh.customer;

import men.brakh.chat.Message;
import men.brakh.chat.User;
import men.brakh.chat.Client;
import java.io.IOException;

/**
 * Класс клиента пользователя
 */
public class CustomerClient extends Client {

    final static String ip = "localhost";
    public static int port = 7777;

    public CustomerClient() {
        super();
    }


    /**
     * Обработка сообщения пользователя
     * @param message Объект сообщения
     */
    public void checkAnswer(Message message) {
        String strMessage = message.getMessage(); // Текст сообщения
        if ((strMessage.length() != 0) && (strMessage.charAt(0) == '!')) { // Команды начинаются с "!"
            String[] words = strMessage.split(" "); // Разделяем команду на слова
            if ((getUser() == null) && (words.length == 2) && (words[0].equals("!register"))) { // Регистрация пользователя
                registerUser(words[1]);
                sendMessage(new Message(getUser(), "", "reg"));
                showMessage(new User("System"), "Hello, " + getUser());
            } else if (getUser() != null) {
                if(words[0].equals("!exit")) { // Выход из системы
                    quit();
                } else if(words[0].equals("!leave")) { // Отключение от чата
                    sendMessage(new Message(getUser(), "", "leave"));
                    log("User Leave");
                }
            }
        } else if (getUser() != null) {  // Если это команда - просто отправляем сообщение собеседнику
            sendMessage(message);
            showMessage(message.getUser(), message.getMessage()); // Отображаем отправленное сообщение
        }
    }


    /**
     * Регистрация пользователя в клиенте
     * @param username Имя пользователя
     */
    @Override
    public void registerUser(String username) {
        log("Customer " + username + " registered in system");
        setUser(new Customer(username));
    }


    public static void main(String args[]) {
        new CustomerClient();
    }

}
