package com.poker.statemachine.guards.impl;

import com.poker.domain.game.ServerGame;
import com.poker.enumeration.GameEvent;
import com.poker.enumeration.GameState;
import com.poker.services.GameTableRegistry;
import com.poker.statemachine.guards.BaseGuard;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

import static com.poker.enumeration.RoundState.SHOWDOWN;

@Component
public class IsRoundNotFinishedGuard extends BaseGuard {

    public IsRoundNotFinishedGuard(GameTableRegistry gameTableRegistry) {
        super(gameTableRegistry);
    }

    @Override
    public boolean evaluate(StateContext<GameState, GameEvent> context) {
        ServerGame game = getServerGame(context);
        return game.getRoundState() != SHOWDOWN && !game.everyoneIsAllIn();
    }
}
