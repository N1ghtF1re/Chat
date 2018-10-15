/**
 * CHAT
 * @author Alexandr Pankratiew
 */

package men.brakh.chat;

import men.brakh.logger.Logger;
import org.glassfish.tyrus.client.ClientManager;

import javax.websocket.DeploymentException;
import javax.websocket.Session;
import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;


/**
 * Класс-родитель клиентов чата
 */
public abstract class Client {

    private User user;

    private Session session;

    private WriteMsg writeThread;

    private int currChat = -1;

    private Logger logger;

    private String host = "localhost";
    private int port = 8081;


    /**
     * Пустой конструктор, хост и порт - по-умолчанию
     */
    public Client() {
        try {
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Начало работы клиента
     * @throws IOException
     */
    public void start() throws IOException {
        writeThread = new WriteMsg();

        logger = new Logger();
        log("Client is open");

        ChatClientEndpoint.client = this;
        ChatClientEndpoint.setLatch(new CountDownLatch(1));

        ClientManager client = ClientManager.createClient();
        try {
            client.connectToServer(ChatClientEndpoint.class, new URI("ws://"+host+":"+port+"/chat"));
            ChatClientEndpoint.getLatch().await();
            quit();


        } catch (DeploymentException | URISyntaxException | InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

    /**
     * Установка сессии вебсокета
     * @param session
     */
    public void setSession(Session session) {
        this.session = session;
    }



    /**
     * Ожидание ввода пользоваетлем в отдельном потоке
     */
    public class WriteMsg extends Thread {
        private Boolean isKilled;

        public WriteMsg() {
            this.isKilled = false;
            this.start();
        }
        public void kill() {
            isKilled = true;
            this.interrupt();

        }

        @Override
        public void run() {
            Scanner scan = null;
            while (!isKilled) {
                String answer;
                scan = new Scanner(System.in);
                answer = scan.nextLine();

                checkAnswer(new Message(getUser(), answer));

            }
            scan.close();

        }
    }

    /**
     * Отправка сообщения на сервер
     * @param message - json сообщения
     * @throws IOException
     */
    public void sendMessage(String message){
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log(e);
        }

    }

    /**
     * Отправка сообщения на сервер
     * @param message объект сообщения
     */
    public void sendMessage(Message message){
        message.setChat_id(currChat);
        String msg = message.getJSON();
        sendMessage(msg);

    }

    /**
     * Обработка сообщений пользоваетелей
     * @param message сообщение пользователя
     */
    public abstract void checkAnswer(Message message);

    /**
     * Отрисовка сообщения пользователя
     * @param user Объект пользователя
     * @param message Сообщение
     */
    public void showMessage(User user, String message) {

        log("Message from " + user + ": " + message);
        System.out.printf("[%s] %s%n", user, message);
    }

    /**
     * Убиваем потоки
     */
    public void killThreads() {
        writeThread.kill();
    }


    /**
     * Проверка ответа сервера (если статус - "ок", то все хорошо)
     * @param json - json ответа сервера
     */
    public void checkServerResponse(String json) {
        Message message;
        message = Message.decodeJSON(json);
        String status = message.getStatus();
        if ("ok".equals(status)) {
            showMessage(message.getUser(), message.getMessage());
        } else if ("exit".equals(status)) { // Сервер захотел прервать связь
            killThreads();
            showMessage(message.getUser(), message.getMessage());
            showMessage(new User("System"), "Connection closed");
        } else if("reg".equals(status)) {
            setUserId(Integer.parseInt(message.getMessage()));
        } else if("chat".equals(status)) {
            setCurrChat(Integer.parseInt(message.getMessage()));
        }
    }

    /**
     * Регистрация пользователя в клиенте
     * @param username Имя пользователя
     */
    public void registerUser(String username) {
        this.user = new User(username);
    }

    /**
     * Отключение пользователя
     */
    public void quit() {
        log("Client is closed");
        sendMessage(new Message(this.getUser(), "", "exit").getJSON());
    }

    // GETTERS AND SETTERS
    public void setUserId(int id) {
        user.setId(id);
    }


    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public void setCurrChat(int currChat) {
        this.currChat = currChat;
    }

    public int getCurrChat() {
        return currChat;
    }

    /**
     * Записываем в лог сообщение
     * @param message сообщение
     */
    public void log(String message) {
        try {
            logger.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Записываем в лог ошибку
     * @param e Объект исключения
     */
    public void log(Exception e) {
        try {
            logger.write("[ERROR] RECEIVED EXCEPTION: " + e.toString() + "\nStackTrace: " + e.getStackTrace());
        } catch (IOException e2) {
            e.printStackTrace();
        }
    }

}
