package men.brakh.chat;

import org.junit.Test;

import static org.junit.Assert.*;

public class MessageTest {

    @Test
    public void getJSON() {
        Message message = new Message(new User("Test", 0), "test");
        assertEquals(message.getJSON(), "{\"user\":{\"name\":\"Test\",\"userType\":\"NONE\",\"id\":0},\"message\":\"test\",\"status\":\"ok\"}");
        message = new Message(new User("1"), "", "test");
        assertEquals(message.getJSON(), "{\"user\":{\"name\":\"1\",\"userType\":\"NONE\",\"id\":-1},\"message\":\"\",\"status\":\"test\"}");

    }

    @Test
    public void decodeJSON() {
        String json = "{\"user\":{\"name\":\"Test\",\"userType\":\"NONE\",\"id\":-1},\"message\":\"test\",\"status\":\"ok\"}";
        assertEquals(Message.decodeJSON(json).getJSON(), new Message(new User("Test"), "test").getJSON());
    }

}