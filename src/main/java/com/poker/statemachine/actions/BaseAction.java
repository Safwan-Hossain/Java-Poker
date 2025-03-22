package com.poker.statemachine.actions;

import com.poker.application.orchestrator.GameLogicHandler;
import com.poker.constants.Constants;
import com.poker.enumeration.GameEvent;
import com.poker.enumeration.GameState;
import com.poker.infrastructure.communication.update.GameUpdate;
import com.poker.infrastructure.communication.update.impl.PlayerActionUpdate;
import com.poker.services.TimeoutService;
import com.poker.statemachine.events.GameStateEventPublisher;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

public abstract class BaseAction implements Action<GameState, GameEvent> {
    protected final GameLogicHandler gameLogicHandler;
    protected final TimeoutService timeoutService;
    private final GameStateEventPublisher eventPublisher;

    protected BaseAction(GameLogicHandler gameLogicHandler, TimeoutService timeoutService, GameStateEventPublisher eventPublisher) {
        this.gameLogicHandler = gameLogicHandler;
        this.timeoutService = timeoutService;
        this.eventPublisher = eventPublisher;
    }

    protected void resetPlayerIdleTimer(StateContext<GameState, GameEvent> context) {
        timeoutService.resetInactivityTimeout(getTableId(context));
    }

    protected String getTableId(StateContext<GameState, GameEvent> context) {
        return context.getStateMachine().getId();
    }

    protected PlayerActionUpdate getPlayerActionGameUpdate(StateContext<GameState, GameEvent> context) {
        return context.getExtendedState().get(Constants.PLAYER_ACTION_UPDATE_HEADER_NAME, PlayerActionUpdate.class);
    }

    protected void publishEvent(StateContext<GameState, GameEvent> context, GameEvent gameEvent, GameUpdate gameUpdate) {
        eventPublisher.publish( context.getStateMachine(), gameEvent, gameUpdate);
    }
    protected void publishEvent(StateContext<GameState, GameEvent> context, GameEvent gameEvent) {
        eventPublisher.publish( context.getStateMachine(), gameEvent);
    }

    /**
     * Abstract method that subclasses must implement.
     */
    @Override
    public abstract void execute(StateContext<GameState, GameEvent> context);
}
