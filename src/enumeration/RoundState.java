package enumeration;

public enum RoundState {
    PRE_FLOP,
    FLOP,
    TURN,
    RIVER,
    SHOWDOWN;

    public static RoundState getNextRoundState(RoundState roundState) {
        switch (roundState) {
            case PRE_FLOP -> {
                return RoundState.FLOP;
            }
            case FLOP -> {
                return RoundState.TURN;
            }
            case TURN -> {
                return RoundState.RIVER;
            }
            case RIVER -> {
                return RoundState.SHOWDOWN;
            }
            case SHOWDOWN -> {
                return RoundState.PRE_FLOP;
            }
        }
        throw new RuntimeException("Round State argument not found");
    }
}
