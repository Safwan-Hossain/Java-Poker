package util;

import model.player.Card;
import model.player.HandEvaluation;
import model.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Evaluates and determines the winner among multiple HandEvaluations in Texas Hold'em.
 */
public class RoundWinnerEvaluator {


    /**
     * Evaluates the hands of all players and determines the winner(s)
     *
     * @param players a list of Player objects representing the players in the round
     * @return a list of Player objects representing the winning players (handles ties)
     */
    public static List<Player> determineWinners(List<Player> players, List<Card> tableCards) {
        if (players == null || players.isEmpty()) {
            throw new IllegalArgumentException("Players list cannot be null or empty");
        }

        Map<HandEvaluation, List<Player>> handEvaluationMap = new HashMap<>();
        List<HandEvaluation> handEvaluations = new ArrayList<>();

        for (Player player : players) {
            if (!player.isFolded()) {
                HandEvaluation handEvaluation = HandEvaluator.evaluateHand(player.getHand(), tableCards);
                handEvaluations.add(handEvaluation);

                // If key does not already exist, create a new list and add the list with the key
                List<Player> playersWithSameHand = handEvaluationMap.computeIfAbsent(handEvaluation, k -> new ArrayList<>());
                playersWithSameHand.add(player);
            }
        }

        List<HandEvaluation> winningHands = determineWinners(handEvaluations);

        List<Player> winners = new ArrayList<>();
        for (HandEvaluation winningHand : winningHands) {
            winners.addAll(handEvaluationMap.get(winningHand));
        }

        return winners;
    }

    public static List<HandEvaluation> determineWinners(List<HandEvaluation> handEvaluations) {
        List<HandEvaluation> winningHands = new ArrayList<>();
        HandEvaluation bestHand = handEvaluations.get(0);

        for (HandEvaluation handEvaluation : handEvaluations) {
            int comparison = handEvaluation.compareTo(bestHand);
            if (comparison > 0) {
                bestHand = handEvaluation;
                winningHands.clear();
                winningHands.add(handEvaluation);
            } else if (comparison == 0) {
                winningHands.add(handEvaluation);
            }
        }

        return winningHands;
    }
}
