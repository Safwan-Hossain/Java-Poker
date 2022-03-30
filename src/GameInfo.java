import java.io.Serializable;

public class GameInfo implements Serializable {

    // REQUIRED EVERYTIME MESSAGE IS SENT
    private final String clientID; // ID of client (player) who performed action
    private final String playerName; // Name of player who performed an action

    // USED TO DEFINE WHAT TYPE OF INFORMATION IS BEING SENT
    private UpdateType updateType;

    // USED TO SEND PLAYER ACTIONS
    private PlayerAction playerAction; // The type of action performed by a player
    private int amount; // amount of chips a player puts in for a bet or raise (0 if fold/call/check)

    // USED TO SEND DIFFERENT UPDATES e.g, player has disconnected
    private boolean gameHasStarted;
    private ConnectionStatus connectionStatus;

    // USED TO BROADCAST SERVER MESSAGE
    private String serverMessage;

    public GameInfo(String clientID, String playerName, PlayerAction playerAction, int amount) {
        this.clientID = clientID;
        this.playerName = playerName;
    }

    public GameInfo(String clientID, String playerName) {
        this.clientID = clientID;
        this.playerName = playerName;
    }

    // DUMMY CODE -- NOT GOING TO BE ON FINAL CODE
    public GameInfo() {
        this.clientID = "";
        this.playerName = "";
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        if (amount == 0) {
            return playerName.toUpperCase() + " performs the action " + playerAction.name();
        }
        return playerName.toUpperCase() + " performs the action " + playerAction.name() + " for an amount of " + amount;
    }
}
