package com.poker.statemachine.actions.impl;

import com.poker.application.orchestrator.GameLogicHandler;
import com.poker.enumeration.GameEvent;
import com.poker.enumeration.GameState;
import com.poker.infrastructure.communication.update.impl.PlayerActionUpdate;
import com.poker.services.TimeoutService;
import com.poker.statemachine.actions.BaseAction;
import com.poker.statemachine.events.GameStateEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AssignNextPlayerTurnAction extends BaseAction {

    protected AssignNextPlayerTurnAction(GameLogicHandler gameLogicHandler, TimeoutService timeoutService, GameStateEventPublisher eventPublisher) {
        super(gameLogicHandler, timeoutService, eventPublisher);
    }

    @Override
    public void execute(StateContext<GameState, GameEvent> context) {
        String tableId = getTableId(context);
        gameLogicHandler.assignNextPlayerTurn(tableId);
        timeoutService.startPlayerMoveTimeout(tableId,() -> onPlayerTimeout(context));
    }
    private void onPlayerTimeout(StateContext<GameState, GameEvent> context) {
        String tableId = getTableId(context);
        PlayerActionUpdate playerActionUpdate = gameLogicHandler.onPlayerTurnTimeout(tableId);
        publishEvent(context,GameEvent.PLAYER_ACTION_RECEIVED,  playerActionUpdate);
    }
}