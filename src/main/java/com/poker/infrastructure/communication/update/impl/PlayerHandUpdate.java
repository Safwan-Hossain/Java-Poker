package com.poker.infrastructure.communication.update.impl;

import com.poker.enumeration.GameUpdateType;
import com.poker.domain.player.Card;
import com.poker.infrastructure.communication.update.GameUpdate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
@RequiredArgsConstructor
public class PlayerHandUpdate extends GameUpdate {

    private static final GameUpdateType UPDATE_TYPE = GameUpdateType.PLAYER_HAND_UPDATE;

    private final String playerId;
    private final List<Card> playerHand;

    @Override
    public GameUpdateType getUpdateType() {
        return UPDATE_TYPE;
    }

}
