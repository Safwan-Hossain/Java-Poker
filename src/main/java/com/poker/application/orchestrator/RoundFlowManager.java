package com.poker.application.orchestrator;

import com.poker.enumeration.GameEvent;
import com.poker.domain.game.ServerGame;
import com.poker.infrastructure.communication.update.GameUpdate;
import com.poker.services.GameUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.function.Consumer;

import static com.poker.constants.Constants.DELAY_BETWEEN_SERVER_EVENTS;
import static com.poker.enumeration.GameEvent.ASSIGN_NEXT_PLAYER_TURN;
import static com.poker.enumeration.GameEvent.ROUND_END;
import static com.poker.enumeration.RoundState.SHOWDOWN;

@Component
@RequiredArgsConstructor
public class RoundFlowManager {

    private final GameUpdateService updateService;

    public Mono<GameEvent> advanceRound(ServerGame game, Consumer<GameUpdate> broadcast) {
        game.advanceRoundState();
        broadcast.accept(updateService.generateRoundStateUpdate(game));

        if (game.getRoundState() == SHOWDOWN) {
            return Mono.just(ROUND_END);
        } else if (game.everyoneIsAllIn()) {
            return autoAdvanceToShowdown(game, broadcast);
        } else {
            return Mono.just(ASSIGN_NEXT_PLAYER_TURN);
        }
    }

    private Mono<GameEvent> autoAdvanceToShowdown(ServerGame game, Consumer<GameUpdate> broadcast) {
        if (game.getRoundState().equals(SHOWDOWN)) {
            return Mono.just(ROUND_END);
        }

        return Mono.delay(Duration.ofMillis(DELAY_BETWEEN_SERVER_EVENTS))
                .doOnNext(__ -> {
                    game.advanceRoundState();
                    broadcast.accept(updateService.generateRoundStateUpdate(game));
                })
                .then(autoAdvanceToShowdown(game, broadcast));
    }
}
