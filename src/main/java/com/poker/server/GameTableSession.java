package com.poker.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poker.domain.game.ServerGame;
import com.poker.domain.player.Player;
import com.poker.enumeration.ConnectionStatus;
import com.poker.enumeration.GameEvent;
import com.poker.enumeration.GameState;
import com.poker.infrastructure.communication.GameMessenger;
import com.poker.infrastructure.communication.update.GameUpdate;
import com.poker.infrastructure.communication.update.impl.ConnectionStatusUpdate;
import com.poker.infrastructure.communication.update.impl.PlayerActionUpdate;
import com.poker.statemachine.events.GameStateEventPublisher;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateMachine;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class GameTableSession {

    private final GameLobby gameLobby = new GameLobby(new GameSettings());
    @Getter
    private final ServerGame serverGame = new ServerGame();
    @Getter
    private final String sessionId;
    @Getter
    private final GameMessenger gameMessenger;
    private final ObjectMapper objectMapper;
    private final StateMachine<GameState, GameEvent> stateMachine;
    private final GameStateEventPublisher eventPublisher;


    @Getter
    private boolean hasSessionStarted = false;

    public GameTableSession(String sessionId, ObjectMapper objectMapper, StateMachine<GameState, GameEvent> stateMachine, GameStateEventPublisher eventPublisher) {
        this.sessionId = sessionId;
        this.objectMapper = objectMapper;
        this.eventPublisher = eventPublisher;
        this.stateMachine = stateMachine;
        this.gameMessenger = new GameMessenger(objectMapper);
    }

    public void startSession() {
        if (!hasSessionStarted) {
            hasSessionStarted = true;
            startStateMachine();
            log.info("Starting state machine for table session: {}", this.getSessionId());
        }
    }

    public void initializeServerGame() {
        this.serverGame.initializeGame(gameLobby);
    }

    public List<Player> getAllPlayersInLobby() {
        return gameLobby.getPlayersListCopy();
    }
    public void onNewPlayerJoined(Player player) {
        ConnectionStatusUpdate connectionStatusUpdate = ConnectionStatusUpdate.builder()
                .connectionStatus(ConnectionStatus.JOINED)
                .playerId(player.getPlayerId())
                .playerName(player.getName())
                .build();

        log.info("BROADCASTING UPDATE: {}", connectionStatusUpdate);
        gameMessenger.sendUpdateToAllPlayers(connectionStatusUpdate);
    }
    public void disconnectPlayers(List<Player> players) {
        List<String> playerIds = players.stream().map(Player::getPlayerId).collect(Collectors.toList());
        gameMessenger.disconnectPlayers(playerIds);
    }

    public Player addPlayer(String playerName, boolean isHost, WebSocketSession webSocketSession) {
        Player player = gameLobby.addPlayer(playerName, isHost);
        gameMessenger.addNewConnection(player.getPlayerId(), webSocketSession);
        return player;
    }

    public void startStateMachine() {
        this.stateMachine.startReactively().subscribe();
    }

    public Flux<GameUpdate> getBroadcastFlux() {
        return gameMessenger.getBroadcaster().getBroadcastFlux();
    }

    public Mono<Void> processClientMessage(String message) {
        return Mono.fromCallable(() -> parseMessage(message))
                .subscribeOn(Schedulers.boundedElastic()) // Offload to separate thread pool
                .flatMap(this::handlePlayerUpdate)
                .onErrorResume(e -> {
                    log.error("Error processing client message: {}", e.getMessage(), e);
                    return Mono.empty();
                });
    }


    private Mono<Void> handlePlayerUpdate(GameUpdate update) {
        if (update instanceof PlayerActionUpdate playerAction) {
            return processPlayerAction(playerAction);
        }
        return Mono.empty();
    }

    private Mono<Void> processPlayerAction(PlayerActionUpdate playerAction) {
        return Mono.fromRunnable(() -> {
            switch (playerAction.getAction()) {
                case HOST_SAYS_START -> handleHostStartAction(playerAction);
                case FOLD, RAISE, BET, CALL, CHECK -> handlePlayerMoveAction(playerAction);
                case QUIT, WAIT -> handleIgnoredAction(playerAction);
            }
        });
    }
    private void handleHostStartAction(PlayerActionUpdate update) {
        initializeServerGame();
        this.startSession();
        sendEventWithGameUpdate(GameEvent.HOST_IS_READY, update);
    }

    private void handlePlayerMoveAction(PlayerActionUpdate update) {
        sendEventWithGameUpdate(GameEvent.PLAYER_ACTION_RECEIVED, update);
    }

    private void handleIgnoredAction(PlayerActionUpdate update) {
        log.info("Ignoring player action: {}", update.getAction());
    }

    private GameUpdate parseMessage(String message) throws JsonProcessingException {
        return objectMapper.readValue(message, GameUpdate.class);
    }

    private void sendEventWithGameUpdate(GameEvent gameEvent, GameUpdate gameUpdate) {
        eventPublisher.publish(stateMachine, gameEvent, gameUpdate);
    }
    public void stop() {
        gameMessenger.closeAllSessions();
        stateMachine.stopReactively().block();
        log.info("State machine stopped for session.");
    }
}

