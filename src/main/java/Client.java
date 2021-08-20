import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;

public class Client {
    static final int PORT = 31220;
    static final String HOST = "127.0.0.1";

    public Client() throws IOException {

        Socket clientSocket = new Socket(HOST, PORT);

        // Запустим отдельный поток который будет смотреть входящие сообщения
        new Thread(new InMessageRunnable(clientSocket)).start();

        try (Scanner scanner = new Scanner(System.in); // Обычный сканнер, для инициализации имени клиента

             PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true)) {

            //Приветственный блок
            System.out.println("Введите ваше имя:");
            String name = scanner.nextLine();
            out.println(name);

            String msg;
            while (true) {
                msg = scanner.nextLine();
                if ("/exit".equals(msg)) {
                    out.println(msg);
                    break;
                }
                out.printf("%s: %s\n", name, msg);
                log(String.format("%s: %s", name, msg));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void log(String msg) {
        InMessageRunnable.log(msg);
    }

}