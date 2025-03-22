package com.poker.constants;

public class Constants {
    public static final String GAME_UPDATE_HEADER_NAME = "Game_Update_Header";
    public static final String PLAYER_ACTION_UPDATE_HEADER_NAME = "Player_Update_Header";
    public static final String NEW_GAME_PARAM = "newGame";
    public static final String FALSE_STRING_VALUE = "false";
    public static final String PLAYER_NAME_PARAM = "playerName";
    public static final String DEFAULT_PLAYER_NAME_PARAM = "Guest";
    public static final int UPDATE_BUFFER_SIZE = 35; // Number of updates that can be queued to a player
    public static final long DELAY_BETWEEN_SERVER_EVENTS = 1000; // in  ms
    public static final String BASE_WS_URL = "ws://localhost:8080/ws/game"; // Maybe  move to application.prop?
    public static final String START_COMMAND = "START";
    public static final int INVALID_BET_AMOUNT = -1;

    public static final String TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String TIME_ZONE = "UTC";

}
