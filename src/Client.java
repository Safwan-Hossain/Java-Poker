import java.io.*;
import java.net.Socket;
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

    private boolean actionIsValid(String action) {
        return action.equals("bet") || action.equals("call") || action.equals("raise") || action.equals("fold");
    }

    private void performAction() {
        if (socket == null || outputStream == null) { return;}

        Scanner scanner =  new Scanner(System.in);
        try {
            while (socket.isConnected() && thisPlayer.hasTurn()) {
                String action = scanner.nextLine().toLowerCase();
                if (actionIsValid(action)) {
                    outputStream.writeChars(action);
                    outputStream.flush();
                    break;
                }
                System.out.println("Invalid action");
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
                    game = gameInfo.getGame();
                    String message = gameInfo.getMessage();
                    if (!message.isEmpty()) {
                        System.out.println(message);
                    }
                    String playerWithTurn = gameInfo.getPlayerWithTurn();
                    if (playerWithTurn.equals(thisPlayer.name)) {
                        thisPlayer.giveTurn();
                    }
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

