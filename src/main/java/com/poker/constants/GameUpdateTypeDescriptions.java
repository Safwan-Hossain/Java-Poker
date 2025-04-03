package com.poker.constants;

// The constants are used for annotation compatibility since Java annotations require compile time constants.
// Direct method calls (e.g., GameUpdateType.getDesc()) are not allowed in annotations because they are evaluated at runtime
// By defining the values as static constants, we ensure they can be safely referenced in annotations
public class GameUpdateTypeDescriptions {
    public static final String UPDATE_TYPE_FIELD = "updateType";
    public final static String CONNECTION_STATUS_UPDATE_DESC = "CONNECTION_STATUS_UPDATE";
    public final static String GAME_STATE_SNAPSHOT_UPDATE_DESC = "GAME_STATE_SNAPSHOT_UPDATE";
    public final static String PLAYER_ACTION_UPDATE_DESC = "PLAYER_ACTION_UPDATE";
    public final static String PLAYER_HAND_UPDATE_DESC = "PLAYER_HAND_UPDATE";
    public final static String PLAYER_SETUP_UPDATE_DESC = "PLAYER_SETUP_UPDATE";
    public final static String PLAYER_TURN_UPDATE_DESC = "PLAYER_TURN_UPDATE";
    public final static String ROUND_STATE_UPDATE_DESC = "ROUND_STATE_UPDATE";
    public final static String SERVER_MESSAGE_UPDATE_DESC = "SERVER_MESSAGE_UPDATE";
    public final static String SHOWDOWN_RESULT_UPDATE_DESC = "SHOWDOWN_RESULT_UPDATE";
    public final static String GAME_OVER_UPDATE_DESC = "GAME_OVER_UPDATE";
}
