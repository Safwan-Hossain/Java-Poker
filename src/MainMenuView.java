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
        System.out.println("The IP address for this server is: " + serverIP);
    }

    public static void displayServerJoinMenu() {
        System.out.println("Please enter the host's server IP: ");
    }

    public static void displayFailedToConnectToServer(String IP) {
        System.out.println("Failed to connect to server IP: " + IP);
        System.out.println("Please try again.");
    }

    public static void displaySuccessfullyStartedServer() {
        System.out.println("Successfully started the server!");
    }

    public static void displaySuccessfulConnection() {
        System.out.println("Successfully connected to the server!");
    }

    public static void displayWaitingForHost() {
        System.out.println("Waiting for the server host to start the game...");
    }

    public static void displayExitingProgram() {
        System.out.println("Closing Game...");
    }
}
