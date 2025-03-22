package com.poker.infrastructure.communication;

import com.poker.infrastructure.communication.update.GameUpdate;
import lombok.Getter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

public class GameBroadcaster {

    // This is the data source (e.g. the publisher). Subscribers receive updates from the sink
    private final Sinks.Many<GameUpdate> sink = Sinks.many().multicast().directBestEffort();

    // This is the data receiver. Subscribers need to use this method to subscribe to the sink and receive updates

    @Getter
    private final Flux<GameUpdate> broadcastFlux = sink.asFlux();

    public void broadcast(GameUpdate gameUpdate) {
        sink.tryEmitNext(gameUpdate);
    }
}
