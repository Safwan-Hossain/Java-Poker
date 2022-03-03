
public class GameInfo {
    public enum playerActions{FOLD, BET, RAISE, CALL};

    private Game game;
    private playerActions playerAction; // The type of action performed by a player
    private String message; // Any messages from the server
    private String playerWithTurn; // Name of player who has the current turn
    private String playerName; // Name of player who performed an action
    private int amount; // amount of chips a player puts in for a bet or raise (0 if fold/call/check)


    public GameInfo(Game game, String message, String playerWithTurn, playerActions playerAction, String playerName, int amount) {
        this.game = game;
        this.message = message;
        this.playerWithTurn = playerWithTurn;
        this.playerAction = playerAction;
        this.playerName = playerName;
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

    public playerActions getPlayerAction() {
        return playerAction;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getAmount() {
        return amount;
    }
}
