package com.poker.util;

import com.poker.domain.game.Game;
import com.poker.domain.player.Card;
import com.poker.domain.player.HandEvaluation;
import com.poker.domain.player.Player;
import com.poker.domain.player.RoundResult;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Evaluates and determines the winner among multiple HandEvaluations in Texas Hold'em.
 */

@Component
public class RoundResultEvaluator {

    public static RoundResult calculateRoundResult(Game game) {
        List<Player> players = game.getPlayersCopy();
        List<Card> tableCards = game.getTableCards();

        Map<HandEvaluation, List<Player>> handEvaluations = calculatePlayerHandEvaluations(players, tableCards);
        Map<Player, HandEvaluation> playerHandEvaluations = getPlayerHandEvaluations(handEvaluations);
        List<Player> winners = getWinningPlayers(handEvaluations);

        int totalPot = game.getTotalPot();
        int sharePerWinner = calculateSharePerWinner(totalPot, winners);

        RoundResult roundResult = new RoundResult();
        roundResult.setWinners(winners);
        roundResult.setPlayerHandEvaluations(playerHandEvaluations);
        roundResult.setTotalPot(totalPot);
        roundResult.setSharePerWinner(sharePerWinner);
        return roundResult;
    }

    // Flips the hand evaluation map
    private static Map<Player, HandEvaluation> getPlayerHandEvaluations(Map<HandEvaluation, List<Player>> handEvaluationListMap) {
        Map<Player, HandEvaluation> playerHandEvaluationMap = new HashMap<>();

        for (Map.Entry<HandEvaluation, List<Player>> entry : handEvaluationListMap.entrySet()) {
            HandEvaluation handEvaluation = entry.getKey();
            for (Player player : entry.getValue()) {
                playerHandEvaluationMap.put(player, handEvaluation);
            }
        }

        return playerHandEvaluationMap;
    }


    private static int calculateSharePerWinner(int totalPot, List<Player> winners) {
        if (winners.isEmpty()) {
            throw new IllegalArgumentException("Invalid winners");
        }
        int oddChips = totalPot % winners.size();
        totalPot -= oddChips;
        return totalPot / winners.size();
    }


    private static Map<HandEvaluation, List<Player>> calculatePlayerHandEvaluations(List<Player> players, List<Card> tableCards) {
        if (players == null || players.isEmpty()) {
            throw new IllegalArgumentException("Players list cannot be null or empty");
        }
        if (tableCards == null || tableCards.isEmpty()) {
            throw new IllegalArgumentException("Table cards list cannot be null or empty");
        }

        Map<HandEvaluation, List<Player>> handEvaluationMap = new HashMap<>();


        for (Player player : players) {
            if (!player.isFolded()) {
                HandEvaluation handEvaluation = HandEvaluator.evaluateHand(player.getHand(), tableCards);

                // If key does not already exist, create a new list and add the list with the key
                handEvaluationMap
                        .computeIfAbsent(handEvaluation, k -> new ArrayList<>())
                        .add(player);
            }
        }

        if (handEvaluationMap.isEmpty()) {
            throw new IllegalStateException("Could not determine hand evaluations");
        }

        return handEvaluationMap;
    }


    private static List<Player> getWinningPlayers(Map<HandEvaluation, List<Player>> handEvaluationMap) {
        List<HandEvaluation> handEvaluations = new ArrayList<>(handEvaluationMap.keySet());

        List<HandEvaluation> winningHands = getBestHands(handEvaluations);
        List<Player> winners = new ArrayList<>();

        for (HandEvaluation winningHand : winningHands) {
            winners.addAll(handEvaluationMap.get(winningHand));
        }

        return winners;
    }

    private static List<HandEvaluation> getBestHands(List<HandEvaluation> handEvaluations) {
        validateHandEvaluations(handEvaluations);

        HandEvaluation bestHand = Collections.max(handEvaluations);

        return getMatchingBestHands(handEvaluations, bestHand);
    }


    private static List<HandEvaluation> getMatchingBestHands(List<HandEvaluation> handEvaluations, HandEvaluation bestHand) {
        return handEvaluations.stream()
                .filter(hand -> hand.compareTo(bestHand) == 0)
                .collect(Collectors.toList());
    }

    private static void validateHandEvaluations(List<HandEvaluation> handEvaluations) {
        if (handEvaluations == null || handEvaluations.isEmpty()) {
            throw new IllegalArgumentException("Hand evaluations cannot be empty");
        }
    }
}
