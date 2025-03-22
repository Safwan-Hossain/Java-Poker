package com.poker.infrastructure.communication.update.impl;

import com.poker.enumeration.GameUpdateType;
import com.poker.infrastructure.communication.update.GameUpdate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@RequiredArgsConstructor
public class ServerMessageUpdate extends GameUpdate {

    private static final GameUpdateType UPDATE_TYPE = GameUpdateType.SERVER_MESSAGE_UPDATE;

    private final String serverMessage;

    @Override
    public GameUpdateType getUpdateType() {
        return UPDATE_TYPE;
    }

}
