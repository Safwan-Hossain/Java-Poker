package com.poker.application.orchestrator;

import com.poker.enumeration.GameEvent;
import com.poker.domain.game.ServerGame;
import com.poker.infrastructure.communication.update.GameUpdate;
import com.poker.services.GameUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.function.Consumer;

import static com.poker.constants.Constants.DELAY_BETWEEN_SERVER_EVENTS;
import static com.poker.enumeration.RoundState.SHOWDOWN;
import static com.poker.enumeration.GameEvent.ROUND_STATE_ADVANCED;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoundFlowManager {

    private final GameUpdateService updateService;

    public Mono<GameEvent> advanceRoundState(ServerGame game, Consumer<GameUpdate> broadcast) {
        advanceStateAndBroadcast(game, broadcast);

        if (game.everyoneIsAllIn() && game.getRoundState() != SHOWDOWN) {
            return autoAdvanceToShowdown(game, broadcast);
        } else {
            return Mono.just(ROUND_STATE_ADVANCED);
        }
    }

    private Mono<GameEvent> autoAdvanceToShowdown(ServerGame game, Consumer<GameUpdate> broadcast) {
        log.info("AUTO ADVANCING with ROUND STATE: {}", game.getRoundState());
        if (game.getRoundState() == SHOWDOWN) {
            return Mono.just(ROUND_STATE_ADVANCED);
        }

        return Mono.delay(Duration.ofMillis(DELAY_BETWEEN_SERVER_EVENTS))
                .doOnNext(__ -> advanceStateAndBroadcast(game, broadcast))
                .then(autoAdvanceToShowdown(game, broadcast));
    }

    private void advanceStateAndBroadcast(ServerGame game, Consumer<GameUpdate> broadcast) {
        game.advanceRoundState();
        broadcast.accept(updateService.generateRoundStateUpdate(game));
    }
}
