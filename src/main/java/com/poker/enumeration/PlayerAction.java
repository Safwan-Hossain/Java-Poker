package com.poker.enumeration;

public enum PlayerAction {
    HOST_SAYS_START,
    QUIT,
    FOLD,
    BET,
    RAISE,
    CALL,
    CHECK,
    WAIT; // If client has no chips (all in) then they must wait

    public boolean isABetAction() {
        return this == BET || this == RAISE;
    }
}