package men.brakh.chat;



import com.google.gson.Gson;

import java.io.IOException;

public class Message {
    private User user; // Объект пользователя
    private String message; // Сообщение пользователя
    private String status; // Статус отправленного сообщения. Если exit - разрыв соединения
    private String userType;


    public Message() {
        // Пустой конструктор для декодирования JSON
    }

    public Message(User user, String message) {
        this.user = user;
        this.message = message;
        this.status = "ok";
    }
    public Message(User user, String message, String status) {
        this(user, message);
        this.status = status;
    }

    public User getUser() {return user;}
    public void setUser(User user) {this.user = user;}
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getJSON()  {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static Message decodeJSON(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Message.class);
    }

    /*
    public static void main(String args[]) {
        Message message = new Message(new User("ek"), "Message");

        try {
            System.out.println(Message.decodeJSON( message.getJSON() ).getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }*/

}
