package com.poker.infrastructure.communication.update.impl;

import com.poker.enumeration.GameUpdateType;
import com.poker.enumeration.RoundState;
import com.poker.domain.player.Card;
import com.poker.infrastructure.communication.update.GameUpdate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
@RequiredArgsConstructor
public class RoundStateUpdate extends GameUpdate {

    private static final GameUpdateType UPDATE_TYPE = GameUpdateType.ROUND_STATE_UPDATE;

    private final RoundState roundState;
    private final List<Card> tableCards;

    @Override
    public GameUpdateType getUpdateType() {
        return UPDATE_TYPE;
    }

}
