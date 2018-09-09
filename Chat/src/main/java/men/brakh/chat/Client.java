/**
 * CHAT
 * @author Alexandr Pankratiew
 */

package men.brakh.chat;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;


/**
 * Класс-родитель клиентов чата
 */
public abstract class Client {

    private User user;

    private Socket clientSocket; // Сокет для общения
    private BufferedReader in; // Поток чтения из соекта
    private BufferedWriter out; // Поток записи в сокет

    private ReadMsg readThread;
    private WriteMsg writeThread;
    /**
     * Чтение сообщений с сервера в отдельном потоке
     */
    private class ReadMsg extends Thread {
        private Boolean isKilled;
        public ReadMsg() {
            isKilled = false;
            this.start();
        }
        public void kill() {
            isKilled = true;
            this.interrupt();
        }
        @Override
        public void run() {

            String str;
            try {
                while (!isKilled) {
                    str = in.readLine(); // ждем сообщения с сервера
                    checkServerResponse(str);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
            while (!isKilled) {
                String answer;
                Scanner scan = new Scanner(System.in);
                answer = scan.nextLine();

                checkAnswer(new Message(getUser(), answer));
                try {
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }
    }

    /**
     * Отправка сообщения на сервер
     * @param message - json сообщения
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException {
        out.write(message + "\n");
        out.flush();
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

        System.out.printf("[%s] %s%n", user, message);
    }


    public void killThreads() {
        readThread.kill();
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
        }
    }


    /**
     * Инициализация соекта клиента
     * @param ip IP сервера
     * @param port Порт сервера
     * @throws IOException
     */
    private void initSocket(String ip, int port) throws IOException {

        clientSocket = new Socket(ip, port);

        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
    }

    public void registerUser(String username) {
        this.user = new User(username);
    }

    /**
     * Создание объекта клиента
     * @param ip IP сервера
     * @param port Порт сервера
     * @throws IOException
     */
    public Client(String ip, int port) throws IOException {
        initSocket(ip, port);
        readThread = new ReadMsg();
        writeThread = new WriteMsg();


        try {
            readThread.join();
            writeThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void quit() {
        try {
            sendMessage(new Message(this.getUser(), "", "exit").getJSON());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
