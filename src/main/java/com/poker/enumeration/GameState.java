package com.poker.enumeration;

public enum GameState {
    WAITING_FOR_HOST,
    ROUND_START,
    BETTING,
    BETTING_ASSIGN_FIRST_PLAYER_TURN, // Child state of Betting
    BETTING_ASSIGN_NEXT_PLAYER_TURN, // Child state of Betting
    BETTING_APPLY_PLAYER_MOVE, // Child state of Betting
    BETTING_ADVANCE_ROUND, // Child state of Betting
    ROUND_END,
    GAME_OVER;
}