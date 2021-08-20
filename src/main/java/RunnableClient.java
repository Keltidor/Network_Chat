import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// реализуем интерфейс Runnable, который позволяет работать с потоками
public class RunnableClient implements Runnable {
    // исходящее сообщение
    private PrintWriter outMessage;
    // входящее сообщение
    private BufferedReader inMessage;
    // количество клиентов в чате
    private static int numberOfClients = 0;
    //имя клиента
    String name;

    // конструктор
    public RunnableClient(Socket socket) {
        numberOfClients++;
        try {
            outMessage = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            inMessage = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //пустой конструктор для теста
    public RunnableClient() {
    }

    // Переопределяем метод run(), который вызывается когда
    // мы вызываем new Thread(client).start() в классе Server;
    @Override
    public void run() {

        try {
            name = inMessage.readLine();
            sendMessageToAllClients("Пользователь " + name + " вошел в чат." +
                    " Количество участников чата равно: " + numberOfClients, null);

            String line;
            while (true) {
                while ((line = inMessage.readLine()) != null) {
                    if ("/exit".equals(line)) {
                        return;
                    }
                    sendMessageToAllClients(line, this);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.close();
        }
    }

    private synchronized void sendMessageToAllClients(String msg, RunnableClient thisClient) {
        log(msg);
        for (RunnableClient client : Server.CLIENTS) {
            if (client != thisClient) {
                client.sendMsg(msg);
            }
        }
    }

    static void log(String msg) {
        String dateNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
        System.out.printf("[%s] %s\n", dateNow, msg);

        File log = new File("src/main/resources/serverLog.txt");
        try (FileWriter fw = new FileWriter(log, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(String.format("[%s] %s\n", dateNow, msg));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // отправляем сообщение
    public void sendMsg(String msg) {
        try {
            outMessage.println(msg);
            outMessage.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // клиент выходит из чата
    public void close() {
        // удаляем клиента из списка
        Server.removeClient(this, Server.CLIENTS);
        numberOfClients--;
        sendMessageToAllClients("Пользователь " + name + " покинул чат." +
                " Количество участников чата равно: " + numberOfClients, this);
    }
}
