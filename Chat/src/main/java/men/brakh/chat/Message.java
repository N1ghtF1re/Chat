package men.brakh.chat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class Message {
    @JsonProperty("user")
    private User user; // Объект пользователя
    @JsonProperty("user-message")
    private String message; // Сообщение пользователя
    @JsonProperty("status")
    private String status; // Статус отправленного сообщения. Если exit - разрыв соединения


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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

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

    public String getJSON() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }
    public static Message decodeJSON(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, Message.class);
    }

}
