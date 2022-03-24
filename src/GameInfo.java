import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class GameInfo implements Serializable {

    // REQUIRED EVERYTIME MESSAGE IS SENT
    private final String clientID; // ID of client (player) who performed action
    private final String playerName; // Name of player who performed an action

    private Player playerWithTurn; // Player with turn
    private RoundState roundState;

    public HashMap<Player, ArrayList<Card>> getPlayerHands() {
        return playerHands;
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

    private HashMap<PokerRole, Player> roles;

    public void setPlayerHands(HashMap<Player, ArrayList<Card>> map) {
        this.playerHands = new HashMap<>();
        for (Player player: map.keySet()) {
            ArrayList<Card> cards = new ArrayList<>(player.get_hand());
            this.playerHands.put(player, cards);
        }
    }

    HashMap<Player, ArrayList<Card>> playerHands;

    public ArrayList<Card> getTableCards(){
        return tableCards;
    }

    public void setTableCards(ArrayList<Card> tableCards) {
        this.tableCards = new ArrayList<>();
        this.tableCards.addAll(tableCards);
    }

    private ArrayList<Card> tableCards;

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

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

    @Override
    public String toString() {
        if (betAmount == 0) {
            return playerName.toUpperCase() + " performs the action " + playerAction.name();
        }
        return playerName.toUpperCase() + " performs the action " + playerAction.name() + " for an amount of " + betAmount;
    }
}
