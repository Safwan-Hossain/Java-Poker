package com.poker.statemachine.guards.impl;

import com.poker.constants.Constants;
import com.poker.domain.game.ServerGame;
import com.poker.domain.player.Player;
import com.poker.enumeration.GameEvent;
import com.poker.enumeration.GameState;
import com.poker.infrastructure.communication.update.impl.PlayerActionUpdate;
import com.poker.services.GameTableRegistry;
import com.poker.statemachine.guards.BaseGuard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class IsPlayerActionValidGuard extends BaseGuard {

    public IsPlayerActionValidGuard(GameTableRegistry gameTableRegistry) {
        super(gameTableRegistry);
    }

    @Override
    public boolean evaluate(StateContext<GameState, GameEvent> context) {
        PlayerActionUpdate update = extractPlayerActionUpdate(context);

        if (!isValid(update)) {
            log.warn("Invalid PlayerActionUpdate: Missing player ID or action");
            return false;
        }

        if (!isCorrectPlayerTurn(context, update)) {
            log.warn("Player [{}] attempted a move but it's not their turn", update.getPlayerId());
            return false;
        }

        storePlayerActionInContext(context, update);
        return true;
    }

    private boolean isValid(PlayerActionUpdate update) {
        return update != null && update.getPlayerId() != null && update.getAction() != null;
    }

    private boolean isCorrectPlayerTurn(StateContext<GameState, GameEvent> context, PlayerActionUpdate update) {
        ServerGame game = getServerGame(context);
        Player currentTurnPlayer = game.getPlayerWithTurn();
        return currentTurnPlayer != null && currentTurnPlayer.getPlayerId().equals(update.getPlayerId());
    }

    private PlayerActionUpdate extractPlayerActionUpdate(StateContext<GameState, GameEvent> context) {
        return Optional.of(context.getMessageHeaders().get(Constants.GAME_UPDATE_HEADER_NAME))
                .filter(PlayerActionUpdate.class::isInstance)
                .map(PlayerActionUpdate.class::cast)
                .orElse(null);
    }

    private void storePlayerActionInContext(StateContext<GameState, GameEvent> context, PlayerActionUpdate update) {
        context.getExtendedState().getVariables().put(Constants.PLAYER_ACTION_UPDATE_HEADER_NAME, update);
    }
}
