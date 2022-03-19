import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientController {
    private Player player;
    private Client client;
    private Game game;

    public ClientController(Socket socket, String username) throws IOException {
        client = new Client(socket, username);
        player = new Player(username, 1000);

        ArrayList<Player> players = new ArrayList<>();
        players.add(player);
        game = new Game(players, 100);
    }

    public void startController() throws IOException {
        Scanner scanner = new Scanner(System.in);
        listenForIncomingMessages();
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
                    System.out.println(gameInfo);
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
        client.sendMessage(CreateGameInfo(playerAction, amount));
    }

    private GameInfo CreateGameInfo(PlayerAction playerAction, int amount) {
        return new GameInfo(client.getClientID(), player.name, playerAction, amount);
//        TODO
//        return new GameInfo(client.getClientID(), player.getName(), playerAction, amount);
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