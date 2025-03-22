package com.poker.infrastructure.communication.update.impl;

import com.poker.enumeration.GameUpdateType;
import com.poker.enumeration.RoundState;
import com.poker.domain.player.Card;
import com.poker.domain.player.Player;
import com.poker.infrastructure.communication.update.GameUpdate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;

@Getter
@SuperBuilder
@RequiredArgsConstructor
public class GameStateSnapshotUpdate extends GameUpdate {

    private static final GameUpdateType UPDATE_TYPE = GameUpdateType.GAME_STATE_SNAPSHOT_UPDATE;

    private final String targetPlayerIdToUpdate;
    private final String playerIdWithTurn;
    private final RoundState roundState;
    private final Map<String, Integer> playerBettings;
    private final List<Player> sanitizedPlayers;
    private final List<Card> tableCards;
    private final List<Card> playerHand;

    @Override
    public GameUpdateType getUpdateType() {
        return UPDATE_TYPE;
    }

}
