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
public class IsRoundFinishedGuard extends BaseGuard {

    public IsRoundFinishedGuard(GameTableRegistry gameTableRegistry) {
        super(gameTableRegistry);
    }

    @Override
    public boolean evaluate(StateContext<GameState, GameEvent> context) {
        ServerGame game = getServerGame(context);
        return game.getRoundState() == SHOWDOWN;
    }
}
