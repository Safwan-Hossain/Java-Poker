package controller;

import server.Server;
import view.MainMenuView;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class MainController {
    //TODO - might want to change back to 3. Last 2 options are debugs.
    private static final int MAX_NUM_OPTIONS = 3;
    private static String username;

    private static ClientController clientController;
    private static Socket socket;
    private static Server server;

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
        int maxNumOptions = MAX_NUM_OPTIONS;

        while (option <= 0 || option > maxNumOptions) {
            MainMenuView.displayMainMenu();
            MainMenuView.askForMenuOption();
            if (scanner.hasNextInt()) {
                option = scanner.nextInt();
                scanner.nextLine();
            } else if (scanner.hasNextLine()) {
                scanner.nextLine();
            }

            if (option <= 0 || option > maxNumOptions) {
                MainMenuView.displayInvalidMenuOption();
            }
        }
        return option;
    }

    private static Socket getValidSocketForServer(Scanner scanner) {
        String serverIP;
        Socket socket;
        while (true) {
            MainMenuView.displayServerJoinMenu();
            serverIP = scanner.nextLine();
            try {
                if (!serverIP.isBlank()) {
                    socket = new Socket(serverIP, 101);
                    break;
                }
            }
            catch (IOException e) {
                MainMenuView.displayFailedToConnectToServer(serverIP);
            }
        }
        System.out.println(serverIP);
        MainMenuView.displaySuccessfulConnection();
        return socket;
    }

    private static void hostServer() throws IOException {
        // catch

        //if a server exists and is not closed
        if (server != null && !server.isClosed()) {
            System.out.println("Already hosting a server!");
            InetAddress localIP = InetAddress.getLocalHost();
            MainMenuView.displayServerIPAddress(localIP.toString());
            return;
        }

        ServerSocket serverSocket = new ServerSocket(101);
        server = new Server(serverSocket);
        new Thread(server::startServer).start();
        // catch
        InetAddress localIP = InetAddress.getLocalHost();
        MainMenuView.displaySuccessfullyStartedServer();
        MainMenuView.displayServerIPAddress(localIP.toString());
    }

    private static void joinServer(Scanner scanner) throws IOException {
        socket = getValidSocketForServer(scanner);
        username = getValidUsername(scanner);
        final boolean isHost = server != null;
        clientController = new ClientController(socket, username, isHost);
        clientController.startController(scanner);
    }

    private static void joinServer(Scanner scanner, InetAddress serverIP) throws IOException {
        socket = new Socket(serverIP, 101);
        username = getValidUsername(scanner);
        clientController = new ClientController(socket, username, true);
        clientController.startController(scanner);
    }

    private static void exitProgram() {
        if (server != null) {
            server.closeServer();
        }
        System.exit(0);
    }

    private static void performMainMenuOperation(Scanner scanner, int option) throws IOException {
        switch (option) {
            case 1 -> {
                hostServer();
                joinServer(scanner, InetAddress.getLocalHost());
            }
            case 2 -> joinServer(scanner);
            case 3 -> exitProgram();
            case 4 -> quickStartAndJoinServer(scanner);
            case 5 -> quickJoinLocalServer(scanner);
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

    // =-=-= DEBUG METHODS =-=-=
    private static void quickStartAndJoinServer(Scanner scanner) throws IOException {
        hostServer();
        socket = new Socket(InetAddress.getLocalHost(), 101);
        username = "Host";
        clientController = new ClientController(socket, username, true);
        clientController.startController(scanner);
    }

    private static void quickJoinLocalServer(Scanner scanner) throws IOException {
        socket = new Socket(InetAddress.getLocalHost(), 101);
        username = "model.Player " + (int) (Math.random() * 100 + 1);
        clientController = new ClientController(socket, username, false);
        clientController.startController(scanner);
    }
}
