package view;

public class MainMenuView {

    public static void displayWelcomeScreen() {
    System.out.println("=======================================");
    System.out.println(" ======= WELCOME TO JAVA POKER ======= ");
    System.out.println("=======================================");
}
    public static void displayMainMenu() {
        System.out.println("PLEASE SELECT AN OPTION:        ");
        System.out.println();
        System.out.println("    1. HOST A NEW SERVER");
        System.out.println("    2. JOIN AN EXISTING SERVER");
        System.out.println("    3. EXIT GAME");
        System.out.println("=======================================");
    }

    public static void askForMenuOption() {
        System.out.println("Please enter your option: ");
    }

    public static void displayInvalidMenuOption() {
        System.out.println("Invalid option.");
    }

    public static void askForUsername() {
        System.out.println("Please enter your username: ");
    }

    public static void displayInvalidUsername() {
        System.out.println("The username you entered is invalid.");
    }

    public static void displayServerIPAddress(String serverIP) {
        System.out.println("The IP address for this main.server is: " + serverIP);
    }

    public static void displayServerJoinMenu() {
        System.out.println("Please enter the host's main.server IP: ");
    }

    public static void displayFailedToConnectToServer(String serverIP) {
        System.out.println("Failed to connect to main.server IP.");
        System.out.println("Please try again.");
    }

    public static void displaySuccessfullyStartedServer() {
        System.out.println("Successfully started the main.server!");
    }

    public static void displaySuccessfulConnection() {
        System.out.println("Successfully connected to the main.server!");
    }

    public static void displayWaitingForHost() {
        System.out.println("Waiting for the main.server host to start the main.model.game...");
    }

    public static void displayExitingProgram() {
        System.out.println("Closing main.model.game.Game...");
    }
}
