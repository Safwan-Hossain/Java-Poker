package model.update;

import model.game.Game;
import model.update.impl.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class GameUpdateHandler {
    private final Game game;
    private final Map<Class<? extends GameUpdate>, Consumer<? extends GameUpdate>> updateProcessors;

    public GameUpdateHandler(Game game) {
        this.game = game;
        this.updateProcessors = new HashMap<>();
        initializeProcessors();
    }

    private void initializeProcessors() {
        registerProcessor(GameStateUpdate.class, this::processGameStateUpdate);
        registerProcessor(PlayerActionUpdate.class, this::processPlayerActionUpdate);
        registerProcessor(PlayerTurnUpdate.class, this::processPlayerTurnUpdate);
        registerProcessor(RoundStateUpdate.class, this::processRoundStateUpdate);
        registerProcessor(ServerMessageUpdate.class, this::processServerMessageUpdate);
        registerProcessor(ConnectionStatusUpdate.class, this::processConnectionStatusUpdate);
    }

    private <T extends GameUpdate> void registerProcessor(Class<T> type, Consumer<T> processor) {
        updateProcessors.put(type, processor);
    }

    @SuppressWarnings("unchecked")
    public void handleUpdate(GameUpdate update) {
        Consumer<GameUpdate> processor = (Consumer<GameUpdate>) updateProcessors.get(update.getClass());
        if (processor != null) {
            processor.accept(update);
        }
        else {
            handleUnknownUpdate(update);
        }
    }

    private void handleUnknownUpdate(GameUpdate update) {
        System.err.println("No processor found for update type: " + update.getClass().getSimpleName());
    }

    private void processGameStateUpdate(GameStateUpdate update) {
//        game.setGameState(update.getGameState());
    }

    private void processPlayerActionUpdate(PlayerActionUpdate update) {
        game.applyPlayerAction(update.getPlayer(), update.getAction(), update.getBetAmount());
    }

    private void processPlayerTurnUpdate(PlayerTurnUpdate update) {
        game.setPlayerWithTurn(update.getPlayerWithTurn());
    }

    private void processRoundStateUpdate(RoundStateUpdate update) {
//        game.advanceRoundState(update.getRoundState());
    }

    private void processServerMessageUpdate(ServerMessageUpdate update) {
        System.out.println("Server message: " + update.getServerMessage());
    }

    private void processConnectionStatusUpdate(ConnectionStatusUpdate update) {
//        game.updateConnectionStatus(update.getPlayer(), update.getStatus());
    }
}
