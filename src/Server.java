import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Server {
    private ServerGame serverGame;
    private ArrayList<String> clientIDs = new ArrayList<>();
    private ServerSocket serverSocket;

    public Server (ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public boolean isClosed() {
        return this.serverSocket.isClosed();
    }

    public void startServer() {
        serverGame = new ServerGame();
        new Thread(this::listenForNewClients).start(); // listen for new clients in a new thread

        waitForHostReady();

        serverGame.startGame();
        ClientHandler.updateAllClients(getStartGameInfo());
        runGame();
    }

    private void runGame() {
        while (true) {
            serverGame.initializeRound();
            ClientHandler.updateAllClients(getNewRoundInfo());

            while (true) {
                if (serverGame.getRoundState().equals(RoundState.SHOWDOWN)) {
                    ClientHandler.updateAllClients(getNewRoundStateInfo());
                    showDown();
                    break;
                }
                takeBets();
                advanceRoundState();
                ClientHandler.updateAllClients(getNewRoundStateInfo());
                serverGame.giveNextPlayerTurn();
                ClientHandler.updateAllClients(getTurnInfo());
            }
            endRound();
        }
    }

    private GameInfo getNewRoundStateInfo() {
        GameInfo gameInfo = new GameInfo("Server", "Server");
        gameInfo.setRoundState(serverGame.getMainGame().getRoundState());
        gameInfo.setGame(serverGame.getMainGame());
        gameInfo.setTableCards(serverGame.getMainGame().getTableCards());
        gameInfo.setUpdateType(UpdateType.NEW_ROUND_STATE);
        return gameInfo;
    }
    private void showDown() {
        serverGame.giveChipsToWinners();
        serverGame.removeLosers();
    }

    private void advanceRoundState() {
        serverGame.advanceRoundState();
    }

    private void endRound() {
        serverGame.endRound();
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {

        }
    }

    // TODO - incomplete method
    private boolean isHostReady() {
        return serverGame.hasPlayerResponded() &&
                serverGame.getGameInfo().getUpdateType().equals(UpdateType.GAME_STARTED) &&
                serverGame.getGameInfo().hasGameStarted();
    }

    private void waitForHostReady() {
        while (!(isHostReady())) {
            Thread.onSpinWait();
        }
        serverGame.setHostReady(true);
        serverGame.setHasPlayerResponded(false);
    }

    private void takeBets() {
        while (!serverGame.isRoundStateOver()) {
            waitForPlayerToRespond();
            serverGame.applyPlayerAction(serverGame.getGameInfo());
            ClientHandler.updateAllClients(serverGame.getGameInfo());
            if (!serverGame.isRoundStateOver()) {
                serverGame.giveNextPlayerTurn();
                ClientHandler.updateAllClients(getTurnInfo());
            }
        }
    }

    private GameInfo getTurnInfo() {
        GameInfo gameInfo = new GameInfo("Server", "Server");
        gameInfo.setUpdateType(UpdateType.PLAYER_TURN);
        gameInfo.setPlayerWithTurn(serverGame.getPlayerWithTurn());
        return gameInfo;
    }

    private void waitForPlayerToRespond() {
        while (!serverGame.hasPlayerResponded()) {
            Thread.onSpinWait();
        }
        serverGame.setHasPlayerResponded(false);
    }

    private GameInfo getStartGameInfo() {
        GameInfo gameInfo = new GameInfo("Server", "Server");
        gameInfo.setGame(serverGame.getMainGame());
        gameInfo.setUpdateType(UpdateType.GAME_STARTED);
        gameInfo.setGameHasStarted(true);

        HashMap<Player, ArrayList<Card>> playerHands = new HashMap<>();
        ArrayList<Player> players = serverGame.getMainGame().getPlayers();
        for (Player player: players) {
            ArrayList<Card> hand = player.get_hand();
            playerHands.put(player, hand);
        }
        gameInfo.setPlayerHands(playerHands);

        return gameInfo;
    }

    private GameInfo getNewRoundInfo() {
        GameInfo gameInfo = new GameInfo("Server", "Server");
        gameInfo.setUpdateType(UpdateType.NEW_ROUND_STATE);
        gameInfo.setRoundState(RoundState.PRE_FLOP);
        gameInfo.setGame(serverGame.getMainGame());
        gameInfo.setPlayerWithTurn(serverGame.getPlayerWithTurn());

        ArrayList<Player> players = serverGame.getMainGame().getPlayers();

        gameInfo.setRoles(serverGame.getMainGame().getPlayersWithRoles());

        HashMap<Player, ArrayList<Card>> playerHands = new HashMap<>();
        for (Player player: players) {
            ArrayList<Card> hand = player.get_hand();
            playerHands.put(player, hand);
        }

        gameInfo.setPlayerHands(playerHands);
        return gameInfo;
    }


    private void gameOver() {
        // Declare winner
        // Show game over screen
        // close server
    }

    public void listenForNewClients() {
        try {
            //System.out.println("SERVER HAS STARTED");
            //System.out.println("SERVER IP: " + InetAddress.getLocalHost());
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
//                System.out.println("SERVER: A new user has entered the table!");
                ClientHandler clientHandler = new ClientHandler(socket, generateClientID(), serverGame);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            closeServer();
        }
    }

    public void closeServer() {
        System.out.println("CLOSING SERVER");
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
                clientIDs.add(uniqueID);
                return uniqueID;
            }
        }
    }

    public static void main(String[] args) throws IOException{
        ServerSocket serverSocket = new ServerSocket(101);
        Server server = new Server(serverSocket);
        System.out.println(InetAddress.getLocalHost());
        server.startServer();
    }
}
