package com.poker.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poker.enumeration.GameEvent;
import com.poker.enumeration.GameState;
import com.poker.server.GameTableSession;
import com.poker.statemachine.events.GameStateEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class GameTableRegistry {
    private final Map<String, GameTableSession> tableSessionMap = new ConcurrentHashMap<>();
    private final StateMachineFactory<GameState, GameEvent> stateMachineFactory;
    private final GameStateEventPublisher eventPublisher;

    private final ObjectMapper objectMapper;

    public GameTableRegistry(@Lazy StateMachineFactory<GameState, GameEvent> stateMachineFactory, GameStateEventPublisher eventPublisher, ObjectMapper objectMapper) {
        this.stateMachineFactory = stateMachineFactory;
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
    }

    /**
     * Retrieve an existing service instance or create a new one if none exists
     * for this tableId
     */
    public GameTableSession getTableSession(String tableId) {
        return tableSessionMap.get(tableId);
    }

    /**
     * Checks if table ID currently exists
     */
    public boolean doesSessionExist(String tableId) {
        return tableSessionMap.containsKey(tableId);
    }


    /**
     * Retrieve an existing service instance or create a new one if none exists
     * for this tableId
     */
    public GameTableSession createNewTableSession() {
        String tableId = generateUniqueTableId();
        log.info("New table session created with ID: {}", tableId);
        GameTableSession newSession = createNewSession(tableId);
        tableSessionMap.put(tableId, newSession);
        return newSession;
    }


    /**
     * If tables can end, you might remove them from the registry here
     */
    public void removeTableService(String tableId) {
        GameTableSession session = tableSessionMap.remove(tableId);
        if (session != null) {
            session.stop();
        }
    }

    // Creates a new session internally
    private GameTableSession createNewSession(String id) {
        StateMachine<GameState, GameEvent> stateMachine = stateMachineFactory.getStateMachine(id);
        return new GameTableSession(id, objectMapper, stateMachine, eventPublisher);
    }


    // Generates a unique 6 digit hexadecimal table ID
    private String generateUniqueTableId() {
        Random random = new Random();
        String tableId;

        do {
            tableId = String.format("%06X", random.nextInt(0x1000000));
        } while (tableSessionMap.containsKey(tableId));

        return tableId;
    }
}