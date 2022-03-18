import java.io.Serializable;

public class GameInfo implements Serializable {

    private Game game;
    private String playerWithTurn; // Name of player who has the current turn
    private String message; // Any messages from the server

    private String clientID; // ID of client (player) who performed action
    private String playerName; // Name of player who performed an action
    private PlayerAction playerAction; // The type of action performed by a player
    private int amount; // amount of chips a player puts in for a bet or raise (0 if fold/call/check)


    public GameInfo(Game game, String message, String playerWithTurn, PlayerAction playerAction, String playerName, int amount) {
        this.game = game;
        this.message = message;
        this.playerWithTurn = playerWithTurn;
        this.playerName = playerName;
        this.playerAction = playerAction;
        this.amount = amount;
    }

    public GameInfo(String ClientID, String playerName, PlayerAction playerAction, int amount) {
        this.game = null;
        this.message = null;
        this.playerWithTurn = null;
        this.clientID = ClientID;
        this.playerName = playerName;
        this.playerAction = playerAction;
        this.amount = amount;
    }

    public Game getGame() {
        return game;
    }

    public String getMessage() {
        return message;
    }

    public String getPlayerWithTurn() {
        return playerWithTurn;
    }

    public PlayerAction getPlayerAction() {
        return playerAction;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        if (amount == 0) {
            return playerName.toUpperCase() + " performs the action " + playerAction.name();
        }
        return playerName.toUpperCase() + " performs the action " + playerAction.name() + " for an amount of " + amount;
    }
}
