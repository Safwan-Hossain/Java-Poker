package com.poker.statemachine.guards;

import com.poker.domain.game.ServerGame;
import com.poker.enumeration.GameEvent;
import com.poker.enumeration.GameState;
import com.poker.server.GameTableSession;
import com.poker.services.GameTableRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;

@RequiredArgsConstructor
public abstract class BaseGuard implements Guard<GameState, GameEvent> {

    protected final GameTableRegistry gameTableRegistry;

    protected GameTableSession getGameSession(StateContext<GameState, GameEvent> context) {
        String tableId = context.getStateMachine().getId();
        if (!gameTableRegistry.doesSessionExist(tableId)) {
            throw new IllegalStateException("Could not find table session for ID: " + tableId);
        }
        return gameTableRegistry.getTableSession(tableId);
    }

    protected ServerGame getServerGame(StateContext<GameState, GameEvent> context) {
        return getGameSession(context).getServerGame();
    }
}