package com.poker.infrastructure.communication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poker.infrastructure.communication.update.GameUpdate;
import com.poker.infrastructure.websocket.WebSocketManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.List;

@AllArgsConstructor
@Slf4j
public class GameMessenger {
    @Getter
    private final GameBroadcaster broadcaster = new GameBroadcaster();
    private final WebSocketManager webSocketManager = new WebSocketManager();
    private final ObjectMapper objectMapper;

    public void sendUpdateToAllPlayers(GameUpdate update) {
        broadcaster.broadcast(update);
    }
    public void sendUpdateToPlayer(String playerId, GameUpdate gameUpdate) {
        try{
            String payload = objectMapper.writeValueAsString(gameUpdate);
            webSocketManager.sendUpdateToSpecificPlayer(playerId, payload);
        }
        catch (JsonProcessingException e) {
            log.error("Failed to convert the following update to JSON String value: {}", gameUpdate, e);
        }
    }

    public void disconnectPlayers(String playerId) {
        webSocketManager.disconnectPlayers(playerId);
    }
    public void disconnectPlayers(List<String> playerIds) {
        webSocketManager.disconnectPlayers(playerIds);
    }

    public void addNewConnection(String playerId, WebSocketSession webSocketSession) {
        webSocketManager.addNewConnection(playerId, webSocketSession);
    }

    public void closeAllSessions() {
        webSocketManager.closeAllSessions();
    }
}
