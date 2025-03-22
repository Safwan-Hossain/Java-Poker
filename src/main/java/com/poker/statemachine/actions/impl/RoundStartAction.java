package com.poker.statemachine.actions.impl;

import com.poker.application.orchestrator.GameLogicHandler;
import com.poker.enumeration.GameEvent;
import com.poker.enumeration.GameState;
import com.poker.services.TimeoutService;
import com.poker.statemachine.actions.BaseAction;
import com.poker.statemachine.events.GameStateEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RoundStartAction extends BaseAction {

    protected RoundStartAction(GameLogicHandler gameLogicHandler, TimeoutService timeoutService, GameStateEventPublisher eventPublisher) {
        super(gameLogicHandler, timeoutService, eventPublisher);
    }

    @Override
    public void execute(StateContext<GameState, GameEvent> context) {
        String tableId = getTableId(context);
        resetPlayerIdleTimer(context);
        gameLogicHandler.startNextRound(tableId)
                .subscribe(event -> publishEvent(context, event));
    }

}
