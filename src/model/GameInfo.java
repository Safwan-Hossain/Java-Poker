package model;

import enumeration.game.PlayerAction;
import enumeration.game.PokerRole;
import enumeration.game.RoundState;
import enumeration.servercommunication.ConnectionStatus;
import enumeration.servercommunication.UpdateType;
import game.Game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class GameInfo implements Serializable {

    // REQUIRED EVERYTIME MESSAGE IS SENT
    private final String clientID; // ID of client (player) who performed action
    private final String playerName; // Name of player who performed an action

    private int smallBlind;
    private int buyIn;

    private boolean isHost;

    private Player playerWithTurn; // model.Player with turn
    private RoundState roundState;
    private HashMap<PokerRole, Player> roles;
    // Note that copying an arraylist of players will not copy the arraylist of cards (their hand) inside each player
    // Therefore, currently it is stored in hashmap
    private HashMap<Player, ArrayList<Card>> playerHands;

    private String nameOfWinningHand;
    // List of all players in the current game
    private ArrayList<Player> players;

    // List of players who won during the showdown of the current round
    private ArrayList<Player> winningPlayers;
    // List of players who lost all their chips after the showdown of the current round
    private ArrayList<Player> losingPlayers;

    private ArrayList<Card> tableCards;

    // THE MAIN GAME
    private Game game;

    // USED TO DEFINE WHAT TYPE OF INFORMATION IS BEING SENT
    private UpdateType updateType;

    // USED TO SEND PLAYER ACTIONS
    private PlayerAction playerAction; // The type of action performed by a player
    private int betAmount; // amount of chips a player puts in for a bet or raise (0 if fold/call/check)

    // USED TO SEND DIFFERENT UPDATES e.g, player has disconnected
    private boolean gameHasStarted;
    private ConnectionStatus connectionStatus;

    // USED TO BROADCAST SERVER MESSAGE
    private String serverMessage;

    public GameInfo(String clientID, String playerName) {
        this.clientID = clientID;
        this.playerName = playerName;
    }

    public boolean isHost() {
        return isHost;
    }

    public void setIsHost(boolean isHost) {
        this.isHost = isHost;
    }

    public int getSmallBlind() {
        return smallBlind;
    }

    public void setSmallBlind(int smallBlind) {
        this.smallBlind = smallBlind;
    }

    public int getBuyIn() {
        return buyIn;
    }

    public void setBuyIn(int buyIn) {
        this.buyIn = buyIn;
    }

    public boolean isGameHasStarted() {
        return gameHasStarted;
    }

    public Player getPlayerWithTurn() {
        return playerWithTurn;
    }

    public void setPlayerWithTurn(Player playerWithTurn) {
        this.playerWithTurn = playerWithTurn;
    }

    public String getServerMessage() {
        return serverMessage;
    }

    public void setServerMessage(String serverMessage) {
        this.serverMessage = serverMessage;
    }

    public UpdateType getUpdateType() {
        return updateType;
    }

    public void setUpdateType(UpdateType updateType) {
        this.updateType = updateType;
    }

    public String getClientID() {
        return clientID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public boolean hasGameStarted() {
        return gameHasStarted;
    }

    public void setGameHasStarted(boolean gameHasStarted) {
        this.gameHasStarted = gameHasStarted;
    }

    public ConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(ConnectionStatus connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public PlayerAction getPlayerAction() {
        return playerAction;
    }

    public void setPlayerAction(PlayerAction playerAction) {
        this.playerAction = playerAction;
    }

    public int getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(int betAmount) {
        this.betAmount = betAmount;
    }

    public RoundState getRoundState() {
        return roundState;
    }

    public void setRoundState(RoundState roundState) {
        this.roundState = roundState;
    }

    public String getNameOfWinningHand() {
        return nameOfWinningHand;
    }

    public void setNameOfWinningHand(String nameOfWinningHand) {
        this.nameOfWinningHand = nameOfWinningHand;
    }

    public HashMap<PokerRole, Player> getRoles() {
        return roles;
    }

    public void setRoles(HashMap<PokerRole, Player> roles) {
        this.roles = new HashMap<>();
        for (PokerRole pokerRole: roles.keySet()) {
            this.roles.put(pokerRole, roles.get(pokerRole));
        }
    }

    public HashMap<Player, ArrayList<Card>> getPlayerHands() {
        return playerHands;
    }

    public void setPlayerHands(HashMap<Player, ArrayList<Card>> map) {
        this.playerHands = new HashMap<>();
        for (Player player: map.keySet()) {
            ArrayList<Card> cards = new ArrayList<>(player.getHand());
            this.playerHands.put(player, cards);
        }
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = new ArrayList<>();
        for (Player player: players) {
            Player newPlayer = new Player(player.getName(), player.getPlayerID());
            newPlayer.set_chips(player.getChips());
            this.players.add(newPlayer);
        }
    }

    public ArrayList<Player> getWinningPlayers() {
        return winningPlayers;
    }

    public void setWinningPlayers(ArrayList<Player> winningPlayers) {
        this.winningPlayers = new ArrayList<>();
        for (Player player: winningPlayers) {
            Player newPlayer = new Player(player.getName(), player.getPlayerID());
            newPlayer.set_chips(player.getChips());
            this.winningPlayers.add(newPlayer);
        }
    }


    public ArrayList<Player> getLosingPlayers() {
        return losingPlayers;
    }

    public void setLosingPlayers(ArrayList<Player> losingPlayers) {
        this.losingPlayers = new ArrayList<>();
        for (Player player: losingPlayers) {
            Player newPlayer = new Player(player.getName(), player.getPlayerID());
            newPlayer.set_chips(player.getChips());
            this.losingPlayers.add(newPlayer);
        }
    }

    public ArrayList<Card> getTableCards(){
        return tableCards;
    }

    public void setTableCards(ArrayList<Card> tableCards) {
        this.tableCards = new ArrayList<>();
        this.tableCards.addAll(tableCards);
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public String toString() {
        if (betAmount == 0) {
            return playerName.toUpperCase() + " performs the action " + playerAction.name();
        }
        return playerName.toUpperCase() + " performs the action " + playerAction.name() + " for an amount of " + betAmount;
    }
}
