/**
 * CHAT
 * @author Alexandr Pankratiew
 */

package men.brakh.chat;

import men.brakh.logger.Logger;

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

    private Logger logger;


    /**
     * Начало работы клиента
     * @param socket Socket
     * @throws IOException
     */
    public void start(Socket socket) throws IOException {
        clientSocket = socket;

        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

        readThread = new ReadMsg();
        writeThread = new WriteMsg();

        logger = new Logger();
        log("Client is open");
    }
    public void join() {
        try {
            readThread.join();
            writeThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Создание объекта клиента
     * @param ip IP сервера
     * @param port Порт сервера
     * @throws IOException
     */
    public Client(String ip, int port) throws IOException {
        Socket socket = new Socket(ip, port);
        start(socket);
    }
    public Client() {

    }


    public void log(String message) {
        try {
            logger.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void log(Exception e) {
        try {
            logger.write("[ERROR] RECEIVED EXCEPTION: " + e.toString() + "\nStackTrace: " + e.getStackTrace());
        } catch (IOException e2) {
            e.printStackTrace();
        }
    }


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
                    try {
                        checkServerResponse(str);
                    } catch (Exception e) {
                        log(e);
                    }
                }
            } catch (IOException e) {
                log(e);
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
                    log(e);
                }


            }
        }
    }

    /**
     * Отправка сообщения на сервер
     * @param message - json сообщения
     * @throws IOException
     */
    public void sendMessage(String message){
        try {
            out.write(message + "\n");
            out.flush();
        } catch (IOException e) {
            log(e);
        }

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
        } else if("reg".equals(status)) {
            setUserId(Integer.parseInt(message.getMessage()));
        }
    }



    public void registerUser(String username) {
        this.user = new User(username);
    }
    public void setUserId(int id) {
        user.setId(id);
    }

    public void quit() {
        log("Client is closed");
        sendMessage(new Message(this.getUser(), "", "exit").getJSON());
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
