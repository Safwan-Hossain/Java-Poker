import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ConcurrentModificationException;

public class ServerTest {
    private static Server server;

    public static void runServer()  {
        try {
            ServerSocket serverSocket = new ServerSocket(101);
            server = new Server(serverSocket);
            new Thread(server::startServer).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Server can handle around 40 concurrent players. Afterwards the program crashes due to too many threads
    // Note that better CPUs can handle more players due to more available threads.
    public static void spamClientJoins() {
        int numOfSpammers = 40;
        try {
            for (int i = 0; i < numOfSpammers; i++) {
                Socket socket = new Socket(InetAddress.getLocalHost(), 101);
                Client client = new Client(socket, "bob " + i);
                GameInfo clientSetUpInfo = new GameInfo("", client.getClientName());
                clientSetUpInfo.setIsHost(false);
                client.sendMessage(clientSetUpInfo);
                clientSetUpInfo = (GameInfo) client.listenForMessage();
                client.setClientID(clientSetUpInfo.getClientID());
                System.out.println("Number of connected clients: " + server.getNumberOfClients());
                Thread.sleep(1000);
            }
        } catch (RuntimeException | IOException | InterruptedException | ClassNotFoundException ignored) {
        }
    }

    public static void main(String[] args) {
        runServer();
        spamClientJoins();
    }
}
