package model.update.impl;

import enumeration.UpdateType;
import model.update.GameUpdate;

public class ServerMessageUpdate extends GameUpdate {
    private final String serverMessage;

    public ServerMessageUpdate(String serverMessage) {
        super(UpdateType.SERVER_MESSAGE);
        this.serverMessage = serverMessage;
    }

    public String getServerMessage() {
        return serverMessage;
    }
}
