import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Properties;

public class Server {
    static final int PORT = 31220; // Порт, по которому будет подключаться наш сервер
    public static final ArrayList<RunnableClient> CLIENTS = new ArrayList<>();

    public Server() {
        Socket clientSocket = null;
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            log("Сервер запущен...");
            //ждём подключений от сервера
            while (true) {
                clientSocket = serverSocket.accept();
                RunnableClient client = new RunnableClient(clientSocket);
                CLIENTS.add(client);
                // каждое подключение клиента обрабатываем в новом потоке
                new Thread(client).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // закрываем подключение
                assert clientSocket != null;
                clientSocket.close();
                log("Сервер остановлен.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void log(String msg) {
        RunnableClient.log(msg);
    }

    public static void removeClient(RunnableClient client, ArrayList<RunnableClient> CLIENTS) {
        CLIENTS.remove(client);
    }
}
