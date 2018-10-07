package men.brakh.customer;

import men.brakh.chat.Message;
import men.brakh.chat.User;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.Socket;

import static org.junit.Assert.assertEquals;

public class CustomerClientTest {
    private byte[] input = new byte[1];
    private InputStream in;
    private OutputStream out;
    private CustomerClient client;


    @Before
    public void init() {
        out = new ByteArrayOutputStream();
        client = new CustomerClient() {
            @Override
            public void showMessage(User user, String message) {
            }
            @Override
            public void start() {
            }
            @Override
            public void sendMessage(String message) {
                try {
                    out.write(message.getBytes());
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void log(Exception e) {
            }
            @Override
            public void log(String str) {
            }
        };


    }



    @Test
    public void serverRegTest() {
        Message message = new Message(new User("Sys"), "2", "reg");
        client.registerUser("Test");
        client.checkServerResponse(message.getJSON());
        assertEquals(client.getUser().getId(), 2);

    }

    private Message sendMessageAndGetResponse(String text) {
        client.registerUser("Test");
        Message message = new Message(new User("test"), text);
        client.checkAnswer(message);
        return Message.decodeJSON(out.toString().replace("\n", ""));
    }

    @Test
    public void regTest() {
        Message message = new Message(new User("test"), "!register Test");
        client.checkAnswer(message);
        Message answer =  Message.decodeJSON(out.toString().replace("\n", ""));
        assertEquals(answer.getStatus(), "reg");
    }

    @Test
    public void exitTest() {
        Message answer = sendMessageAndGetResponse("!exit");
        assertEquals(answer.getStatus(), "exit");
    }

    @Test
    public void leaveTest() {
        Message answer = sendMessageAndGetResponse("!leave");
        assertEquals(answer.getStatus(), "leave");
    }

    @Test
    public void normalSendMessageTest() {
        Message answer = sendMessageAndGetResponse("Test");
        assertEquals(answer.getStatus(), "ok");
    }
}