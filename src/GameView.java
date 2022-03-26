import java.util.ArrayList;
import java.util.HashMap;

public class GameView {
    public static void displayClientInformation(String clientName, String clientID){
        System.out.println("Client Name: " + clientName + " | ID: " + clientID);
    }
    public static void askHostToStart(){
        System.out.println("Type in \"START\" to start the game.");
    }
    public static void displayWaitingForHostMessage(){
        System.out.println("Waiting for host to start the game...");
    }
    public static void displayWaitingForPlayerMessage(String playerName){
        System.out.println("Waiting for " + playerName.toUpperCase() + " to make their move...");
    }
    public static void displayServerMessage(String serverMessage){
        System.out.println("SERVER: " + serverMessage);
    }
    public static void displayGameIsStartingMessage(){
        System.out.println("Take a seat, the game is starting...");
    }
    public static void displayMyHandRanking(String handRankName) {
        System.out.println("You have " + getHandDescription(handRankName) + "!");
    }
    public static void displayOtherHandRanking(String playerName, String handRankName) {
        System.out.println(playerName.toUpperCase() + " has" + getHandDescription(handRankName) + "!");
    }

    private static String getHandDescription(String handRankName) {
        if (handRankName.equals("four of a kind") || handRankName.equals("two pairs")) {
            return handRankName;
        }
        return "a " + handRankName;
    }

    public static void displayCurrentRoundWinners(ArrayList<Player> players, String winningHandRankName) {
        String message = "";
        for (int i = 0; i < players.size() - 1; i++) {
            String playerName = players.get(i).getName().toUpperCase();
            message += playerName + ", ";
        }
        String lastPlayerName = players.get(players.size() - 1).getName().toUpperCase();
        message += lastPlayerName + " wins the round with " + getHandDescription(winningHandRankName) + "! ";
        System.out.println(message);
    }
    public static void displayPlayerLostMessage(Player player) {
        System.out.println(player.getName().toUpperCase() + " lost. They will be leaving the table.");
    }

    public static void displayLoseGameScreen() {
        System.out.println("You've lost all your chips. You are asked to leave the table.");
        System.out.println("Exiting the game...");
    }
    public static void displayNewRoundMessage(int i) {
        System.out.println("=== ROUND "+ i + " === ");
    }
    public static void displayPlayerHUD(ArrayList<Card> tableCards, ArrayList<Card> playerHand) {
        System.out.println("===================");
        System.out.println("TABLE CARDS: " + tableCards.toString());
        System.out.println("YOUR HAND: " + playerHand.toString());
    }
    public static void displayRoles(HashMap<PokerRole, Player> roles) {
        String dealerName = roles.get(PokerRole.DEALER).getName().toUpperCase();
        String smallBlindName = roles.get(PokerRole.SMALL_BLIND).getName().toUpperCase();
        String bigBlindName = roles.get(PokerRole.BIG_BLIND).getName().toUpperCase();

        System.out.println(dealerName + " is the dealer.");
        System.out.println(smallBlindName + " is the small blind.");
        System.out.println(bigBlindName+ " is the big blind.");
    }
    public static void displayPlayerAction(Player actingPlayer, PlayerAction playerAction, int betAmount) {
        String playerName = actingPlayer.getName().toUpperCase();
        // Makes action name to a present-tense verb. E.g. bet -> bets, fold -> folds, raise -> raises
        String action = playerAction.name().toLowerCase() + "s";
        String message = playerName + " " + action;

        if (playerAction.isABet()) {
            // Converts message from "JOHN bets" to "JOHN bets 100 chips"
            message += " " + betAmount + " chips";
        }

        System.out.println(message);
    }
    public static void displayConnectionUpdate(Player player, ConnectionStatus status) {
        String action = "";
        switch (status) {
            case JOINED -> action = "joined";
            case DISCONNECTED -> action = "disconnected from";
            case RECONNECTED -> action = "reconnected to";
        }
        System.out.println(player + " has " + action + " the table!");

    }
    public static void askForABetAmount(PlayerAction action, int minimumBet) {
        String actionName = action.name().toLowerCase();
        System.out.println("How much do you want to " + actionName + "?: ");
    }
    public static void displayBetTooLowMessage(PlayerAction action, int minimumAmount) {
        String actionName = action.name().toLowerCase();
        System.out.println("Bet too low. Minimum " + actionName + ": " + minimumAmount + ".");
    }
    public static void displayInvalidValueMessage() {
        System.out.println("The value you entered is invalid.");
    }
    public static void displayReceivedTurnMessage() {
        System.out.println("It is your turn!");
    }
    public static void askForAnAction(ArrayList<PlayerAction> validActions) {
        String availableActions = "(";
        for (int i = 0; i < validActions.size() - 1; i++) {
            String action = validActions.get(i).name();
            availableActions += action + ", ";
        }
        String lastAction = validActions.get(validActions.size() - 1).name();
        availableActions += lastAction + ")";

        System.out.println("Please enter an available action " + availableActions + ": ");
    }
    public static void displayInvalidActionMessage() {
        System.out.println("Invalid action.");
    }
}
