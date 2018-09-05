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

    private static Socket clientSocket; // Сокет для общения
    private static BufferedReader in; // Поток чтения из соекта
    private static BufferedWriter out; // Поток записи в сокет

    /**
     * Чтение сообщений с сервера в отдельном потоке
     */
    private class ReadMsg extends Thread {

        public ReadMsg() {
            this.start();
        }

        @Override
        public void run() {

            String str;
            try {
                while (true) {
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

        public WriteMsg() {
            this.start();
        }

        @Override
        public void run() {
            while (true) {
                String answer;
                Scanner scan = new Scanner(System.in);
                answer = scan.nextLine();

                try {
                    checkAnswer(Message.decodeJSON(answer));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }
    }

    /**
     * Обработка сообщений пользоваетелей
     * @param message сообщение пользователя
     */
    abstract void checkAnswer(Message message);

    /**
     * Отрисовка сообщения пользователя
     * @param user Объект пользователя
     * @param message Сообщение
     */
    public void showMessage(User user, String message) {
        System.out.printf("[%s%n] %s%n", user, message);
    }

    /**
     * Проверка ответа сервера (если статус - "ок", то все хорошо)
     * @param json - json ответа сервера
     */
    public void checkServerResponse(String json) {
        Message message;
        try {
            message = Message.decodeJSON(json);
            if(message.getStatus().equals("ok")) {
                showMessage(message.getUser(), message.getMessage());
            }
        } catch (IOException e) {
            System.out.println("Bad response");
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

    /**
     * Создание объекта клиента
     * @param name Имя пользователя клиента
     * @param ip IP сервера
     * @param port Порт сервера
     * @throws IOException
     */
    public Client(String name, String ip, int port) throws IOException {
        this.user = new User(name);
        initSocket(ip, port);

    }


}
