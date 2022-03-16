import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private String clientUsername;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
            clientHandlers.add(this);
        } catch (IOException e) {
            closeEverything();
        }
    }

    @Override
    public void run() {
        GameInfo gameInfo;
        while (socket.isConnected()) {
            try {
                gameInfo = (GameInfo) inputStream.readObject();
                updateClients(gameInfo);
                System.out.println(gameInfo);
            } catch (IOException | ClassNotFoundException e) {
                closeEverything();
                break;
            }
        }

    }

    private void updateClients(GameInfo gameInfo) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (clientHandler != this) {
                    clientHandler.outputStream.writeObject(gameInfo);
                    clientHandler.outputStream.flush();
                }
            } catch (IOException e) {
                closeEverything();
            }
        }
    }

    private void closeEverything() {
        try {
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
            System.out.println("CLOSED EVERYTHING");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}