package controller;

import enumeration.PlayerAction;
import enumeration.PokerRole;
import enumeration.RoundState;
import model.game.Game;
import model.player.Card;
import model.client.Client;
import model.update.GameInfo;
import model.player.Player;
import view.GameView;

import java.io.IOException;
import java.net.Socket;
import java.util.*;

import static enumeration.UpdateType.*;

public class ClientController {
    private Player myPlayer;
    private Client client;
    private final boolean isHost;
    private volatile Game myGame;
    private volatile boolean gameHasStarted;
    private volatile boolean gameHasEnded;
    private volatile boolean myPlayerLost;
    private List<Thread> runningThreads;

    private final String CANCEL_BET_VALUE = "B";
    private final String START_GAME_COMMAND = "START";

    // Multiply coefficients by the small blinds to determine max and min buy in.
    // Generally casinos use 20-50 big blinds for minimum buy in and 100-250 for maximum buy in.
    private final int MIN_BUY_IN_COEFFICIENT = 40 * 2;
    private final int MAX_BUY_IN_COEFFICIENT = 250 * 2;

    private final int MIN_SMALL_BLIND = 10;
    private final int MAX_SMALL_BLIND = Integer.MAX_VALUE / MAX_BUY_IN_COEFFICIENT ;

    public ClientController(Socket socket, String username, boolean isHost) throws IOException {
        this.isHost = isHost;
        this.gameHasStarted = false;
        this.client = new Client(socket, username);
        this.runningThreads = new ArrayList<>();
    }

    public void startController(Scanner scanner) throws IOException {
        setUpClient(); // sets up client name and ID with main.server
        GameView.displayClientInformation(client);
        setUpMyPlayer(); // sets up player using client name and ID

        Thread listeningThread = new Thread(this::listenForIncomingMessages); // listens for messages from main.server on separate thread
        runningThreads.add(listeningThread);
        listeningThread.start();

        runGame(scanner);
        if (myPlayerLost && !isHost) {
            GameView.displayLoseGameScreen();
            GameView.displayExitMessage();
            leaveTable();
        }
        else if (myPlayerLost && isHost) {
            GameView.displayLoseGameScreen();
            waitForGameFinish();
            GameView.displayExitMessage();
            leaveTable();
        }
    }

    private void waitForGameFinish() {
        System.out.println("Since you are the host you will have to wait for the main.model.game to finish...");
        while(!gameHasEnded) {
            Thread.onSpinWait();
        }
    }

    private void setUpClient() throws IOException {
        GameInfo clientInfo = new GameInfo("", client.getClientName());
        clientInfo.setIsHost(isHost);
        client.sendMessageOld(clientInfo);
        try {
            clientInfo = (GameInfo) client.receiveMessageOld();
            client.setClientID(clientInfo.getClientID());
        } catch (ClassNotFoundException ignored) {
        }
    }

    private void setUpMyPlayer() {
        myPlayer = new Player(client.getClientName(), client.getClientID());
    }

    private synchronized boolean myPlayerHasTurn() {
//        if (!myGame.getPlayers().contains(myPlayer)) {
//            return false;
//        }
//        return myGame.getPlayer(myPlayer).hasTurn();
          return myPlayer.hasTurn();
    }

    private void askHostToStart(Scanner scanner) throws IOException {
        while (true) {
            GameView.askHostToStart(START_GAME_COMMAND);
            String response = scanner.nextLine().strip();
            if (response.equalsIgnoreCase(START_GAME_COMMAND)) {
                String gameSettings = "GAME SETTINGS";
                String message = gameSettings + "\n" + "=".repeat(gameSettings.length());
                System.out.println(message);
                int smallBlind = getValidSmallBlind(scanner);
                int buyIn = getValidBuyIn(scanner, smallBlind);

                GameInfo gameInfo = new GameInfo(client.getClientID(), client.getClientName());
                gameInfo.setUpdateType(GAME_STARTED);
                gameInfo.setGameHasStarted(true);
                gameInfo.setBuyIn(buyIn);
                gameInfo.setSmallBlind(smallBlind);
                client.sendMessageOld(gameInfo);
                return;
            }
        }
    }

    private int getValidSmallBlind(Scanner scanner) {
        while (true) {
            System.out.println("Please specify the small blind: ");
            String input = scanner.nextLine();
            if (isInteger(input)) {
                int smallBlind = Integer.parseInt(input);
                if (smallBlind > MAX_SMALL_BLIND) {
                    System.out.println("The number you entered is too large. Max small blind: " + MAX_SMALL_BLIND);
                }
                else if (smallBlind < MIN_SMALL_BLIND) {
                    System.out.println("The number you entered is too small. Min small blind: " + MIN_SMALL_BLIND);
                }
                else {
                    return smallBlind;
                }
            }
        }
    }

    private int getValidBuyIn(Scanner scanner, int smallBlind) {
        final int MIN_BUY_IN = smallBlind * MIN_BUY_IN_COEFFICIENT;
        final int MAX_BUY_IN = smallBlind * MAX_BUY_IN_COEFFICIENT;

        while (true) {
            System.out.println("Please specify the buy in (all players will be given the buy in): ");
            String input = scanner.nextLine();
            if (isInteger(input)) {
                int buyIn = Integer.parseInt(input);
                if (buyIn > MAX_BUY_IN) {
                    System.out.println("The number you entered is too large. Max buy in: " + MAX_BUY_IN);
                }
                else if (buyIn < MIN_BUY_IN) {
                    System.out.println("The number you entered is too small. Min buy in: " + MIN_BUY_IN);
                }
                else {
                    return buyIn;
                }
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
        waitForGameToStart(); // wait for main.model.game to initialize and start
        while (!myPlayerLost && !gameHasEnded) {
            if (myPlayerHasTurn()) {
                performGameAction(scanner);
                myPlayer.setTurn(false);
//                myGame.getPlayer(myPlayer).setTurn(false);
            }
            waitForTurn();
        }
    }

    private void performGameAction(Scanner scanner) throws IOException {
        if (myGame.getPlayer(myPlayer).isBankrupt()) {
            // TODO - remove sleep
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            client.sendMessageOld(getPlayerActionInfo(PlayerAction.WAIT, 0));
            return;
        }
        PlayerAction playerAction = null;
        int amount = -1;

        GameView.displayReceivedTurnMessage();
        while (amount < 0) {
            playerAction = getValidPlayerAction(scanner);
            if (!playerAction.isABet()) { break; }
            amount = getValidBet(playerAction, scanner);
        }
        client.sendMessageOld(getPlayerActionInfo(playerAction, amount));
    }

    private void waitForTurn() {
        while (!gameHasEnded && !myPlayerLost && !myPlayerHasTurn()) {
            Thread.onSpinWait();
        }
    }

    private GameInfo getPlayerActionInfo(PlayerAction playerAction, int amount) {
        GameInfo gameInfo = new GameInfo(client.getClientID(), myPlayer.getName());

        gameInfo.setPlayerWithTurn(myPlayer);
        gameInfo.setPlayerAction(playerAction);
        gameInfo.setBetAmount(amount);
        gameInfo.setUpdateType(PLAYER_ACTION);

        return gameInfo;
    }

    private void listenForIncomingMessages() {
        try {
            // TODO  --
            while (client.isConnectedToServer() && !(myPlayerLost && !isHost)) {
                GameInfo gameInfo = (GameInfo) client.receiveMessageOld();
                deconstructGameInfo(gameInfo);
            }
        }
        catch (IOException | ClassNotFoundException e) {
            //TODO
//            throw new RuntimeException("Something is wrong");
        }
    }

    private void deconstructGameInfo(GameInfo gameInfo) {
        if (gameInfo.getUpdateType() == null) {
            throw new NullPointerException("Update type cannot be null");
        }


        switch (gameInfo.getUpdateType()) {
            case GAME_ENDED -> {
                Player winner = gameInfo.getWinningPlayers().get(0);
                GameView.displayGameOverScreen(winner);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {}
                gameHasEnded = true;
            }
            case GAME_STARTED -> startGame(gameInfo);
            case LAST_UNFOLDED_PLAYER_WINS -> lastUnfoldedPlayerWins(gameInfo);
            case NEW_ROUND_STATE -> updateRoundState(gameInfo);
            case PLAYER_QUIT -> {}
            case PLAYER_TURN -> updateTurn(gameInfo);
            case PLAYER_ACTION -> applyPlayerAction(gameInfo);
            case CONNECTION_STATUS -> playerConnectionUpdate(gameInfo);
            case SERVER_MESSAGE -> GameView.displayServerMessage(gameInfo.getServerMessage());
        }
    }

    private void lastUnfoldedPlayerWins(GameInfo gameInfo) {
        updatePlayers(gameInfo);
        displayUnfoldedWinner(gameInfo);
        displayLosers(gameInfo);
        checkIfMyPlayerLost(gameInfo);
        myGame.endRound();
    }

    private void startGame(GameInfo gameInfo) {
        myGame = gameInfo.getGame();
        List<Player> players = new ArrayList<>(gameInfo.getPlayerHands().keySet());
        myGame.setPlayers(players);
        gameHasStarted = true;
    }

    private void leaveTable() {
        for (Thread thread: runningThreads) {
            thread.interrupt();
        }

        GameInfo quitGameInfo = new GameInfo(client.getClientID(), client.getClientName());
        quitGameInfo.setUpdateType(PLAYER_QUIT);

        try {
            client.sendMessageOld(quitGameInfo);
        } catch (IOException ignored) {
        }
    }

    private void updateRoundState(GameInfo gameInfo) {
        RoundState roundState = gameInfo.getRoundState();
        GameView.displayNewRoundState(roundState);
        myGame.endRoundState();
        switch (roundState) {
            case PRE_FLOP -> {
                updateRoles(gameInfo);
                updateAllHands(gameInfo);
                myGame.takeChipsFromBlinds();
                DisplayMyPlayerHUD();
                updateTurn(gameInfo);
            }
            case FLOP, TURN, RIVER -> {
                updateTableCards(gameInfo);
            }
            case SHOWDOWN -> {
                updateAllHands(gameInfo);
                displayHands();
                updatePlayers(gameInfo);
                displayWinners(gameInfo);
                displayLosers(gameInfo);
                checkIfMyPlayerLost(gameInfo);
                myGame.endRound();
            }
        }
    }

    private void DisplayMyPlayerHUD() {
        List<Card> tableCards = myGame.getTableCards();
        List<Card> myHand = myGame.getPlayerHand(myPlayer);
        int totalPot = myGame.getTotalPot();
        int myChips = myGame.getPlayer(myPlayer).getChips();
        GameView.displayPlayerHUD(tableCards, myHand, totalPot, myChips);
    }

    private void updatePlayers(GameInfo gameInfo) {
        List<Player> players = new ArrayList<>(gameInfo.getPlayers());
        // TODO - FIND BETTER WAY TO HANDLE THIS -- CURRENTLY TRIES TO AVOID DIFFERENT THREAD CHECKING IF PLAYER HAS TURN
        // IF player is removed then there is null exception
        if (!players.contains(myPlayer)) {
            players.add(myPlayer);
        }

        myGame.setPlayers(players);
    }

    private void displayHands() {
        for (Player player: myGame.getPlayers()) {
            String handName = myGame.getPlayerHandName(player);
            if (myPlayer.equals(player)) {
                GameView.displayMyHandRanking(handName);
            }
            else {
                GameView.displayPlayerHandRanking(player, handName);
            }
        }
    }

    private void displayUnfoldedWinner(GameInfo gameInfo) {
        List<Player> winningPlayers = gameInfo.getWinningPlayers();
        GameView.displayLastUnfoldedPlayer(winningPlayers.get(0));
    }

    private void displayWinners(GameInfo gameInfo) {
        List<Player> winningPlayers = gameInfo.getWinningPlayers();
        String nameOfWinningHand = gameInfo.getNameOfWinningHand();
        GameView.displayCurrentRoundWinners(winningPlayers, nameOfWinningHand);
    }

    private void checkIfMyPlayerLost(GameInfo gameInfo) {
        List<Player> playersWhoLost = gameInfo.getLosingPlayers();
        if (playersWhoLost.contains(myPlayer)) {
            myPlayerLost = true;
        }
    }

    private void displayLosers(GameInfo gameInfo) {
        List<Player> playersWhoLost = gameInfo.getLosingPlayers();
        for (Player player: playersWhoLost) {
            if (!player.equals(myPlayer)) {
                GameView.displayPlayerLostMessage(player);
            }
        }
    }

    private void updateTableCards(GameInfo gameInfo) {
        List<Card> tableCards = gameInfo.getTableCards();
        myGame.setTableCards(tableCards);
        GameView.displayPlayerHUD(tableCards, myGame.getPlayerHand(myPlayer), myGame.getTotalPot(), myGame.getPlayer(myPlayer).getChips());
    }

    private void updateTurn(GameInfo gameInfo) {
        Player playerWithTurn = gameInfo.getPlayerWithTurn();
        if (playerWithTurn.equals(myPlayer)) {
            myPlayer.setTurn(true);
        }
        myGame.setPlayerWithTurn(playerWithTurn);
        if (!playerWithTurn.equals(myPlayer)) {
            GameView.displayWaitingForPlayerMessage(playerWithTurn);
        }
    }

    private void updateRoles(GameInfo gameInfo) {
        Map<PokerRole, Player> roleMap = gameInfo.getRoles();
        myGame.setPlayerRoles(roleMap);
        GameView.displayRoles(roleMap);
    }

    private void updateAllHands(GameInfo gameInfo) {
        Map<Player, List<Card>> playerHands = gameInfo.getPlayerHands();
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
        GameView.displayConnectionUpdate(gameInfo.getPlayerName(), gameInfo.getConnectionStatus());
    }

    private PlayerAction getValidPlayerAction(Scanner scanner) {
        HashSet<PlayerAction> validActions = myGame.getValidActions(myPlayer);

        while (true) {
            System.out.println("Minimum call amount: " + myGame.getMinimumCallAmount());
            System.out.println("Minimum bet amount: " + myGame.getMinimumBetAmount());
            System.out.println("Your side pot: " + myGame.getPlayerSidePot(myPlayer));
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
        int amount = -1;
        int minimumAmount = Math.min(myGame.getMinimumBetAmount(), myGame.getPlayerBettingPower(myPlayer));

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

        return Math.min(amount, myGame.getPlayerBettingPower(myPlayer));
    }

    private boolean isInteger(String string) {
        try {
            Integer.parseInt(string);
            return true;
        }
        catch(NumberFormatException ignored) {
            return false;
        }
    }

}