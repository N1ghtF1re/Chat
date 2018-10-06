package men.brakh.server;

import men.brakh.chat.Message;
import men.brakh.chat.User;
import men.brakh.chat.UsersTypes;
import men.brakh.server.data.TwoPersonChat;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.Socket;

import static org.junit.Assert.*;

public class ServerTest {
    private Server server;
    private Socket clientSocket;
    private byte[] input;
    private InputStream in;
    private OutputStream out;

    @Before
    public void init() throws IOException {

       String nullMsg = new Message(new User("1"), "", "none").getJSON();
       input = nullMsg.getBytes();

       server = new Server() {
           @Override
           public void log(String message) {}
           @Override
           public void log(Exception e) {}
       };

       clientSocket = new Socket(){
            @Override
            public InputStream getInputStream() {
                in = new ByteArrayInputStream(input);
                return in;
            }

            @Override
            public OutputStream getOutputStream() {
                out = new ByteArrayOutputStream();
                return out;
            }
        };
    }

    Socket createSocket() {
        return new Socket(){
            @Override
            public InputStream getInputStream() {
                in = new ByteArrayInputStream(input);
                return in;
            }

            @Override
            public OutputStream getOutputStream() {
                out = new ByteArrayOutputStream();
                return out;
            }
        };
    }

    @Test
    public void regAgentTest() throws IOException {
        final String name1 = "Agent1";
        final String name2 = "Agent2";


        User agent = new User(name1);
        agent.setUserType(UsersTypes.AGENT);

        ServerSomthing serverSomthing1 = new ServerSomthing(createSocket(), server); // Создаем нить
        serverSomthing1.agentsHandler(new Message(agent, "", "reg")); // Регистрируем агента
        User agent1 = server.agentsQueue.getFirst().getUser(); // Получаем первого агента в очереди
        assertEquals(agent1.getName(), name1);
        assertEquals(agent1.getId(), 1);

        agent = new User(name2);
        ServerSomthing serverSomthing2 = new ServerSomthing(createSocket(), server);
        serverSomthing2.agentsHandler(new Message(agent, "", "reg"));
        User agent2 = server.agentsQueue.getFirst().getUser();
        assertEquals(agent2.getName(), name1); // Проверяем что при добавленном новом агенте первым в очереди остается тот, кто зашел первее

        server.agentsQueue.poll();
        agent2 = server.agentsQueue.getFirst().getUser();
        assertEquals(agent2.getName(), name2);
        assertEquals(agent2.getId(), 2);

    }

    @Test
    public void regClientTest() throws IOException {
        final String name1 = "Client1";
        final String name2 = "Client2";

        ServerSomthing serverSomthing1 = new ServerSomthing(createSocket(), server); // Создаем нить
        User user1 = new User(name1, server.getNewId());
        serverSomthing1.usersHandler(new Message(user1 , "", "reg")); // Регистрируем пользователя
        serverSomthing1.usersHandler(new Message(user1, "help", "ok")); // Ставим его в очередь

        TwoPersonChat chat1 = server.customerChatQueue.getFirst();
        assertEquals(chat1.getCustomer().getUser().getName(), name1);

        ServerSomthing serverSomthing2 = new ServerSomthing(createSocket(), server);
        User user2 = new User(name2, server.getNewId());
        serverSomthing2.usersHandler(new Message(user2 , "", "reg"));
        serverSomthing2.usersHandler(new Message(user2, "help", "ok"));

        chat1 = server.customerChatQueue.getFirst();
        assertEquals(chat1.getCustomer().getUser().getName(), name1);
    }

    @Test
    public void connectClientAndAgentTest() throws IOException {
        final User ag1 = new User("Agent1", 1);
        final User cl1 = new User("Client1",2);
        final User ag2 = new User("Agent2", 3);
        final User cl2 = new User("Client2", 4);

        ServerSomthing s1 = new ServerSomthing(createSocket(), server);
        s1.agentsHandler(new Message(ag1, "reg"));

        ServerSomthing s2 = new ServerSomthing(createSocket(), server);
        s2.usersHandler(new Message(cl1, "reg"));

        s2.usersHandler(new Message(cl1, "Help", "ok"));


        TwoPersonChat c1 = server.customerChatQueue.getFirst();
        assertTrue(c1.getCustomer().getUser().equal(cl1));
        assertTrue(c1.getAgent().getUser().equal(ag1));

        ServerSomthing s4 = new ServerSomthing(createSocket(), server);
        s4.usersHandler(new Message(cl2, "reg"));
        s4.usersHandler(new Message(cl2, "Help", "ok"));

        ServerSomthing s3 = new ServerSomthing(createSocket(), server);
        s3.agentsHandler(new Message(ag2, "reg"));

        TwoPersonChat c2 = server.customerChatQueue.searchCustomer(cl2);
        assertTrue(c2.getCustomer().getUser().equal(cl2));
        assertTrue(c2.getAgent().getUser().equal(ag2));

    }

    @Test
    public void userLeaveAndJoinTest() throws IOException {
        final User ag1 = new User("Agent1", 1);
        final User cl1 = new User("Client1",2);

        ServerSomthing s1 = new ServerSomthing(createSocket(), server);
        s1.agentsHandler(new Message(ag1, "reg"));

        ServerSomthing s2 = new ServerSomthing(createSocket(), server);
        s2.usersHandler(new Message(cl1, "reg"));
        s2.usersHandler(new Message(cl1, "Help", "ok"));

        s2.usersHandler(new Message(cl1, "", "leave"));
        assertTrue(server.agentsQueue.getFirst().getUser().equal(ag1));

        s2.usersHandler(new Message(cl1, "I am be back", "ok"));
        assertEquals(server.agentsQueue.getFirst(), null);
        assertTrue(server.customerChatQueue.getFirst().getAgent().getUser().equal(ag1));
    }

    @Test
    public void userExitTest1() throws IOException {

        // Делаем чтобы пользователь ливнул, после чего подключился другой пользователь
        final User ag1 = new User("Agent1", 1);
        final User cl1 = new User("Client1",2);
        final User cl2 = new User("Client2",3);

        ServerSomthing s1 = new ServerSomthing(createSocket(), server);
        s1.agentsHandler(new Message(ag1, "reg"));

        ServerSomthing s2 = new ServerSomthing(createSocket(), server);
        s2.usersHandler(new Message(cl1, "reg"));
        s2.usersHandler(new Message(cl1, "Help", "ok"));

        s2.usersHandler(new Message(cl1, "", "exit"));

        assertEquals(server.customerChatQueue.getFirst(), null);
        assertEquals(server.agentsQueue.getFirst().getUser(), ag1);

        ServerSomthing s3 = new ServerSomthing(createSocket(), server);
        s3.usersHandler(new Message(cl2, "reg"));
        s3.usersHandler(new Message(cl2, "Help", "ok"));

        assertTrue(server.customerChatQueue.getFirst().getAgent().getUser().equals(ag1));
        assertTrue(server.customerChatQueue.getFirst().getCustomer().getUser().equal(cl2));
        assertEquals(server.agentsQueue.getFirst(), null);
    }

    @Test
    public void userExitTest2() throws IOException {

        // Делаем чтобы изначально было 2 пользователя, а потом один ливнул
        final User ag1 = new User("Agent1", 1);
        final User cl1 = new User("Client1",2);
        final User cl2 = new User("Client2",3);

        ServerSomthing s1 = new ServerSomthing(createSocket(), server);
        s1.agentsHandler(new Message(ag1, "reg"));

        ServerSomthing s2 = new ServerSomthing(createSocket(), server);
        s2.usersHandler(new Message(cl1, "reg"));
        s2.usersHandler(new Message(cl1, "Help", "ok"));

        ServerSomthing s3 = new ServerSomthing(createSocket(), server);
        s3.usersHandler(new Message(cl2, "reg"));
        s3.usersHandler(new Message(cl2, "Help", "ok"));

        s2.usersHandler(new Message(cl1, "", "exit"));


        assertTrue(server.customerChatQueue.getFirst().getAgent().getUser().equals(ag1));
        assertTrue(server.customerChatQueue.getFirst().getCustomer().getUser().equal(cl2));
        assertEquals(server.agentsQueue.getFirst(), null);
    }

    @Test
    public void agentExitTest1() throws IOException {

        // Делаем чтобы агент ливнул и присоеденился другой агент

        final User ag1 = new User("Agent1", 1);
        final User cl1 = new User("Client1",2);
        final User ag2 = new User("Agent2",3);

        ServerSomthing s1 = new ServerSomthing(createSocket(), server);
        s1.agentsHandler(new Message(ag1, "reg"));

        ServerSomthing s2 = new ServerSomthing(createSocket(), server);
        s2.usersHandler(new Message(cl1, "reg"));
        s2.usersHandler(new Message(cl1, "Help", "ok"));

        s1.agentsHandler(new Message(ag1, "", "exit"));

        assertEquals(server.customerChatQueue.getFirst().getAgent(), null);

        ServerSomthing s3 = new ServerSomthing(createSocket(), server);
        s3.agentsHandler(new Message(ag2, "reg"));
        assertEquals(server.customerChatQueue.getFirst().getAgent().getUser(), ag2);

    }

    @Test
    public void agentExitTest2() throws IOException {

        // Делаем чтобы изначально было 2 агента, а потом первый агент ливнул

        final User ag1 = new User("Agent1", 1);
        final User ag2 = new User("Agent2",2);
        final User cl1 = new User("Client1",3);


        ServerSomthing s1 = new ServerSomthing(createSocket(), server);
        s1.agentsHandler(new Message(ag1, "reg"));

        ServerSomthing s2 = new ServerSomthing(createSocket(), server);
        s2.agentsHandler(new Message(ag2, "reg"));

        ServerSomthing s3 = new ServerSomthing(createSocket(), server);
        s3.usersHandler(new Message(cl1, "reg"));
        s3.usersHandler(new Message(cl1, "Help", "ok"));

        s1.agentsHandler(new Message(ag1, "", "exit"));

        assertEquals(server.customerChatQueue.getFirst().getAgent().getUser(), ag2);

    }

    OutputStream out1;
    OutputStream out2;


    private int getChatId(String[] messages) {
        for(int i = 0; i < messages.length; i++) {
            Message msg = Message.decodeJSON(messages[i]);
            if(msg.getStatus().equals("chat")) {
                return Integer.parseInt(msg.getMessage());
            }
        }
        return -1;
    }

    @Test
    public void messageSendTest() throws IOException {
        final User ag1 = new User("Agent1", 1);
        final User cl1 = new User("Client1",2);

        final String msg1 = "help";
        final String msg2 = "ok";
        final String msg3 = "thank!";


        ServerSomthing s1 = new ServerSomthing(new Socket(){
            @Override
            public OutputStream getOutputStream() {
                out1 = new ByteArrayOutputStream();
                return out1;
            }

            @Override
            public InputStream getInputStream() {
                in = new ByteArrayInputStream(input);
                return in;
            }
        }, server);
        s1.agentsHandler(new Message(ag1, "reg"));

        ServerSomthing s2 = new ServerSomthing(new Socket(){
            @Override
            public InputStream getInputStream() {
                in = new ByteArrayInputStream(input);
                return in;
            }

            @Override
            public OutputStream getOutputStream() {
                out2 = new ByteArrayOutputStream();
                return out2;
            }
        }, server);
        s2.usersHandler(new Message(cl1, "reg"));
        String messages2[] = out2.toString().split("\n");
        int chat_id = getChatId(messages2);
        s2.usersHandler(new Message(cl1, msg1, chat_id));

        String messages1[] = out1.toString().split("\n");



        assertEquals(Message.decodeJSON(messages1[messages1.length-1]).getStatus(), "ok");
        assertEquals(Message.decodeJSON(messages1[messages1.length-1]).getMessage(), msg1);


        chat_id = getChatId(messages1);

        s1.agentsHandler(new Message(ag1, msg2, chat_id));
        messages2 = out2.toString().split("\n");
        assertEquals(Message.decodeJSON(messages2[messages2.length-1]).getStatus(), "ok");
        assertEquals(Message.decodeJSON(messages2[messages2.length-1]).getMessage(), msg2);

        s1.usersHandler(new Message(cl1, msg3, chat_id));
        messages1 = out1.toString().split("\n");
        assertEquals(Message.decodeJSON(messages1[messages1.length-1]).getStatus(), "ok");
        assertEquals(Message.decodeJSON(messages1[messages1.length-1]).getMessage(), msg3);
    }

    @Test
    // Агент подключается одновременно к двум клиентам
    public void agentWithTwoSessionsTest() throws IOException {

        final User ag1 = new User("Agent1", 1);
        final User cl1 = new User("Client1",2);
        final User cl2 = new User("Client1",3);

        ServerSomthing s1 = new ServerSomthing(createSocket(), server);
        s1.agentsHandler(new Message(ag1, "reg"));
        s1.agentsHandler(new Message(ag1, "","add-session"));

        ServerSomthing s2 = new ServerSomthing(createSocket(), server);
        s2.usersHandler(new Message(cl1, "reg"));
        s2.usersHandler(new Message(cl1, "Help", "ok"));

        ServerSomthing s3 = new ServerSomthing(createSocket(), server);
        s3.usersHandler(new Message(cl2, "reg"));
        s3.usersHandler(new Message(cl2, "Help", "ok"));

        TwoPersonChat chat1 = server.customerChatQueue.searchCustomer(cl1);
        TwoPersonChat chat2 = server.customerChatQueue.searchCustomer(cl2);
        assert(chat1.getAgent().getUser().equal(ag1));
        assert(chat2.getAgent().getUser().equal(ag1));

        s1.agentsHandler(new Message(ag1, "","rm-session", chat2.getId()));
        assert(chat2.getAgent() == null);
        assert(server.agentsQueue.getFirst() == null);
    }



}