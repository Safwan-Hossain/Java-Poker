package com.poker.application.orchestrator;

import com.poker.enumeration.GameEvent;
import com.poker.enumeration.PlayerAction;
import com.poker.domain.game.ServerGame;
import com.poker.domain.player.Player;
import com.poker.domain.player.RoundResult;
import com.poker.infrastructure.communication.GameMessenger;
import com.poker.infrastructure.communication.update.GameUpdate;
import com.poker.infrastructure.communication.update.impl.GameStateSnapshotUpdate;
import com.poker.infrastructure.communication.update.impl.PlayerActionUpdate;
import com.poker.services.GameTableRegistry;
import com.poker.services.GameUpdateService;
import com.poker.util.RoundResultEvaluator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GameLogicHandler {

    private final GameTableRegistry registry;
    private final GameUpdateService updateService;
    private final PlayerActionManager actionHandler;
    private final RoundFlowManager roundFlowManager;


    public Mono<GameEvent> startNextRound(String tableId) {
        ServerGame game = getGame(tableId);

        // 1. Disconnect bankrupt players
        List<Player> bankruptPlayers = game.getPlayersWithNoChips();
        if (!bankruptPlayers.isEmpty()) {
            registry.getTableSession(tableId).disconnectPlayers(bankruptPlayers);
        }

        // 2. Initialize new round
        game.initializeRound();

        // 3. Generate and broadcast updates for each player
        List<GameStateSnapshotUpdate> updates = updateService.generateNewRoundUpdates(game);
        for (GameStateSnapshotUpdate update : updates) {
            getMessenger(tableId).sendUpdateToPlayer(update.getTargetPlayerIdToUpdate(), update);
        }


        // 4. Signal that round has been initialized
        return Mono.just(GameEvent.ROUND_INITIALIZED);
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

        // 1. Apply the player's action
        GameEvent outcome = actionHandler.handle(game, playerId, action, betAmount);

        // 2. Broadcast the player's action and resulting state
        GameUpdate update = updateService.generatePlayerActionUpdate(playerId, game.getPlayerBetting(playerId), action);
        getMessenger(tableId).sendUpdateToAllPlayers(update);

        // 3. Return the resulting game event
        return Mono.just(outcome);
    }



    public Mono<GameEvent> advanceRound(String tableId) {
        ServerGame game = getGame(tableId);
        // Use the round flow manager to advance the round and broadcast changes
        return roundFlowManager.advanceRound(game, getMessenger(tableId)::sendUpdateToAllPlayers);
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

    public PlayerActionUpdate onPlayerTurnTimeout(String tableId) {
        String playerId = getGame(tableId).getPlayerWithTurn().getPlayerId();
        return this.updateService.generatePlayerActionUpdate(playerId, -1, PlayerAction.FOLD);
    }


    private ServerGame getGame(String tableId) {
        return registry.getTableSession(tableId).getServerGame();
    }

    private GameMessenger getMessenger(String tableId) {
        return registry.getTableSession(tableId).getGameMessenger();
    }
}
