package men.brakh.logger;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private static final String defaulFileName = "chat.log";
    private String filename;
    private Boolean isWriteToConsole;
    private FileWriter out;

    public Logger (String filename, Boolean isWriteToConsole) throws IOException {
        this.filename = filename;
        this.isWriteToConsole = isWriteToConsole;
        out = new FileWriter(filename, true);
    }
    public Logger(String filename) throws IOException {
        this(filename, false);
    }
    public Logger(Boolean isWriteToConsole) throws IOException {
        this(defaulFileName, isWriteToConsole);
    }
    public  Logger() throws IOException {
        this(defaulFileName, false);
    }


    public void write(String message) throws IOException {
        Date currDate = new Date();
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String logMessage = "[" + dt.format(currDate) + "] " + message;
        out.write(logMessage + "\n");
        out.flush();
        if(isWriteToConsole) {
            System.out.println(logMessage);
        }
    }


}
