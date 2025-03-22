package com.poker.infrastructure.communication.update.impl;

import com.poker.enumeration.ConnectionStatus;
import com.poker.enumeration.GameUpdateType;
import com.poker.infrastructure.communication.update.GameUpdate;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@ToString
public class ConnectionStatusUpdate extends GameUpdate {

    private static final GameUpdateType UPDATE_TYPE = GameUpdateType.CONNECTION_STATUS_UPDATE;

    private final ConnectionStatus connectionStatus;
    private final String playerId;
    private final String playerName;

    public ConnectionStatusUpdate(ConnectionStatus connectionStatus, String playerId, String playerName) {
        super();
        this.connectionStatus = connectionStatus;
        this.playerId = playerId;
        this.playerName = playerName;
    }

    @Override
    public GameUpdateType getUpdateType() {
        return UPDATE_TYPE;
    }

}
