import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class MainController {
    private static String username;
    private static Socket socket;
    private static Client client;

    private static String getValidUsername(Scanner scanner) {
        String username = "";
        while (username.strip().isBlank()) {
            MainMenuView.askForUsername();
            username = scanner.nextLine();

            if (username.strip().isBlank()) {
                MainMenuView.displayInvalidUsername();
            }
        }
        return username;
    }

    private static int getValidOption(Scanner scanner) {
        int option = 0;
        int maxNumOptions = MainMenu.getMaxNumOptions();

        while (option <= 0 || option > maxNumOptions) {
            MainMenuView.displayMainMenu();
            MainMenuView.askForMenuOption();
            if (scanner.hasNextInt()) {
                option = scanner.nextInt();
                scanner.nextLine();
            }

            if (option <= 0 || option > maxNumOptions) {
                MainMenuView.displayInvalidMenuOption();
            }
        }
        return option;
    }

    private static Socket getValidSocketForServer(Scanner scanner) {
        String serverIP = "";
        Socket socket;
        while (true) {
            try {
                MainMenuView.displayServerJoinMenu();
                serverIP = scanner.nextLine();
                socket = new Socket(serverIP, 100);
                break;
            }
            catch (IOException e) {
                MainMenuView.displayFailedToConnectToServer(serverIP);
            }
        }
        MainMenuView.displaySuccessfulConnection();
        return socket;
    }

    private static void startClient() {
        if (client == null) {
            throw new NullPointerException("CLIENT IS NULL");
        }
    }

    private static void hostServer(Scanner scanner) throws IOException {
        MainMenu.hostServer();
        InetAddress localIP = InetAddress.getLocalHost();
        MainMenuView.displaySuccessfullyStartedServer();
        MainMenuView.displayServerIPAddress(localIP.toString());
    }

    private static void joinServer(Scanner scanner) throws IOException {
        socket = getValidSocketForServer(scanner);
        username = getValidUsername(scanner);
        MainMenu.joinServer(socket, username);
        //TODO start client
    }

    private static void exitProgram() {
        MainMenuView.displayExitingProgram();
        MainMenu.exitProgram();
    }

    private static void performMainMenuOperation(Scanner scanner, int option) throws IOException {
        switch (option) {
            case 1 -> hostServer(scanner);
            case 2 -> joinServer(scanner);
            case 3 -> exitProgram();
            default -> { }
        }
    }

    public static void enterProgram() throws IOException {
        MainMenuView.displayWelcomeScreen();
        System.out.println();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            int option = getValidOption(scanner);
            performMainMenuOperation(scanner, option);
        }
    }

    public static void main(String[] args) throws IOException {
        enterProgram();
    }




}
