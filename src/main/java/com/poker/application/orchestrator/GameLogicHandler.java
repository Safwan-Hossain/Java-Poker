package com.poker.application.orchestrator;

import com.poker.domain.game.ServerGame;
import com.poker.domain.player.Player;
import com.poker.domain.player.RoundResult;
import com.poker.enumeration.GameEvent;
import com.poker.enumeration.PlayerAction;
import com.poker.infrastructure.communication.GameMessenger;
import com.poker.infrastructure.communication.update.GameUpdate;
import com.poker.infrastructure.communication.update.impl.GameStateSnapshotUpdate;
import com.poker.infrastructure.communication.update.impl.PlayerActionUpdate;
import com.poker.services.GameTableRegistry;
import com.poker.services.GameUpdateService;
import com.poker.util.RoundResultEvaluator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

import static com.poker.constants.Constants.MAX_MISSED_CONSECUTIVE_TURNS;

@Service
@RequiredArgsConstructor
public class GameLogicHandler {

    private final GameTableRegistry registry;
    private final GameUpdateService updateService;
    private final RoundFlowManager roundFlowManager;

    public Mono<GameEvent> startNextRound(String tableId) {
        ServerGame game = getGame(tableId);

        // 1. Disconnect and remove bankrupt players
        List<Player> bankruptPlayers = game.getBankruptPlayers();
        if (!bankruptPlayers.isEmpty()) {
            registry.getTableSession(tableId).disconnectPlayers(bankruptPlayers);
            game.getMainGame().removeBankruptPlayers();
        }

        if (game.isOnlyOnePlayerLeft()) {
            return Mono.just(GameEvent.GAME_OVER);
        }

        // 2. Initialize new round
        game.initializeRound();

        // 3. Generate and broadcast updates for each player
        List<GameStateSnapshotUpdate> updates = updateService.generateNewRoundUpdates(game);
        for (GameStateSnapshotUpdate update : updates) {
            getMessenger(tableId).sendUpdateToPlayer(update.getTargetPlayerIdToUpdate(), update);
        }

        // 4. Signal that round has been initialized
        return Mono.just(GameEvent.ROUND_STARTED);
    }

    public void assignFirstPlayerTurn(String tableId) {
        ServerGame game = getGame(tableId);

        // 1. Set first player's turn
        game.giveFirstPlayerTurn();

        // 2. Broadcast turn update
        GameUpdate update = updateService.generateTurnUpdate(game);
        getMessenger(tableId).sendUpdateToAllPlayers(update);
    }


    public void assignNextPlayerTurn(String tableId) {
        ServerGame game = getGame(tableId);

        // 1. Move to next player's turn
        game.giveNextPlayerTurn();

        // 2. Broadcast turn update
        GameUpdate update = updateService.generateTurnUpdate(game);
        getMessenger(tableId).sendUpdateToAllPlayers(update);
    }


    public Mono<GameEvent> applyPlayerMove(String tableId, String playerId, PlayerAction action, int betAmount) {
        ServerGame game = getGame(tableId);
        game.getPlayerWithTurn().resetMissedTurns();

        // 1. Apply the player's action
        game.applyPlayerAction(playerId, action, betAmount);

        // 2. Broadcast the player's action and resulting state
        GameUpdate update = updateService.generatePlayerActionUpdate(playerId, betAmount, action);
        getMessenger(tableId).sendUpdateToAllPlayers(update);

        // 3. Return the resulting game event
        return Mono.just(GameEvent.PLAYER_ACTION_APPLIED);
    }



    public Mono<GameEvent> advanceRoundState(String tableId) {
        ServerGame game = getGame(tableId);
        // Use the round flow manager to advance the round and broadcast changes
        return roundFlowManager.advanceRoundState(game, getMessenger(tableId)::sendUpdateToAllPlayers);
    }

    public Mono<GameEvent> handleRoundEnd(String tableId, Duration delayBeforeNextRound) {
        ServerGame game = getGame(tableId);

        // 1. Calculate round result
        RoundResult result = RoundResultEvaluator.calculateRoundResult(game.getMainGame());

        // 2. Distribute chips to winners
        result.getWinners().forEach(player -> game.giveChipsToPlayer(result.getSharePerWinner(), player));

        // 3. Broadcast showdown info
        GameUpdate showdownUpdate = updateService.getShowdownGameUpdate(game, result);
        getMessenger(tableId).sendUpdateToAllPlayers(showdownUpdate);

        // 4. Determine next event
        if (game.isGameOver()) {
            return Mono.just(GameEvent.GAME_OVER);
        } else {
            return Mono.delay(delayBeforeNextRound)
                    .thenReturn(GameEvent.START_NEXT_ROUND);
        }
    }

    public void handlePlayerTimeout(String tableId) {
        Player playerWithTurn = getGame(tableId).getPlayerWithTurn();
        playerWithTurn.incrementMissedTurn();
        String playerId = playerWithTurn.getPlayerId();

        if (playerWithTurn.getConsecutiveMissedTurns() >= MAX_MISSED_CONSECUTIVE_TURNS){
            PlayerActionUpdate playerQuitUpdate = this.updateService.generateAutoQuitUpdate(playerId);
            // Change game state
            getGame(tableId).applyPlayerAction(playerId, playerQuitUpdate.getAction(), playerQuitUpdate.getBetAmount());
            // Send quit update to all players then disconnect timed out player
            getMessenger(tableId).sendUpdateToAllPlayers(playerQuitUpdate);
            getMessenger(tableId).disconnectPlayers(playerId);
        }
    }

    public void handleGameOver(String tableId) {
        ServerGame game = getGame(tableId);
        GameUpdate gameOverUpdate = this.updateService.generateGameOverUpdate(game);
        getMessenger(tableId).sendUpdateToAllPlayers(gameOverUpdate);
        stopTableSession(tableId);
    }

    private void stopTableSession(String tableId) {
        registry.removeTableService(tableId);
    }
    private ServerGame getGame(String tableId) {
        return registry.getTableSession(tableId).getServerGame();
    }

    private GameMessenger getMessenger(String tableId) {
        return registry.getTableSession(tableId).getGameMessenger();
    }

}
