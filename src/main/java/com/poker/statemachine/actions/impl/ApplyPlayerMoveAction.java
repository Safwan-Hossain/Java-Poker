package com.poker.statemachine.actions.impl;

import com.poker.application.orchestrator.GameLogicHandler;
import com.poker.constants.Constants;
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
public class ApplyPlayerMoveAction extends BaseAction {

    protected ApplyPlayerMoveAction(GameLogicHandler gameLogicHandler, TimeoutService timeoutService, GameStateEventPublisher eventPublisher) {
        super(gameLogicHandler, timeoutService, eventPublisher);
    }

    @Override
    public void execute(StateContext<GameState, GameEvent> context) {
        String tableId = getTableId(context);
        timeoutService.cancelPlayerMoveTimeout(tableId);
        PlayerActionUpdate update = getPlayerActionGameUpdate(context);

        gameLogicHandler
                .applyPlayerMove(tableId, update.getPlayerId(), update.getAction(), update.getBetAmount())
                .doOnNext(event -> log.info("Next event after player action: {}", event))
                .doOnTerminate(() -> removeCachedPlayerAction(context))
                .subscribe(event -> publishEvent(context, event));

    }

    private void removeCachedPlayerAction(StateContext<GameState, GameEvent> context) {
        context.getExtendedState().getVariables().remove(Constants.PLAYER_ACTION_UPDATE_HEADER_NAME);
    }
}
