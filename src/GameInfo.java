import java.io.Serializable;

public class GameInfo implements Serializable {

    private final String clientID; // ID of client (player) who performed action
    private final String playerName; // Name of player who performed an action
    private final PlayerAction playerAction; // The type of action performed by a player
    private final int amount; // amount of chips a player puts in for a bet or raise (0 if fold/call/check)

    public GameInfo(String ClientID, String playerName, PlayerAction playerAction, int amount) {
        this.clientID = ClientID;
        this.playerName = playerName;
        this.playerAction = playerAction;
        this.amount = amount;
    }


    // DUMMY CODE -- NOT GOING TO BE ON FINAL CODE
    public GameInfo() {
        this.clientID = "";
        this.playerName = "";
        this.playerAction = null;
        this.amount = 0;
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
