package com.poker.enumeration;

public enum RoundState {
    WAITING_FOR_PLAYERS,
    PRE_FLOP,
    FLOP,
    TURN,
    RIVER,
    SHOWDOWN,
    ROUND_COMPLETE,
    GAME_OVER;

    public RoundState getNextRoundState() {
        return switch (this) {
            case WAITING_FOR_PLAYERS, ROUND_COMPLETE -> PRE_FLOP;
            case PRE_FLOP -> FLOP;
            case FLOP -> TURN;
            case TURN -> RIVER;
            case RIVER -> SHOWDOWN;
            case SHOWDOWN -> ROUND_COMPLETE;
            default -> this; // No transition for game over state
        };
    }
}
