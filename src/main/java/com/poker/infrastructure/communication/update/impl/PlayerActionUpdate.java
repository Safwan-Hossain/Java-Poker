package com.poker.infrastructure.communication.update.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.poker.enumeration.GameUpdateType;
import com.poker.enumeration.PlayerAction;
import com.poker.infrastructure.communication.update.GameUpdate;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class PlayerActionUpdate extends GameUpdate {

    private static final GameUpdateType UPDATE_TYPE = GameUpdateType.PLAYER_ACTION_UPDATE;

    private final String playerId;
    private final PlayerAction action;
    private final int betAmount;

    // Allows Jackson to deserialize JSON into this object
    @JsonCreator
    public PlayerActionUpdate(
            @JsonProperty("playerId") String playerId,
            @JsonProperty("action") PlayerAction action,
            @JsonProperty("betAmount") int betAmount
    ) {
        this.playerId = playerId;
        this.action = action;
        this.betAmount = betAmount;
    }

    @Override
    public GameUpdateType getUpdateType() {
        return UPDATE_TYPE;
    }

}
