import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;

public class Server {
    private ArrayList<String> clientIDs = new ArrayList<>();
    private ServerSocket serverSocket;
    private String hostClientID;
    private InetAddress hostAddress;

    public Server (ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public boolean isClosed() {
        return this.serverSocket.isClosed();
    }

    public void startServer() {
        try {
            //System.out.println("SERVER HAS STARTED");
            //System.out.println("SERVER IP: " + InetAddress.getLocalHost());
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                //System.out.println("A new user has entered the room!");
                ClientHandler clientHandler = new ClientHandler(socket, generateClientID());
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            closeServer();
        }

    }

    public void closeServer() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isClientIDUnique(String clientID) {
        for (String existingID: clientIDs) {
            if (existingID.equals(clientID)) {
                return false;
            }
        }
        return true;
    }

    private String generateClientID() {
        while (true) {
            String uniqueID = UUID.randomUUID().toString();
            if (isClientIDUnique(uniqueID)) {
                return uniqueID;
            }
        }
    }

    public static void main(String[] args) throws IOException{
        ServerSocket serverSocket = new ServerSocket(100);
        Server server = new Server(serverSocket);

        server.startServer();
    }
}
