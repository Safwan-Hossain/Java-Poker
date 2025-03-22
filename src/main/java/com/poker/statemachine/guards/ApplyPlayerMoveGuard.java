package com.poker.statemachine.guards;

import com.poker.constants.Constants;
import com.poker.enumeration.GameEvent;
import com.poker.enumeration.GameState;
import com.poker.domain.game.ServerGame;
import com.poker.domain.player.Player;
import com.poker.infrastructure.communication.update.impl.PlayerActionUpdate;
import com.poker.server.GameTableSession;
import com.poker.services.GameTableRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class ApplyPlayerMoveGuard implements Guard<GameState, GameEvent> {


    private final GameTableRegistry gameTableRegistry;

    public ApplyPlayerMoveGuard(GameTableRegistry gameTableRegistry) {
        this.gameTableRegistry = gameTableRegistry;
    }


    @Override
    public boolean evaluate(StateContext<GameState, GameEvent> context) {
        PlayerActionUpdate playerActionUpdate = getPlayerActionUpdateFromHeaders(context);


        if (!isValidPlayerActionUpdate(playerActionUpdate)) {
            log.warn("Invalid PlayerActionUpdate: Missing player or action");
            return false;
        }

        ServerGame serverGame = getServerGame(context);
        Player playerWithTurn = serverGame.getPlayerWithTurn();
        String expectedId = playerWithTurn.getPlayerId();
        String receivedId = playerActionUpdate.getPlayerId();

        boolean isCorrectPlayerTurn  = expectedId.equals(receivedId);

        if (!isCorrectPlayerTurn) {
            log.warn("Player [{}] attempted a move but it's not their turn", playerActionUpdate.getPlayerId());
            return false;
        }
        storePlayerAction(context, playerActionUpdate);
        return true;
    }


    /**
     * Checks if PlayerActionUpdate object is valid
     */
    private boolean isValidPlayerActionUpdate(PlayerActionUpdate playerActionUpdate) {
        return playerActionUpdate.getPlayerId() != null && playerActionUpdate.getAction() != null;
    }


    /**
     * Retrieves the game session based on the state machine context
     */
    private GameTableSession getGameSession(StateContext<GameState, GameEvent> context) {
        String tableId = context.getStateMachine().getId();
        if (!gameTableRegistry.doesSessionExist(tableId)) {
            throw new RuntimeException("Could not find table session for ID: "+ tableId);
        }
        return gameTableRegistry.getTableSession(tableId);
    }

    /**
     * Retrieves the server game instance for this table
     */
    private ServerGame getServerGame(StateContext<GameState, GameEvent> context) {
        return getGameSession(context).getServerGame();
    }

    private PlayerActionUpdate getPlayerActionUpdateFromHeaders(StateContext<GameState, GameEvent> context) {
        return Optional.of(context.getMessageHeaders().get(Constants.GAME_UPDATE_HEADER_NAME))
                .filter(PlayerActionUpdate.class::isInstance)
                .map(PlayerActionUpdate.class::cast)
                .orElse(null);
    }

    private void storePlayerAction(StateContext<GameState, GameEvent> context, PlayerActionUpdate playerActionUpdate) {
        context.getExtendedState().getVariables().put(Constants.PLAYER_ACTION_UPDATE_HEADER_NAME, playerActionUpdate);
    }
}
