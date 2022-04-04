public enum PlayerAction {
    FOLD,
    BET,
    RAISE,
    CALL,
    CHECK,
    WAIT; // If client has no chips then they must wait

    public boolean isABet() {
        return this == BET || this == RAISE;
    }

    public static boolean actionIsValid(String action) {
        for (PlayerAction playerAction: PlayerAction.values()) {
            if (playerAction.toString().equalsIgnoreCase(action)) {
                return true;
            }
        }
        return false;
    }

    public static PlayerAction getActionByString(String action) {
        for (PlayerAction playerAction: PlayerAction.values()) {
            if (playerAction.toString().equalsIgnoreCase(action)) {
                return playerAction;
            }
        }
        throw new IllegalArgumentException("Unexpected value: " + action);
    }
}