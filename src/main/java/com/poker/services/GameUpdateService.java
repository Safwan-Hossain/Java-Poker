package com.poker.services;

import com.poker.enumeration.PlayerAction;
import com.poker.enumeration.RoundState;
import com.poker.domain.game.ServerGame;
import com.poker.domain.player.Card;
import com.poker.domain.player.Player;
import com.poker.domain.player.RoundResult;
import com.poker.infrastructure.communication.update.impl.*;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GameUpdateService {

    public PlayerActionUpdate generatePlayerActionUpdate(String playerId, int betAmount, PlayerAction playerAction) {
        return PlayerActionUpdate.builder()
                .playerId(playerId)
                .betAmount(betAmount)
                .action(playerAction)
                .build();
    }

    public RoundStateUpdate generateRoundStateUpdate(ServerGame serverGame) {
        return RoundStateUpdate.builder()
                .roundState(serverGame.getRoundState())
                .tableCards(serverGame.getTableCards())
                .build();
    }

    public PlayerTurnUpdate generateTurnUpdate(ServerGame serverGame) {
        Player playerWithTurn = serverGame.getPlayerWithTurn();
        int minimumBetAmount = serverGame.getMainGame().getMinimumBetAmount();
        int maximumBetAmount = playerWithTurn.getChips(); // Confirm if this is max bet
        int minimumCallAmount = serverGame.getMainGame().getMinimumCallAmount();
        HashSet<PlayerAction> playerActions = serverGame.getMainGame().getValidActions(playerWithTurn);

        return PlayerTurnUpdate.builder()
                .playerIdWithTurn(playerWithTurn.getPlayerId())
                .maximumBetAmount(maximumBetAmount)
                .minimumBetAmount(minimumBetAmount)
                .minimumCallAmount(minimumCallAmount)
                .validPlayerActions(playerActions)
                .build();
    }

    public ShowdownResultUpdate getShowdownGameUpdate(ServerGame serverGame, RoundResult roundResult) {
        return ShowdownResultUpdate.builder()
                .players(serverGame.getPlayersCopy())
                .bankruptPlayers(serverGame.getPlayersWithNoChips())
                .tableCards(serverGame.getTableCards())
                .playerHandEvaluations(roundResult.getPlayerHandEvaluations())
                .winnersForThisRound(roundResult.getWinners())
                .totalPot(roundResult.getTotalPot())
                .sharePerWinner(roundResult.getSharePerWinner())
                .build();
    }

    public List<GameStateSnapshotUpdate> generateNewRoundUpdates(ServerGame serverGame) {
        List<Player> players = serverGame.getPlayersCopy();

        Map<String, List<Card>> playerIdToHands = players.stream()
                .collect(Collectors.toMap(Player::getPlayerId, Player::getHand));

        return players.stream()
                .map(player -> generateNewRoundUpdate(player.getPlayerId(), serverGame, playerIdToHands, players))
                .collect(Collectors.toList());
    }

    private GameStateSnapshotUpdate generateNewRoundUpdate(String playerIdForThisUpdate, ServerGame serverGame,
                                                           Map<String, List<Card>> playerIdToHands, List<Player> players) {
        Player playerWithTurn = serverGame.getPlayerWithTurn();
        List<Card> playerHand = playerIdToHands.get(playerIdForThisUpdate);

        return GameStateSnapshotUpdate.builder()
                .roundState(RoundState.PRE_FLOP)
                .targetPlayerIdToUpdate(playerIdForThisUpdate)
                .tableCards(serverGame.getTableCards())
                .playerIdWithTurn(playerWithTurn.getPlayerId())
                .sanitizedPlayers(players)
                .playerBettings(serverGame.getPlayerIdsToBettings())
                .playerHand(playerHand)
                .build();
    }
}
