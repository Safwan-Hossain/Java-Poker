package com.poker.infrastructure.communication.update.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.poker.enumeration.GameUpdateType;
import com.poker.enumeration.PlayerAction;
import com.poker.infrastructure.communication.update.GameUpdate;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;

@Getter
@SuperBuilder
public class PlayerTurnUpdate extends GameUpdate {

    private static final GameUpdateType UPDATE_TYPE = GameUpdateType.PLAYER_TURN_UPDATE;

    private final String playerIdWithTurn;
    private final HashSet<PlayerAction> validPlayerActions;
    private final int minimumBetAmount;
    private final int maximumBetAmount;
    private final int minimumCallAmount;

    // Allows Jackson to deserialize JSON into this object
    @JsonCreator
    public PlayerTurnUpdate(
            @JsonProperty("playerIdWithTurn") String playerIdWithTurn,
            @JsonProperty("validPlayerActions") HashSet<PlayerAction> validPlayerActions,
            @JsonProperty("minimumBetAmount") int minimumBetAmount,
            @JsonProperty("maximumBetAmount") int maximumBetAmount,
            @JsonProperty("minimumCallAmount") int minimumCallAmount
    ) {
        this.playerIdWithTurn = playerIdWithTurn;
        this.validPlayerActions = validPlayerActions;
        this.minimumBetAmount = minimumBetAmount;
        this.maximumBetAmount = maximumBetAmount;
        this.minimumCallAmount = minimumCallAmount;
    }

    @Override
    public GameUpdateType getUpdateType() {
        return UPDATE_TYPE;
    }

}
