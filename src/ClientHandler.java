import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private static Game mainGame;
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private String clientName;
    private String clientID;

    public ClientHandler(Socket socket, String clientID) {
        try {
            this.socket = socket;
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
            clientHandlers.add(this);
        } catch (IOException e) {
            closeEverything();
        }
        this.clientID = clientID;
    }

    @Override
    public void run() {
        GameInfo gameInfo;
        while (socket.isConnected()) {
            try {
                // First message is client username
                if (clientName == null) {
                    clientName = (String) inputStream.readObject();
                    GameInfo gm = new GameInfo(clientID, clientName);
                    gm.setUpdateType(UpdateType.CONNECTION_STATUS);
                    gm.setConnectionStatus(ConnectionStatus.JOINED);
                    updateOtherClients(gm);
                }

                gameInfo = (GameInfo) inputStream.readObject();
                updateAllClients(gameInfo);
                System.out.println(gameInfo);
            } catch (IOException | ClassNotFoundException e) {
                closeEverything();
                break;
            }
        }

    }

    private void updateAllClients(GameInfo gameInfo) throws IOException {
        this.outputStream.writeObject(gameInfo);
        this.outputStream.flush();
        updateOtherClients(gameInfo);
    }

    private void updateOtherClients(GameInfo gameInfo) {
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