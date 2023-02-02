package enumeration.servercommunication;

public enum UpdateType {
    CONNECTION_STATUS,
    GAME_ENDED,
    GAME_STARTED,
    LAST_UNFOLDED_PLAYER_WINS, // If only one player is left who didn't fold, then he wins the round
    NEW_ROUND_STATE,
    PLAYER_ACTION,
    PLAYER_QUIT,
    PLAYER_TURN,
    SERVER_MESSAGE;
}
