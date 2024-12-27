package model.update.impl;

import enumeration.ConnectionStatus;
import enumeration.UpdateType;
import model.update.GameUpdate;

public class PlayerSetupUpdate extends GameUpdate {
    private final String playerName;
    private final String playerId;

    private final String clientId;

    private final boolean isHost;

    public PlayerSetupUpdate(ConnectionStatus connectionStatus) {
        super(UpdateType.CONNECTION_STATUS); // TODO ====================================// TODO ====================================

    }

}
