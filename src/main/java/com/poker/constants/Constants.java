package com.poker.constants;

public class Constants {
    // Headers
    public static final String GAME_UPDATE_HEADER_NAME = "Game_Update_Header";
    public static final String PLAYER_ACTION_UPDATE_HEADER_NAME = "Player_Update_Header";

    // Player related constants
    public static final String PLAYER_NAME_PARAM = "playerName";
    public static final String DEFAULT_PLAYER_NAME_PARAM = "Guest";
    public static final int MAX_MISSED_CONSECUTIVE_TURNS = 2;
    public static final int MINIMUM_PLAYERS_TO_CONTINUE_GAME = 2;

    // Game related constants
    public static final String NEW_GAME_PARAM = "newGame";
    public static final int INVALID_BET_AMOUNT = -1;

    // Server event configs
    public static final int UPDATE_BUFFER_SIZE = 35; // Max queued updates per player
    public static final long DELAY_BETWEEN_SERVER_EVENTS = 1; // Delay in seconds

    // Others
    public static final String FALSE_STRING_VALUE = "false";
    public static final String TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String TIME_ZONE = "UTC";

}
