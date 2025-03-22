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

import java.time.Duration;

@Slf4j
@Component
public class RoundEndAction extends BaseAction {

    private final static Duration DELAY_BEFORE_NEXT_ROUND = Duration.ofSeconds(1);
    public RoundEndAction(GameLogicHandler gameLogicHandler, TimeoutService timeoutService, GameStateEventPublisher eventPublisher) {
        super(gameLogicHandler, timeoutService, eventPublisher);
    }
    @Override
    public void execute(StateContext<GameState, GameEvent> context) {
        String tableId = getTableId(context);
        log.info("[Table {}] Round ended. Evaluating result...", tableId);

        triggerNextStateAfterRoundEnds(context, tableId);
    }

    private void triggerNextStateAfterRoundEnds(StateContext<GameState, GameEvent> context, String tableId) {
        gameLogicHandler.handleRoundEnd(tableId, DELAY_BEFORE_NEXT_ROUND)
                .doOnNext(event -> log.info("[Table {}] Sending FSM event: {}", tableId, event))
                .subscribe(event -> publishEvent(context, event));
    }

}
