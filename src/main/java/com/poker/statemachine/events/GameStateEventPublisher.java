package com.poker.statemachine.events;

import com.poker.constants.Constants;
import com.poker.enumeration.GameEvent;
import com.poker.enumeration.GameState;
import com.poker.infrastructure.communication.update.GameUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class GameStateEventPublisher {

    public void publish(StateMachine<GameState, GameEvent> stateMachine, GameEvent event) {
        Message<GameEvent> message = MessageBuilder.withPayload(event).build();
        stateMachine.sendEvent(Mono.just(message)).subscribe();
    }

    public void publish(StateMachine<GameState, GameEvent> stateMachine, GameEvent event, GameUpdate update) {
        Message<GameEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(Constants.GAME_UPDATE_HEADER_NAME, update)
                .build();
        stateMachine.sendEvent(Mono.just(message)).subscribe();
    }
}
