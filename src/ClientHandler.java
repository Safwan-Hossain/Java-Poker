import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private static ServerGame serverGame;
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private String clientName;
    private String clientID;

    public ClientHandler(Socket socket, String clientID, ServerGame serverGame) {
        try {
            this.socket = socket;
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
            clientHandlers.add(this);
        } catch (IOException e) {
            closeEverything();
        }
        ClientHandler.serverGame = serverGame;
        this.clientID = clientID;
    }

    @Override
    public void run() {
        GameInfo gameInfo;
        while (socket.isConnected()) {
            try {
                // First message is client username
                if (clientName == null) {
                    setUpClient();
                    serverGame.addPlayer(clientName, clientID);
                }
                gameInfo = (GameInfo) inputStream.readObject();
                if (gameInfo.getUpdateType() == UpdateType.PLAYER_QUIT) {
                    closeEverything();
                    return;
                }
                serverGame.setGameInfo(gameInfo);
                serverGame.setHasPlayerResponded(true);
            } catch (IOException | ClassNotFoundException e) {
                closeEverything();
                break;
            }
        }

    }

    private void setUpClient() throws IOException, ClassNotFoundException {
        GameInfo clientSetupInfo = (GameInfo) inputStream.readObject();
        clientName = clientSetupInfo.getPlayerName();

        clientSetupInfo = new GameInfo(clientID, clientName);
        clientSetupInfo.setUpdateType(UpdateType.CONNECTION_STATUS);
        clientSetupInfo.setConnectionStatus(ConnectionStatus.JOINED);

        updateClient(clientSetupInfo);
        updateOtherClients(clientSetupInfo);
    }

    public static void updateAllClients(GameInfo gameInfo) {
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.updateClient(gameInfo);
        }
    }

    public static void startGame(GameInfo gameInfo) {
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.updateClient(gameInfo);
        }
    }

//    private void updateAllClients(GameInfo gameInfo) throws IOException {
//        this.outputStream.writeObject(gameInfo);
//        this.outputStream.flush();
//        updateOtherClients(gameInfo);
//    }
    public void updateClient(GameInfo gameInfo) {
        try {
            this.outputStream.writeObject(gameInfo);
            this.outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            closeEverything();
        }
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
            System.out.println("CLIENT HANDLER: CLOSED EVERYTHING");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getClientName() {
        return clientName;
    }

    public String getClientID() {
        return clientID;
    }


}