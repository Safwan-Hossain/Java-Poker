import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class MainMenuController {
    static final int MAX_NUM_OPTIONS = 3;

    private static String getValidUsername() {
        Scanner scanner = new Scanner(System.in);
        String username = "";

        while (username.strip().isBlank()) {
            MainMenuView.askForUsername();
            username = scanner.nextLine();
            System.out.println();

            if (username.strip().isBlank()) {
                System.out.println("Invalid username.");
            }
        }
        scanner.close();
        return username;
    }

    private static int getValidOption(int maxNumOptions) {
        Scanner scanner = new Scanner(System.in);
        int option = 0;

        while (option <= 0 || option > maxNumOptions) {
            System.out.print("Please enter your option: ");
            if (scanner.hasNextInt()) {
                option = scanner.nextInt();
            }
            else {
                System.out.println("Invalid option.");
            }
            System.out.println();
        }
        scanner.close();
        return option;
    }

    private static Socket getValidSocketForServer() {
        Scanner scanner = new Scanner(System.in);
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
        scanner.close();
        return socket;
    }

    private static void joinServer()  {
        String username = getValidUsername();
        Socket socket = getValidSocketForServer();

        Client client = new Client(socket, username);
        client.startClient();
    }

    private static void hostServer() throws IOException {
        // Host server
        ServerSocket serverSocket = new ServerSocket(100);
        Server server = new Server(serverSocket);
        server.startServer();
        MainMenuView.displayServerIPAddress(InetAddress.getLocalHost().toString());
        // Join server
        String username = getValidUsername();
        Socket socket = new Socket(InetAddress.getLocalHost(), 100);
        Client client = new Client(socket, username);
        client.startClient();
    }

    private static void exitProgram() {
        System.exit(0);
    }

    private static void performMainMenuOperation() throws IOException {
        MainMenuView.displayMainMenu();
        int option = getValidOption(MAX_NUM_OPTIONS);

        switch (option) {
            case 1 -> hostServer();
            case 2 -> joinServer();
            case 3 -> exitProgram();
            default -> { }
        }

    }

    public static void EnterProgram() throws IOException {
        performMainMenuOperation();
    }


}
