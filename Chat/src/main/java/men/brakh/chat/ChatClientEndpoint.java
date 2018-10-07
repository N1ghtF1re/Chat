package men.brakh.chat;



import javax.websocket.*;
import java.util.concurrent.CountDownLatch;

/**
 * EndPoint клиента
 */
@ClientEndpoint
public class ChatClientEndpoint {
    public static Client client;
    private static CountDownLatch latch;


    @OnOpen
    public void onOpen(Session session) {
        client.setSession(session);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        client.checkServerResponse(message);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        client.quit();
        latch.countDown();
    }

    public static void setLatch(CountDownLatch newLatch) {
        latch = newLatch;
    }

    public static CountDownLatch getLatch() {
        return latch;
    }


}