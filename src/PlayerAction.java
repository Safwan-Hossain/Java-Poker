public enum PlayerAction {
    FOLD,
    BET,
    RAISE,
    CALL;

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

