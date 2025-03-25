package com.poker.statemachine.guards.impl;

import com.poker.domain.game.ServerGame;
import com.poker.enumeration.GameEvent;
import com.poker.enumeration.GameState;
import com.poker.services.GameTableRegistry;
import com.poker.statemachine.guards.BaseGuard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class IsBettingNotFinishedGuard extends BaseGuard {
    public IsBettingNotFinishedGuard(GameTableRegistry gameTableRegistry) {
        super(gameTableRegistry);
    }

    @Override
    public boolean evaluate(StateContext<GameState, GameEvent> context) {
        ServerGame game = getServerGame(context);
         return !(game.isBettingFinished());
    }
}
