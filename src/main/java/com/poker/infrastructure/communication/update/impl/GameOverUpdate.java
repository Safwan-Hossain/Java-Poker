package com.poker.infrastructure.communication.update.impl;

import com.poker.domain.player.Player;
import com.poker.enumeration.GameUpdateType;
import com.poker.infrastructure.communication.update.GameUpdate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@RequiredArgsConstructor
public class GameOverUpdate extends GameUpdate {

    private static final GameUpdateType UPDATE_TYPE = GameUpdateType.GAME_OVER_UPDATE;

    private final Player winner;

    @Override
    public GameUpdateType getUpdateType() {
        return UPDATE_TYPE;
    }

}
