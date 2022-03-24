import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class ClientController {
    private Player thisPlayer;
    private Client client;
    private volatile Game thisGame;
    private final boolean isHost;
    private volatile boolean gameHasStarted;

    public ClientController(Socket socket, String username, boolean isHost) throws IOException {
        this.isHost = isHost;
        this.gameHasStarted = false;
        client = new Client(socket, username);
    }

    public void startController(Scanner scanner) throws IOException {
        GameInfo clientSetUpInfo = new GameInfo("", client.getClientName());
        client.sendMessage(clientSetUpInfo);
        try {
            clientSetUpInfo = (GameInfo) client.listenForMessage();
            client.setClientID(clientSetUpInfo.getClientID());
        } catch (ClassNotFoundException e) {
        }
        System.out.println("Client Name: " + client.getClientName() + " | ID: " + client.getClientID());
        thisPlayer = new Player(client.getClientName(), client.getClientID());

        listenForIncomingMessages();
        while (!gameHasStarted) {
            if (isHost) {
                System.out.println("Type in \"START\" to start the game");
                String response = scanner.nextLine();
                if (response.equalsIgnoreCase("START")) {
                    GameInfo gameInfo = new GameInfo(client.getClientID(), client.getClientName());

                    // TODO - set NEW_ROUND to game start
                    gameInfo.setUpdateType(UpdateType.NEW_ROUND);
                    gameInfo.setGameHasStarted(true);
                    client.sendMessage(gameInfo);
                    break;
                }
            }
            else {
                System.out.println("Waiting for host to start the game...");
                while (!gameHasStarted) {
                    Thread.onSpinWait();
                }
            }
        }

        while (thisGame == null) {
            Thread.onSpinWait();
        }

        while (true) {
            if (thisGame.getPlayer(thisPlayer).hasTurn()) {
                performGameAction(scanner);
                thisGame.getPlayer(thisPlayer).setTurn(false);
            } else {
                System.out.println("Waiting for player to move");
                while (!thisGame.getPlayer(thisPlayer).hasTurn()) {
                    Thread.onSpinWait();
                }
            }
        }
    }

    private void listenForIncomingMessages() {
        if (client == null) {
            throw new RuntimeException("Client is not setup");
        }

        new Thread(() -> {
            try {
                while (client.IsConnectedToServer()) {
                    GameInfo gameInfo = (GameInfo) client.listenForMessage();
                    deconstructGameInfo(gameInfo);
                }
            }
            catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException("Something is wrong");
            }
        }).start();
    }

    private void performGameAction(Scanner scanner) throws IOException {
        PlayerAction playerAction = getValidPlayerAction(scanner);
        int amount = 0;
        if (playerAction.isABet()) {
            amount = getValidBet(scanner);
        }

        client.sendMessage(createGameInfo(playerAction, amount));
    }

    private GameInfo createGameInfo(PlayerAction playerAction, int amount) {
        GameInfo gameInfo = new GameInfo(client.getClientID(), thisPlayer.name);
        gameInfo.setPlayerWithTurn(thisGame.getPlayer(thisPlayer));
        gameInfo.setPlayerAction(playerAction);
        gameInfo.setBetAmount(amount);
        gameInfo.setUpdateType(UpdateType.PLAYER_ACTION);
        return gameInfo;

//        TODO
//        return new GameInfo(client.getClientID(), player.getName(), playerAction, amount);
    }

    private void deconstructGameInfo(GameInfo gameInfo) {
        if (gameInfo.getUpdateType() == null) {
            throw new RuntimeException("Update type cannot be null");
        }
//        System.out.println(gameInfo.getUpdateType());
        switch (gameInfo.getUpdateType()) {
            case GAME_STARTED -> startGame(gameInfo);
            case NEW_ROUND -> initializeRound(gameInfo);
            case NEW_ROUND_STATE -> updateRoundState(gameInfo);
            case PLAYER_TURN -> updateTurn(gameInfo);
            case PLAYER_ACTION -> applyPlayerAction(gameInfo);
            case CONNECTION_STATUS -> playerConnectionUpdate(gameInfo);
            case SERVER_MESSAGE -> System.out.println("SERVER: " + gameInfo.getClientID());
            case GAME_ENDED -> endGame(gameInfo);
        }
        gameInfo = null;
    }
    private void startGame(GameInfo gameInfo) {
        System.out.println("The game is starting...");
        thisGame = gameInfo.getGame();
        gameHasStarted = true;
    }

    private void endGame(GameInfo gameInfo) {

    }

    private void updateRoundState(GameInfo gameInfo) {
        ArrayList<Card> tableCards = gameInfo.getTableCards();
        System.out.println("TABLE CARDS: " + tableCards.toString());
        thisGame.setTableCards(tableCards);
    }

    private void initializeRound(GameInfo gameInfo) {
        while (thisGame == null) {
            Thread.onSpinWait();
        }

        System.out.println("DEBUG: New Round");

        Game mainGame = gameInfo.getGame();
        updateRoles(mainGame);
        updateAllHands(gameInfo);
        updateTurn(gameInfo);
        System.out.println(mainGame.getPlayerHand(thisPlayer));
    }

    private void updateTurn(GameInfo gameInfo) {
        Player playerWithTurn = gameInfo.getPlayerWithTurn();
        thisGame.setPlayerWithTurn(playerWithTurn);
    }

    private void updateRoles(Game mainGame) {
        HashMap<PokerRole, Player> hashMap = mainGame.getPlayersWithRoles();
        thisGame.setPlayerRoles(hashMap);

        Player dealer = hashMap.get(PokerRole.DEALER);
        Player smallBlind = hashMap.get(PokerRole.SMALL_BLIND);
        Player bigBlind = hashMap.get(PokerRole.BIG_BLIND);

        System.out.println(dealer.getName().toUpperCase() + " is the dealer.");
        System.out.println(smallBlind.getName().toUpperCase() + " is the small blind.");
        System.out.println(bigBlind.getName().toUpperCase() + " is the big blind.");
    }

    private void updateAllHands(GameInfo gameInfo) {
        HashMap<Player, ArrayList<Card>> playerHands = gameInfo.getPlayerHands();
        for (Player player: playerHands.keySet()) {
            thisGame.setPlayerHand(player, playerHands.get(player));
        }
    }

    private void applyPlayerAction(GameInfo gameInfo) {
        Player actingPlayer = gameInfo.getPlayerWithTurn();
        PlayerAction playerAction = gameInfo.getPlayerAction();
        int betAmount = gameInfo.getBetAmount();
        thisGame.applyPlayerAction(actingPlayer, playerAction, betAmount);

        if (betAmount == 0) {
            System.out.println(actingPlayer.getName().toUpperCase() + " performs the action " + playerAction.name());
        }
        System.out.println(actingPlayer.getName().toUpperCase() + " performs the action " + playerAction.name() + " for an amount of " + betAmount);
    }

    private void playerConnectionUpdate(GameInfo gameInfo) {
        if (!gameInfo.hasGameStarted()) {
            String action = "";
            switch (gameInfo.getConnectionStatus()) {
                case JOINED -> action = "joined";
                case DISCONNECTED -> action = "disconnected from";
                case RECONNECTED -> action = "reconnected from";
            }
            System.out.println(gameInfo.getPlayerName() + " has " + action + " the table!");
        }
    }


    private PlayerAction getValidPlayerAction(Scanner scanner) {
        while (true) {
            System.out.println("Enter an action: ");
            String actionToMake = scanner.nextLine();
            if (PlayerAction.actionIsValid(actionToMake)) {
                return PlayerAction.getActionByString(actionToMake);
            }
            System.out.println("Invalid action");
        }
    }

    private int getValidBet(Scanner scanner) {
        int amount = 0;
        //TODO
        while (amount <= 0 /* !player.CanBet(amount)*/) {
            System.out.println("How much do you want to bet/raise?: ");
            if (scanner.hasNextInt()) {
                amount = scanner.nextInt();
                scanner.nextLine();
            }
            if (amount <= 0) {
                System.out.println("Invalid betting amount. Try again.");
            }
        }
        return amount;
    }

}