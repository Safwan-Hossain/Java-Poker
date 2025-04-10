package com.poker.domain.player;

import com.poker.enumeration.HandRank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the evaluation of a hand in Texas Hold'em.
 * Implements Comparable to compare hand evaluations.
 */
@ToString
@EqualsAndHashCode
public final class HandEvaluation implements Comparable<HandEvaluation> {
    @Getter
    private final HandRank handRank;
    private final List<Card> cards;

    public HandEvaluation(HandRank handRank, List<Card> cards) {
        if (handRank == null || cards == null) {
            throw new IllegalArgumentException("Invalid HandEvaluation, parameter(s) are null");
        }
        this.handRank = handRank;
        this.cards = new ArrayList<>(cards);
        this.cards.sort(Collections.reverseOrder());
    }

    /**
     * Returns an unmodifiable list of cards.
     *
     * @return the cards
     */
    public List<Card> getCards() {
        return Collections.unmodifiableList(cards);
    }


    @Override
    public int compareTo(HandEvaluation other) {
        // Step 1: Compare hand ranks using custom comparison method
        int rankComparison = handRank.compareRank(other.handRank);
        if (rankComparison != 0) {
            return rankComparison;
        }

        // Step 2: Compare individual cards in descending order
        return compareCards(cards, other.cards);
    }

    private int compareCards(List<Card> cards1, List<Card> cards2) {
        for (int i = 0; i < cards1.size(); i++) {
            int cardComparison = cards1.get(i).compareTo(cards2.get(i));
            if (cardComparison != 0) {
                return cardComparison;
            }
        }
        // If all cards are equal, return 0 indicating a tie
        return 0;
    }

}
