import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class InMessageRunnable implements Runnable {
    // Создаем переменную типа BufferedReader ()
    private BufferedReader inMessage;

    public InMessageRunnable(Socket socket) {
        try {
            inMessage = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void log(String msg) {
        String dateNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));

        File Log = new File("src/main/resources/clientLog.txt");
        try (FileWriter fw = new FileWriter(Log, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(String.format("[%s] %s\n", dateNow, msg));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void inMessage(String msg) {
        System.out.println(msg);
        log(msg);
    }

    @Override
    public void run() {
        try {
            String line;
            while (true) {
                while ((line = inMessage.readLine()) != null) {
                    inMessage(line);
                }
            }
        } catch (IOException ignored) {
        }
    }
}