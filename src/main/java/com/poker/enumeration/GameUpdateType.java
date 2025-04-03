package com.poker.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.poker.constants.GameUpdateTypeDescriptions.*;

@Getter
@AllArgsConstructor
// The constants are used for annotation compatibility since Java annotations require compile time constants.
// Direct method calls (e.g., GameUpdateType.getDesc()) are not allowed in annotations because they are evaluated at runtime
// By defining the values as static constants, we ensure they can be safely referenced in annotations
public enum GameUpdateType {
    CONNECTION_STATUS_UPDATE(CONNECTION_STATUS_UPDATE_DESC),
    GAME_STATE_SNAPSHOT_UPDATE(GAME_STATE_SNAPSHOT_UPDATE_DESC),
    PLAYER_ACTION_UPDATE(PLAYER_ACTION_UPDATE_DESC),
    PLAYER_HAND_UPDATE(PLAYER_HAND_UPDATE_DESC),
    PLAYER_SETUP_UPDATE(PLAYER_SETUP_UPDATE_DESC),
    PLAYER_TURN_UPDATE(PLAYER_TURN_UPDATE_DESC),
    ROUND_STATE_UPDATE(ROUND_STATE_UPDATE_DESC),
    SERVER_MESSAGE_UPDATE(SERVER_MESSAGE_UPDATE_DESC),
    SHOWDOWN_RESULT_UPDATE(SHOWDOWN_RESULT_UPDATE_DESC),
    GAME_OVER_UPDATE(GAME_OVER_UPDATE_DESC);


    private final String desc;
}
