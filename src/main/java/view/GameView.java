package view;

import enumeration.ConnectionStatus;
import enumeration.PlayerAction;
import enumeration.PokerRole;
import enumeration.RoundState;
import model.player.Card;
import model.player.Client;
import model.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class GameView {
    public static void displayClientInformation(Client client){
        System.out.println("main.model.player.Client Name: " + client.getClientName() + " | ID: " + client.getClientID());
    }
    public static void askHostToStart(String startGameCommand){
        System.out.println("Type in \"" + startGameCommand +"\" to start the main.model.game.");
    }
    public static void displayWaitingForHostMessage(){
        System.out.println("Waiting for host to start the main.model.game...");
    }
    public static void displayWaitingForPlayerMessage(Player player){
        System.out.println("Waiting for " + player.getName().toUpperCase() + " to make their move...");
    }
    public static void displayServerMessage(String serverMessage){
        System.out.println("SERVER: " + serverMessage);
    }
    public static void displayGameIsStartingMessage(){
        System.out.println("Take a seat, the main.model.game is starting...");
    }
    public static void displayMyHandRanking(String handRankName) {
        System.out.println("You have " + getHandDescription(handRankName) + "!");
    }
    public static void displayPlayerHandRanking(Player player, String handRankName) {
        System.out.println(player.getName().toUpperCase() + " has " + getHandDescription(handRankName) + "!");
    }

    private static String getHandDescription(String handRankName) {
        if (handRankName.equals("four of a kind") || handRankName.equals("two pairs")) {
            return handRankName;
        }
        return "a " + handRankName;
    }

    public static void displayLastUnfoldedPlayer(Player lastPlayer) {
        System.out.println("All other players folded. \n" + lastPlayer.getName().toUpperCase() + " wins the round");
    }

    public static void displayCurrentRoundWinners(ArrayList<Player> players, String winningHandRankName) {
        String message = "";
        for (int i = 0; i < players.size() - 1; i++) {
            String playerName = players.get(i).getName().toUpperCase();
            message += playerName + ", ";
        }
        String lastPlayerName = players.get(players.size() - 1).getName().toUpperCase();
        message += "and " + lastPlayerName + " wins the round with " + getHandDescription(winningHandRankName) + "! ";
        System.out.println(message);
    }

    public static void displayGameOverScreen(Player winner) {
        String winnerName = winner.getName().toUpperCase();
        int winnerChips = winner.getChips();
        String winnerMessage = " ".repeat(5) + winnerName + " wins with " + winnerChips + " total chips!" + " ".repeat(5);
        String gameOverDash = "- ".repeat(winnerMessage.length() / 4);
        gameOverDash = gameOverDash.substring(0, gameOverDash.length() - 1);
        String gameOverText = "GAME OVER";
        String gameOverMessage = gameOverDash + " " + gameOverText + " " + gameOverDash;
        String winnerMessageSpacing = " ".repeat((gameOverMessage.length() - winnerMessage.length()) / 2);
        winnerMessage = winnerMessageSpacing + winnerMessage + winnerMessageSpacing;
        String horizontalDivider = "=".repeat(gameOverMessage.length());

        String finalMessage = "\n" + horizontalDivider + "\n" + gameOverMessage + "\n" + horizontalDivider  + "\n\n" +
                                 winnerMessage + "\n\n" + horizontalDivider + "\n\n";
        System.out.println(finalMessage);
    }

    public static void displayPlayerLostMessage(Player player) {
        System.out.println(player.getName().toUpperCase() + " lost. They will be leaving the table.");
    }

    public static void displayLoseGameScreen() {
        String loseGameMessage = "You've lost all your chips.";
        String horizontalDivider = "=".repeat(loseGameMessage.length());
        System.out.println("\n" + horizontalDivider + "\n" + loseGameMessage);
    }

    public static void displayExitMessage() {
        System.out.println("Exiting the main.model.game... \n");
    }

    public static void displayNewRoundState(RoundState roundState) {
        String roundStateMessage = " - - " + roundState.name().toUpperCase() + " - -  ";
        String horizontalDivider = "=".repeat(roundStateMessage.length());
        System.out.println(horizontalDivider + "\n" + roundStateMessage + "\n" + horizontalDivider);
    }
    public static void displayNewRoundMessage(int i) {
        System.out.println("=== ROUND "+ i + " === ");
    }
    public static void displayPlayerHUD(List<Card> tableCards, List<Card> playerHand, int totalPot, int playerChips) {
        String totalPotDisplay = "TOTAL POT: " + totalPot;
        String playerChipsDisplay = "YOUR CHIPS: " + playerChips;
        int totalPotSpacing = totalPotDisplay.length();
        int playerChipsSpacing = playerChipsDisplay.length();
        int spacing = Math.max(totalPotSpacing, playerChipsSpacing) + 1;

        String tableInfo = totalPotDisplay + " ".repeat(spacing - totalPotSpacing) + "|  " + "TABLE CARDS: " + tableCards.toString();
        String playerInfo = playerChipsDisplay + " ".repeat(spacing - playerChipsSpacing) + "|  " +  "YOUR HAND: " + playerHand.toString();
        String horizontalDivider = "=".repeat(Math.max(playerInfo.length(), tableInfo.length()));
        String message =  horizontalDivider + "\n" + tableInfo + "\n" + playerInfo + "\n" + horizontalDivider;
        System.out.println(message);
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

        if (playerAction.equals(PlayerAction.BET)) {

            message += " " + betAmount + " chips";
        }
        else if (playerAction.equals(PlayerAction.RAISE)) {
            message += " to " + betAmount + " chips";
        }
        System.out.println(message);
    }
    public static void displayConnectionUpdate(String playerName, ConnectionStatus status) {
        String action = "";
        switch (status) {
            case CONNECTED -> action = "joined";
            case DISCONNECTED -> action = "disconnected from";
            case RECONNECTED -> action = "reconnected to";
        }
        System.out.println(playerName + " has " + action + " the table!");

    }
    public static void askForABetAmount(PlayerAction action, int minimumBet, String cancelValue) {
        String actionName = action.name().toLowerCase();
        System.out.println("Type \"" + cancelValue + "\" to cancel bet.");
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
    public static void askForAnAction(HashSet<PlayerAction> validActions) {
        int i = 0;
        String availableActions = "(";
        for (PlayerAction playerAction: validActions) {
            if (i < validActions.size() - 1) {
                String action = playerAction.name();
                availableActions += action + ", ";
            }
            else {
                String lastAction = playerAction.name();
                availableActions += lastAction + ")";
            }
            i++;
        }
        System.out.println("Please enter an available action " + availableActions + ": ");
    }
    public static void displayInvalidActionMessage() {
        System.out.println("Invalid action.");
    }
}
