import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Scanner;

public class Client {
    private Player thisPlayer;
    private Game game;
    private String playerName;
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    public Client(Socket socket, String name) {
        this.socket = socket;
        this.playerName = name;

        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            closeEverything();
        }
    }

    // ============== Temporary Functions ==============

    private boolean actionIsValid(String action) {
        return action.equals("bet") || action.equals("call") || action.equals("raise") || action.equals("fold");
    }

    private GameInfo.playerActions getActionByString(String action) {
        if (action.equals("bet")) {
            return GameInfo.playerActions.BET;
        }
        if (action.equals("call")) {
            return GameInfo.playerActions.CALL;
        }
        if (action.equals("raise")) {
            return GameInfo.playerActions.RAISE;
        }
        if (action.equals("fold")) {
            return GameInfo.playerActions.FOLD;
        }
        throw new RuntimeException("Invalid Action");
    }

    private GameInfo createGameInfo (String action) {
        if (actionIsValid(action)) {
            if (action.equals("bet") || action.equals("raise")) {
                Scanner scanner = new Scanner(System.in);
                System.out.print("How much do you want to bet/raise?: ");
                int amount = Integer.parseInt(scanner.nextLine());
                return new GameInfo(playerName, getActionByString(action), amount);
            } else {
                return new GameInfo(playerName, getActionByString(action), 0);
            }
        }
        return null;
    }

    // ============== ===========  ==============

    private void performAction() {
        if (socket == null || outputStream == null) {
            return;}

        Scanner scanner =  new Scanner(System.in);
        try {
            while (socket.isConnected()) {
                System.out.println("SELECT AN ACTION: BET, FOLD, RAISE, CALL");
                String action = scanner.nextLine().toLowerCase().strip();
                GameInfo gameInfo = createGameInfo(action);

                if (gameInfo == null) {
                    System.out.println("Invalid input");
                    continue;
                }

                outputStream.writeObject(gameInfo);
                outputStream.flush();
            }
        } catch (IOException e) {
            closeEverything();
        }
    }

    private void listenForMessages() {
        new Thread(() -> {
            while (socket.isConnected()) {
                try {
                    GameInfo gameInfo = (GameInfo) inputStream.readObject();
                    System.out.println(gameInfo.toString());
                } catch (IOException | ClassNotFoundException e) {
                    closeEverything();
                    break;
                }
            }
        }).start();
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

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please print your username: ");
        String name = scanner.nextLine();
        Socket socket = new Socket(InetAddress.getLocalHost(), 100);
        Client client = new Client(socket, name);
        client.listenForMessages();
        client.performAction();
    }
}

