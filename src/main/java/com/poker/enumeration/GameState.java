package com.poker.enumeration;

public enum GameState {
    WAITING_FOR_HOST,
    ROUND_START,
    BETTING,
    ROUND_END,
    GAME_OVER,


    // Substates of BETTING
    ASSIGNING_FIRST_TURN,
    ASSIGNING_NEXT_TURN,
    APPLYING_PLAYER_ACTION,
    ADVANCING_ROUND_STATE,
    EVALUATE_PLAYER_ACTION_RESULT,
    PROCESSING_PLAYER_TIMEOUT
}