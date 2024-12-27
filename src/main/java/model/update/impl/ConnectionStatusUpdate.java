package model.update.impl;

import enumeration.ConnectionStatus;
import enumeration.UpdateType;
import model.update.GameUpdate;

public class ConnectionStatusUpdate extends GameUpdate {
    private final ConnectionStatus connectionStatus;

    public ConnectionStatusUpdate(ConnectionStatus connectionStatus) {
        super(UpdateType.CONNECTION_STATUS);
        this.connectionStatus = connectionStatus;
    }

    public ConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }
}
