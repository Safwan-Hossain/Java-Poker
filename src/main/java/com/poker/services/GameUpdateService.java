package com.poker.services;

import com.poker.domain.game.ServerGame;
import com.poker.domain.player.Card;
import com.poker.domain.player.HandEvaluation;
import com.poker.domain.player.Player;
import com.poker.domain.player.RoundResult;
import com.poker.enumeration.PlayerAction;
import com.poker.enumeration.RoundState;
import com.poker.infrastructure.communication.update.GameUpdate;
import com.poker.infrastructure.communication.update.impl.*;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.poker.constants.Constants.INVALID_BET_AMOUNT;

@Service
public class GameUpdateService {

    public PlayerActionUpdate generateAutoQuitUpdate(Player player, int updatedTotalPot) {
        return PlayerActionUpdate.builder()
                .playerId(player.getPlayerId())
                .betAmount(INVALID_BET_AMOUNT)
                .action(PlayerAction.QUIT)
                .updatedPlayerChips(player.getChips())
                .updatedTotalPot(updatedTotalPot)
                .build();
    }
    public PlayerActionUpdate generatePlayerActionUpdate(Player player, int betAmount, PlayerAction playerAction, int updatedTotalPot) {
        return PlayerActionUpdate.builder()
                .playerId(player.getPlayerId())
                .betAmount(betAmount)
                .action(playerAction)
                .updatedPlayerChips(player.getChips())
                .updatedTotalPot(updatedTotalPot)
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
        int minimumBetAmount = serverGame.getMinimumBetAmount();
        int maximumBetAmount = playerWithTurn.getChips();
        int minimumCallAmount = serverGame.getMinimumCallAmount();
        HashSet<PlayerAction> playerActions = serverGame.getValidActions(playerWithTurn);

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
                .bankruptPlayers(serverGame.getBankruptPlayers())
                .tableCards(serverGame.getTableCards())
                .playerHandEvaluations(mapPlayerHandEvaluations(roundResult.getPlayerHandEvaluations())) // Convert to String Map
                .winnersForThisRound(roundResult.getWinners())
                .totalPot(roundResult.getTotalPot())
                .sharePerWinner(roundResult.getSharePerWinner())
                .build();
    }

    //  converts player objects to player IDs
    private Map<String, HandEvaluation> mapPlayerHandEvaluations(Map<Player, HandEvaluation> playerHandEvaluations) {
        return playerHandEvaluations.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().getPlayerId(),
                        Map.Entry::getValue
                ));
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

    // TODO - In future add more stuff (e.g. leaderboard, total game time)
    public GameUpdate generateGameOverUpdate(ServerGame serverGame) {
        Player richestPlayer = serverGame.getRichestPlayer();
        return GameOverUpdate.builder()
                .winner(richestPlayer)
                .build();
    }
}
