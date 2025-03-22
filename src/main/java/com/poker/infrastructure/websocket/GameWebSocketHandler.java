package com.poker.infrastructure.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poker.domain.player.Player;
import com.poker.infrastructure.communication.update.GameUpdate;
import com.poker.infrastructure.communication.update.impl.PlayerSetupUpdate;
import com.poker.server.GameTableSession;
import com.poker.services.GameTableRegistry;
import com.poker.util.WebSocketUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.CloseStatus;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;

import static com.poker.constants.Constants.UPDATE_BUFFER_SIZE;
import static reactor.core.publisher.BufferOverflowStrategy.DROP_OLDEST;

@Slf4j
@Component
public class GameWebSocketHandler implements WebSocketHandler {
    private static final int INBOUND_PROCESSING_CONCURRENCY = 5;
    private final GameTableRegistry tableRegistry;
    private final ObjectMapper objectMapper;
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final Duration RETRY_BACKOFF_DURATION = Duration.ofSeconds(1);

    public GameWebSocketHandler(GameTableRegistry tableRegistry, ObjectMapper objectMapper) {
        this.tableRegistry = tableRegistry;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        log.info("New WebSocket connection: sessionId={}, url={}", session.getId(), session.getHandshakeInfo().getUri());
        GameTableSession tableSession;

        Map<String, String> queryParams = WebSocketUtil.extractQueryParams(session);
        String playerName  = WebSocketUtil.extractPlayerName(queryParams);
        boolean isHost = WebSocketUtil.isNewGame(queryParams);

        String tableId = WebSocketUtil.extractTableId(session);
        if (isHost) {
            tableSession = tableRegistry.createNewTableSession();
        }
        else if (tableRegistry.doesSessionExist(tableId)) {
            tableSession = tableRegistry.getTableSession(tableId);
        }
        else {
            log.warn("Rejecting WebSocket connection: Invalid table ID or session does not exist. Given ID: {}", tableId);
            return session.close(CloseStatus.NOT_ACCEPTABLE);
        }

        Player player = tableSession.addPlayer(playerName, isHost, session);

        PlayerSetupUpdate updatedInfo = PlayerSetupUpdate
                .builder()
                .tableSessionId(tableSession.getSessionId())
                .playerName(player.getName())
                .playerId(player.getPlayerId())
                .isHost(player.isHost())
                .existingPlayers(tableSession.getAllPlayersInLobby())
                .build();


        Mono<Void> sendPlayerAssignment = sendPlayerAssignment(session, updatedInfo);
        Mono<Void> outbound = setupOutboundMessages(session, tableSession);
        Mono<Void> inbound = setupInboundMessages(session, tableSession);

        return sendPlayerAssignment
                .then(onNewPlayerJoined(tableSession, player))
                .then(Mono.when(outbound, inbound))
                .then(startTableSession(tableSession))
                .doOnSubscribe(sub -> log.info("WebSocket session handling started"))
                .doOnSuccess(unused -> log.info("WebSocket session handling completed"))
                .doOnError(e -> log.error("WebSocket session handling error: {}", e.getMessage(), e));
    }


    private Mono<Void> startTableSession(GameTableSession tableSession) {
        return Mono.fromRunnable(tableSession::startSession);
    }
    private Mono<Void> onNewPlayerJoined(GameTableSession tableSession, Player player) {
        return Mono.fromRunnable(() -> tableSession.onNewPlayerJoined(player));
    }


    private String convertToJson(GameUpdate update) {
        try {
            return objectMapper.writeValueAsString(update);
        } catch (Exception e) {
            throw new RuntimeException("JSON serialization failed", e);
        }
    }

    private Mono<Void> sendPlayerAssignment(WebSocketSession session, PlayerSetupUpdate playerInfo) {
        String playerInfoJson = prepareAndConvertUpdate(playerInfo);
        WebSocketMessage message = session.textMessage(playerInfoJson);

        return session.send(Mono.just(message))
                .doOnSuccess(unused -> logAssignmentSuccess(session, playerInfo))
                .doOnError(error -> logAssignmentFailure(session, playerInfo, error))
                .retryWhen(Retry.backoff(MAX_RETRY_ATTEMPTS, RETRY_BACKOFF_DURATION))
                .onErrorResume(error -> handleAssignmentFailure(session, playerInfo, error));
    }
    private void logAssignmentSuccess(WebSocketSession session, PlayerSetupUpdate playerInfo) {
        log.info("Player assignment successful. Session ID: {}, Player ID: {}, Player Name: {}",
                session.getId(), playerInfo.getPlayerId(), playerInfo.getPlayerName());
    }

    private void logAssignmentFailure(WebSocketSession session, PlayerSetupUpdate playerInfo, Throwable error) {
        log.error("Failed to send player assignment. Session ID: {}, Player ID: {}, Player Name: {}",
                session.getId(), playerInfo.getPlayerId(), playerInfo.getPlayerName(), error);
    }

    private Mono<Void> handleAssignmentFailure(WebSocketSession session, PlayerSetupUpdate playerInfo, Throwable error) {
        log.error("Player assignment retries failed. Closing WebSocket session. Session ID: {}, Player ID: {}, Player Name: {}",
                session.getId(), playerInfo.getPlayerId(), playerInfo.getPlayerName(), error);
        return session.close(); // Close the session so that it doesn't subscribe in the handle method
    }

    // Note - If a client lags behind too much (e.g. 35 updates) then they will lose the oldest update. This is a very
    // unlikely scenario
    private Mono<Void> setupOutboundMessages(WebSocketSession session, GameTableSession tableSession) {
        return session.send(
                        tableSession.getBroadcastFlux()
                                .map(this::prepareAndConvertUpdate)
                                .map(session::textMessage)
                                .onBackpressureBuffer(UPDATE_BUFFER_SIZE, DROP_OLDEST)
                )
                .doOnSubscribe(sub -> log.info("Outbound WebSocket stream started"))
                .doOnError(e -> log.error("Error in outbound WebSocket stream: {}", e.getMessage(), e))
                .then(); // Ensures it returns Mono<Void>
    }

    private Mono<Void> setupInboundMessages(WebSocketSession session, GameTableSession tableSession) {
        return session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .doOnNext(msg -> log.info("Received WebSocket Message: {}", msg))
                .flatMap(tableSession::processClientMessage, INBOUND_PROCESSING_CONCURRENCY)
                .doOnSubscribe(sub -> log.info("Inbound WebSocket stream started for table ID: {}", tableSession.getSessionId()))
                .doOnError(e -> log.error("Error in inbound WebSocket stream: {}", e.getMessage(), e))
                .then(); // Ensures Mono<Void>
    }

    private String prepareAndConvertUpdate(GameUpdate update) {
        update.recordBroadcastTime();
        return convertToJson(update);
    }

}