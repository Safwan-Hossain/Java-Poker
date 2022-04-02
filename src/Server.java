import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
 import java.util.Scanner;
import java.util.UUID;

public class Server {
    private volatile boolean localHostIsReady;
    private ServerGame serverGame;
    private ArrayList<String> clientIDs = new ArrayList<>();
    private ServerSocket serverSocket;

    public Server (ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        this.localHostIsReady = false;
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
        while (!isGameOver()) {
            serverGame.initializeRound();
            ClientHandler.updateAllClients(getNewRoundInfo());

            while (true) {
                takeBets();
                advanceRoundState();
                if (serverGame.getRoundState().equals(RoundState.SHOWDOWN)) {
                    serverGame.giveChipsToWinners();
                    GameInfo showdownGameInfo = getNewRoundStateInfo();
                    serverGame.removeLosers();
                    showdownGameInfo.setPlayers(serverGame.getPlayers());
                    ClientHandler.updateAllClients(showdownGameInfo);
                    waitForNumOfSeconds(2);
                    break;
                }
                ClientHandler.updateAllClients(getNewRoundStateInfo());
                waitForNumOfSeconds(1);
                serverGame.giveNextPlayerTurn();
                ClientHandler.updateAllClients(getTurnInfo());
            }
            serverGame.endRound();

//            System.out.println("SERVER POINT: 1");
            while (!everyClientHandlerHasAPlayer()) {
                waitForNumOfSeconds(0.1);
            }
//            System.out.println("SERVER POINT: 2");
        }
//        System.out.println("SERVER POINT: 3");
        ClientHandler.updateAllClients(getGameEndedInfo());
        while (ClientHandler.clientHandlers.size() > 0) {
            waitForNumOfSeconds(0.5);
        }
//        System.out.println("SERVER POINT: 4");
        closeServer();
    }

    private void waitForNumOfSeconds(double numOfSeconds) {
        try {
            Thread.sleep((int) numOfSeconds * 1000L);
        } catch (InterruptedException ignored) {
        }
    }

    private boolean isGameOver() {
        return serverGame.isGameOver();
    }

    private ArrayList<String> getAllPlayerIDs() {
        ArrayList<String> playerIDs = new ArrayList<>();
        ArrayList<Player> players = serverGame.getPlayers();
        for (Player player: players) {
            playerIDs.add(player.getPlayerID());
        }
        return playerIDs;
    }

    private boolean everyClientHandlerHasAPlayer() {
        ArrayList<String> playerIDs = getAllPlayerIDs();
        for (ClientHandler clientHandler : ClientHandler.clientHandlers) {
            String clientID = clientHandler.getClientID();
            if (!playerIDs.contains(clientID)) {
                System.out.println("Client Name: " + clientHandler.getClientName());
                return false;
            }
        }
        return true;
    }

    private GameInfo getGameEndedInfo() {
        GameInfo gameInfo = new GameInfo("Server", "Server");
        gameInfo.setUpdateType(UpdateType.GAME_ENDED);
        gameInfo.setWinningPlayers(serverGame.getPlayers());
        return gameInfo;
    }

    private GameInfo getNewRoundStateInfo() {
        GameInfo gameInfo = new GameInfo("Server", "Server");
        gameInfo.setRoundState(serverGame.getRoundState());
        gameInfo.setGame(serverGame.getMainGame());
        gameInfo.setTableCards(serverGame.getTableCards());
        gameInfo.setUpdateType(UpdateType.NEW_ROUND_STATE);

        if (serverGame.getRoundState().equals(RoundState.SHOWDOWN)) {
            gameInfo.setWinningPlayers(serverGame.getWinningPlayers());
            gameInfo.setLosingPlayers(serverGame.getPlayersWithNoChips());
            String winningHand = HandEval.getHandName(serverGame.getMainGame().getHighestScore());
            gameInfo.setNameOfWinningHand(winningHand);
            HashMap<Player, ArrayList<Card>> playerHands = new HashMap<>();
            for (Player player: serverGame.getPlayers()) {
                ArrayList<Card> hand = player.getHand();
                playerHands.put(player, hand);
            }
            gameInfo.setPlayerHands(playerHands);
        }
        return gameInfo;
    }

    private void advanceRoundState() {
        serverGame.advanceRoundState();
    }

    // TODO - incomplete method
    private boolean isHostReady() {
        return (serverGame.hasPlayerResponded() &&
                serverGame.getGameInfo().getUpdateType().equals(UpdateType.GAME_STARTED) &&
                serverGame.getGameInfo().hasGameStarted()) ||
                localHostIsReady;
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

    private void waitForPlayerToRespond() {
        while (!serverGame.hasPlayerResponded()) {
            Thread.onSpinWait();
        }
        serverGame.setHasPlayerResponded(false);
    }

    private GameInfo getTurnInfo() {
        GameInfo gameInfo = new GameInfo("Server", "Server");
        gameInfo.setUpdateType(UpdateType.PLAYER_TURN);
        gameInfo.setPlayerWithTurn(serverGame.getPlayerWithTurn());
        return gameInfo;
    }


    private GameInfo getStartGameInfo() {
        GameInfo gameInfo = new GameInfo("Server", "Server");
        gameInfo.setGame(serverGame.getMainGame());
        gameInfo.setUpdateType(UpdateType.GAME_STARTED);
        gameInfo.setGameHasStarted(true);

        HashMap<Player, ArrayList<Card>> playerHands = new HashMap<>();
        ArrayList<Player> players = serverGame.getPlayers();
        for (Player player: players) {
            ArrayList<Card> hand = player.getHand();
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
        gameInfo.setRoles(serverGame.getPlayersWithRoles());

        HashMap<Player, ArrayList<Card>> playerHands = new HashMap<>();
        for (Player player: serverGame.getPlayers()) {
            ArrayList<Card> hand = player.getHand();
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
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            if (scanner.nextLine().equalsIgnoreCase("start")) {
                server.localHostIsReady = true;
            }
        }).start();
        server.startServer();
    }
}
