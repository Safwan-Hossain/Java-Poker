import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientController {
    private Player player;
    private Client client;
    private Game game;
    private final boolean isHost;
    private boolean gameHasStarted;

    public ClientController(Socket socket, String username, boolean isHost) throws IOException {
        this.isHost = isHost;
        this.gameHasStarted = false;
        client = new Client(socket, username);
        player = new Player(username, 1000);

        ArrayList<Player> players = new ArrayList<>();
        players.add(player);
        game = new Game(players, 100);
    }

    public void startController(Scanner scanner) throws IOException {
        listenForIncomingMessages();

        while (!gameHasStarted) {
            if (isHost) {
                System.out.println("Type in \"START\" to start the game");
                String response = scanner.nextLine();
                if (response.equalsIgnoreCase("START")) {
                    break;
                }
            }
            else {
                // wait
            }
        }
        performGameAction(scanner);
    }

    private void listenForIncomingMessages() {
        if (client == null) {
            throw new RuntimeException("Client is not setup");
        }

        new Thread(() -> {
            while (client.IsConnectedToServer()) {
                try {
                    GameInfo gameInfo = (GameInfo) client.listenForMessage();
                    //System.out.println(gameInfo);
                    deconstructGameInfo(gameInfo);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
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
        return new GameInfo(client.getClientID(), player.name, playerAction, amount);
//        TODO
//        return new GameInfo(client.getClientID(), player.getName(), playerAction, amount);
    }

    private void deconstructGameInfo(GameInfo gameInfo) {
        switch (gameInfo.getUpdateType()) {
            case GAME_STATE -> changeGameState(gameInfo);
            case PLAYER_ACTION -> applyPlayerAction(gameInfo);
            case CONNECTION_STATUS -> playerConnectionUpdate(gameInfo);
            case SERVER_MESSAGE -> System.out.println("SERVER: " + gameInfo.getClientID());
        }
    }

    private void changeGameState(GameInfo gameInfo) {

    }

    private void applyPlayerAction(GameInfo gameInfo) {

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