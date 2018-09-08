package men.brakh;

import men.brakh.chat.Message;
import men.brakh.chat.User;

import java.io.IOException;

public class CustomerClient extends men.brakh.chat.Client {

    final static String ip = "localhost";
    public static int port = 9999;

    public CustomerClient(String ip, int port) throws IOException {
        super(ip, port);
    }

    public void checkAnswer(Message message) {
        String strMessage = message.getMessage();
        if ((strMessage.length() != 0) && (strMessage.charAt(0) == '!')) {
            String[] words = strMessage.split(" ");
            if ((words.length == 2) && (words[0].equals("!register"))) {
                registerUser(words[1]);
                showMessage(new User("System"), "Hello, " + getUser());
            } else if (getUser() != null) {

            }
        } else if (getUser() != null) {
            try {
                sendMessage(message.getJSON());
            } catch (IOException e) {
                e.printStackTrace();
            }
            showMessage(message.getUser(), message.getMessage());
        }
    }


    @Override
    public void registerUser(String username) {
        setUser(new Customer(username));
    }

    public static void main(String args[]) {
        try {
            new CustomerClient(ip, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
