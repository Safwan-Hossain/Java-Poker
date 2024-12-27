package enumeration;

public enum UpdateType {
    LAST_UNFOLDED_PLAYER_WINS, // If only one player is left who didn't fold, then he wins the round
    NEW_ROUND_STATE,
    PLAYER_QUIT,
    PLAYER_TURN,
    PLAYER_ACTION,
    CONNECTION_STATUS,
    SERVER_MESSAGE,
    // ====
    GAME_STATE,
    GAME_ENDED,
    GAME_STARTED,
    ROUND_STATE
}
