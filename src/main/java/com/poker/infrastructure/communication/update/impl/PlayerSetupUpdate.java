package com.poker.infrastructure.communication.update.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.poker.enumeration.GameUpdateType;
import com.poker.domain.player.Player;
import com.poker.infrastructure.communication.update.GameUpdate;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
public class PlayerSetupUpdate extends GameUpdate {

    private static final GameUpdateType UPDATE_TYPE = GameUpdateType.PLAYER_SETUP_UPDATE;

    private final String tableSessionId;
    private final String playerName;
    private final String playerId;
    @JsonProperty("isHost")
    private final boolean isHost;
    private final List<Player> existingPlayers;

    public PlayerSetupUpdate(String tableSessionId, String playerName, String playerId, boolean isHost, List<Player> existingPlayers) {
        super();
        this.tableSessionId = tableSessionId;
        this.playerName = playerName;
        this.playerId = playerId;
        this.isHost = isHost;
        this.existingPlayers = existingPlayers;
    }
    @Override
    public GameUpdateType getUpdateType() {
        return UPDATE_TYPE;
    }

}
