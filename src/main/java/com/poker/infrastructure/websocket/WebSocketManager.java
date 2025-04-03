package com.poker.infrastructure.websocket;


import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.socket.CloseStatus;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@NoArgsConstructor
public class WebSocketManager {

    private final Map<String, WebSocketSession> webSockets = new ConcurrentHashMap<>();

    public void addNewConnection(String playerId, WebSocketSession webSocketSession) {
        webSockets.put(playerId, webSocketSession);
    }

    public void sendUpdateToSpecificPlayer(String playerId, String playerUpdate) {
        if (!webSockets.containsKey(playerId)) {
            log.error("Failed to find and send specific message to Player ID: {}", playerId);
        }

        WebSocketSession playerWebSocket = webSockets.get(playerId);
        playerWebSocket.send(Mono.just(playerWebSocket.textMessage(playerUpdate))).subscribe();
    }

    private Mono<Void> internalDisconnectPlayers(String playerId) {
        return Mono.fromRunnable(() -> webSockets.computeIfPresent(playerId, (key, session) -> {
            if (session.isOpen()) {
                session.close(CloseStatus.NORMAL).subscribe();
            }
            return null;
        }));
    }

    public void disconnectPlayers(String playerId) {
        internalDisconnectPlayers(playerId).subscribe();
    }
    public void disconnectPlayers(List<String> playerIds) {
        Flux.fromIterable(playerIds)
                .flatMap(this::internalDisconnectPlayers)
                .then()
                .subscribe();
    }


    public void closeAllSessions() {
        Flux.fromIterable(webSockets.values())
                // For each session, if open, attempt to close; otherwise do nothing.
                .flatMap(session -> {
                    if (session.isOpen()) {
                        return session.close(CloseStatus.NORMAL);
                    } else {
                        return Mono.empty();
                    }
                })
                // If closing any session fails, log the error but continue closing the rest.
                .onErrorContinue((error, session) -> log.warn("Error closing session: {}", error.getMessage(), error))
                // Once all close attempts are done, clear the local webSockets map.
                .then(Mono.fromRunnable(() -> {
                    webSockets.clear();
                    log.info("All WebSocket sessions have been closed and cleared");
                }))
                .subscribe();
    }


}
