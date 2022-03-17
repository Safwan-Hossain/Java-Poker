import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class MainMenu {
    private static final int MAX_NUM_OPTIONS = 3;

    public static int getMaxNumOptions() {
        return MAX_NUM_OPTIONS;
    }

    public static Client joinServer(Socket socket, String username)  {
        Client client = new Client(socket, username);
        return client;
    }

    public static void hostServer() throws IOException {
        // Host server
        ServerSocket serverSocket = new ServerSocket(100);
        Server server = new Server(serverSocket);
        new Thread(() -> { server.startServer(); }).start();

    }

    public static void exitProgram() {
        System.exit(0);
    }


}
