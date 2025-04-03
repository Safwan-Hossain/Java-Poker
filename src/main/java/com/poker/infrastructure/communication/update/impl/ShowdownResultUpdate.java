package com.poker.infrastructure.communication.update.impl;

import com.poker.enumeration.GameUpdateType;
import com.poker.enumeration.RoundState;
import com.poker.domain.player.Card;
import com.poker.domain.player.HandEvaluation;
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
public class ShowdownResultUpdate extends GameUpdate {

    private static final GameUpdateType UPDATE_TYPE = GameUpdateType.SHOWDOWN_RESULT_UPDATE;

    private final RoundState roundState = RoundState.SHOWDOWN;
    private final List<Player> players;
    private final List<Player> winnersForThisRound;
    private final List<Player> bankruptPlayers;
    private final Map<String, HandEvaluation> playerHandEvaluations;
    private final List<Card> tableCards;
    private final int totalPot;
    private final int sharePerWinner;

    @Override
    public GameUpdateType getUpdateType() {
        return UPDATE_TYPE;
    }

}
