import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class ClientController {
    private Player myPlayer;
    private Client client;
    private final boolean isHost;
    private volatile Game myGame;
    private volatile boolean gameHasStarted;
    private volatile boolean gameHasEnded;
    private volatile boolean myPlayerLost;
    private ArrayList<Thread> runningThreads;

    private final String CANCEL_BET_VALUE = "B";
    private final String START_GAME_COMMAND = "START";

    public ClientController(Socket socket, String username, boolean isHost) throws IOException {
        this.isHost = isHost;
        this.gameHasStarted = false;
        this.client = new Client(socket, username);
        this.runningThreads = new ArrayList<>();
    }

    public void startController(Scanner scanner) throws IOException {
        setUpClient(); // sets up client name and ID with server
        GameView.displayClientInformation(client);
        setUpMyPlayer(); // sets up player using client name and ID

        Thread listeningThread = new Thread(this::listenForIncomingMessages); // listens for messages from server on separate thread
        runningThreads.add(listeningThread);
        listeningThread.start();

        runGame(scanner);
        if (myPlayerLost) {
            GameView.displayLoseGameScreen();
            GameView.displayExitMessage();
            leaveTable();
        }
        if (isHost) {
            askHostToStart(scanner);
        }
        else {
            waitForHostReady();
        }

        GameView.displayGameIsStartingMessage();
        waitForGameToStart(); // wait for game to initialize and start

        runGame(scanner);
        closeController();
    }

    public void closeController() {
        if (client != null) {
            client.closeEverything();
        }
    }

    private void setUpClient() throws IOException {
        GameInfo clientSetUpInfo = new GameInfo("", client.getClientName());
        client.sendMessage(clientSetUpInfo);
        try {
            clientSetUpInfo = (GameInfo) client.listenForMessage();
            client.setClientID(clientSetUpInfo.getClientID());
        } catch (ClassNotFoundException e) {
        }
    }

    private void setUpMyPlayer() {
        myPlayer = new Player(client.getClientName(), client.getClientID());
    }

    private boolean myPlayerHasTurn() {
        return myGame.getPlayer(myPlayer).hasTurn();
    }

    private void askHostToStart(Scanner scanner) throws IOException {
        while (true) {
            GameView.askHostToStart(START_GAME_COMMAND);
            String response = scanner.nextLine().strip();
            if (response.equalsIgnoreCase(START_GAME_COMMAND)) {
                GameInfo gameInfo = new GameInfo(client.getClientID(), client.getClientName());
                gameInfo.setUpdateType(UpdateType.GAME_STARTED);
                gameInfo.setGameHasStarted(true);
                client.sendMessage(gameInfo);
                return;
            }
        }
    }

    private void waitForHostReady() {
        GameView.displayWaitingForHostMessage();
        while (!gameHasStarted) {
            Thread.onSpinWait();
        }
    }

    private void waitForGameToStart() {
        while (myGame == null) {
            Thread.onSpinWait();
        }
    }

    private void runGame(Scanner scanner) throws IOException {

        if (isHost) {
            askHostToStart(scanner);
        }
        else {
            waitForHostReady();
        }

        GameView.displayGameIsStartingMessage();
        waitForGameToStart(); // wait for game to initialize and start
        while (!myPlayerLost && !gameHasEnded) {
            if (myPlayerHasTurn()) {
                performGameAction(scanner);
                myGame.getPlayer(myPlayer).setTurn(false);
            }
            waitForTurn();
        }
    }

    private void performGameAction(Scanner scanner) throws IOException {
        PlayerAction playerAction = getValidPlayerAction(scanner);
        int amount = getValidBet(playerAction, scanner);

        client.sendMessage(createPlayerActionInfo(playerAction, amount));
        myGame.getPlayer(myPlayer).setTurn(false);
    }

    private void waitForTurn() {
        while (!myPlayerHasTurn()) {
            Thread.onSpinWait();
        }
    }

    private GameInfo createPlayerActionInfo(PlayerAction playerAction, int amount) {
        GameInfo gameInfo = new GameInfo(client.getClientID(), myPlayer.name);

        gameInfo.setPlayerWithTurn(myGame.getPlayer(myPlayer));
        gameInfo.setPlayerAction(playerAction);
        gameInfo.setBetAmount(amount);
        gameInfo.setUpdateType(UpdateType.PLAYER_ACTION);

        return gameInfo;
    }

    private void listenForIncomingMessages() {
        try {
            // TODO  --
            while (client.isConnectedToServer() && !myPlayerLost) {
                GameInfo gameInfo = (GameInfo) client.listenForMessage();
                deconstructGameInfo(gameInfo);
            }
        }
        catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Something is wrong");
        }
    }

    private void deconstructGameInfo(GameInfo gameInfo) {
        if (gameInfo.getUpdateType() == null) {
            throw new NullPointerException("Update type cannot be null");
        }

        switch (gameInfo.getUpdateType()) {
            case GAME_STARTED -> startGame(gameInfo);
            case NEW_ROUND_STATE -> updateRoundState(gameInfo);
            case PLAYER_TURN -> updateTurn(gameInfo);
            case PLAYER_ACTION -> applyPlayerAction(gameInfo);
            case CONNECTION_STATUS -> playerConnectionUpdate(gameInfo);
            case SERVER_MESSAGE -> GameView.displayServerMessage(gameInfo.getServerMessage());
            case GAME_ENDED -> endGame(gameInfo);
        }
    }

    private void startGame(GameInfo gameInfo) {
        myGame = gameInfo.getGame();
        ArrayList<Player> players = new ArrayList<>(gameInfo.getPlayerHands().keySet());
        myGame.setPlayers(players);
        gameHasStarted = true;
    }

    private void endGame(GameInfo gameInfo) {
    }


    private void endGame() {
        client.closeEverything();
        System.exit(0);
    }

    private void updateRoundState(GameInfo gameInfo) {
        RoundState roundState = gameInfo.getRoundState();
        GameView.displayNewRoundState(roundState);
        switch (roundState) {
            case PRE_FLOP -> {
                updateRoles(gameInfo);
                updateAllHands(gameInfo);
                updateTurn(gameInfo);
            }
            case FLOP, TURN, RIVER -> {
                updateTableCards(gameInfo);
            }
            case SHOWDOWN -> {
                displayHands();
                displayWinners();
                myGame.giveChipsToWinners();
                displayLosers();
                myGame.removeLoses();
            }
        }
    }

    private void displayHands() {
        for (Player player: myGame.getPlayers()) {
            String handName = HandEval.getHandName(myGame.getScore(player));
            if (myPlayer.equals(player)) {
                GameView.displayMyHandRanking(handName);
            }
            else {
                GameView.displayPlayerHandRanking(player, handName);
            }
        }
    }

    private void displayWinners() {
        ArrayList<Player> winningPlayers = myGame.getWinningPlayers();
        String winningHand = HandEval.getHandName(myGame.getHighestScore());
        GameView.displayCurrentRoundWinners(winningPlayers, winningHand);
    }
    private void displayLosers() {
        ArrayList<Player> players = myGame.getPlayersWithNoChips();
        for (Player player: players) {
            // TODO - make lose game different for myPlayer
            if (player.equals(myPlayer)) {
                GameView.displayLoseGameScreen();
                endGame();
            }
            GameView.displayPlayerLostMessage(player);
        }
    }

    private void updateTableCards(GameInfo gameInfo) {
        ArrayList<Card> tableCards = gameInfo.getTableCards();
        myGame.setTableCards(tableCards);
        GameView.displayPlayerHUD(tableCards, myGame.getPlayerHand(myPlayer));
    }

    private void updateTurn(GameInfo gameInfo) {
        Player playerWithTurn = gameInfo.getPlayerWithTurn();
        myGame.setPlayerWithTurn(playerWithTurn);
        if (!playerWithTurn.equals(myPlayer)) {
            GameView.displayWaitingForPlayerMessage(playerWithTurn);
        }
    }

    private void updateRoles(GameInfo gameInfo) {
        HashMap<PokerRole, Player> hashMap = gameInfo.getRoles();
        myGame.setPlayerRoles(hashMap);
        GameView.displayRoles(hashMap);
    }

    private void updateAllHands(GameInfo gameInfo) {
        HashMap<Player, ArrayList<Card>> playerHands = gameInfo.getPlayerHands();
        for (Player player: playerHands.keySet()) {
            myGame.setPlayerHand(player, playerHands.get(player));
        }
    }

    private void applyPlayerAction(GameInfo gameInfo) {
        Player actingPlayer = gameInfo.getPlayerWithTurn();
        PlayerAction playerAction = gameInfo.getPlayerAction();
        int betAmount = gameInfo.getBetAmount();

        myGame.applyPlayerAction(actingPlayer, playerAction, betAmount);
        GameView.displayPlayerAction(actingPlayer, playerAction, betAmount);
    }

    private void playerConnectionUpdate(GameInfo gameInfo) {
        // TODO should not be playerWithTurn
        GameView.displayConnectionUpdate(gameInfo.getPlayerWithTurn(), gameInfo.getConnectionStatus());
    }

    private PlayerAction getValidPlayerAction(Scanner scanner) {
        GameView.displayReceivedTurnMessage();
        ArrayList<PlayerAction> validActions = myGame.getValidActions();

        while (true) {
            GameView.askForAnAction(validActions);
            String input = scanner.nextLine();
            if (PlayerAction.actionIsValid(input)) {
                PlayerAction action = PlayerAction.getActionByString(input);
                if (validActions.contains(action)) {
                    return action;
                }
            }
            GameView.displayInvalidActionMessage();
        }
    }

    private int getValidBet(PlayerAction playerAction, Scanner scanner) {
        if (!playerAction.isABet()) {
            return 0;
        }

        int amount = -1;
        // TODO - Make getMinBetAmount method
        int minimumAmount = myGame.getMinimumCallAmount();
        //TODO
        while (amount < minimumAmount /* !player.CanBet(amount)*/) {
            GameView.askForABetAmount(playerAction, minimumAmount, CANCEL_BET_VALUE);
            String input = scanner.nextLine().strip();

            if (isInteger(input)) {
                amount = Integer.parseInt(input);
                if (amount < minimumAmount) {
                    GameView.displayBetTooLowMessage(playerAction, minimumAmount);
                }
            }
            else {
                if (input.equalsIgnoreCase(CANCEL_BET_VALUE)) {
                    return -1;
                }
                GameView.displayInvalidValueMessage();
            }
        }
        return amount;
    }

}