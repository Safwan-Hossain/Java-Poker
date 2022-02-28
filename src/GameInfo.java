public class GameInfo {
    private Game game;
    private String message;
    private Player playerWithTurn;

    public GameInfo(Game game,  String message, Player playerWithTurn) {
        this.game = game;
        this.message = message;
        this.playerWithTurn = playerWithTurn;
    }

    public Game getGame() {
        return game;
    }

    public String getMessage() {
        return message;
    }

    public Player getPlayerWithTurn() {
        return playerWithTurn;
    }
}
