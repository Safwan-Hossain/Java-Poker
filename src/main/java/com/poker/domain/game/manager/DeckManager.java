package com.poker.domain.game.manager;

import com.poker.enumeration.RoundState;
import com.poker.domain.player.Card;
import com.poker.domain.player.Deck;
import com.poker.domain.player.Player;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


// Consider adding a settings variable to change game rules for different game types
@NoArgsConstructor
public class DeckManager {
    // Game rules
    private static final int MAX_TABLE_CARDS = 5;
    private static final int MAX_HAND_SIZE = 2;
    private static final int FLOP_CARD_COUNT = 3;
    private static final int TURN_RIVER_CARD_COUNT = 1;


    private final Deck deck = new Deck();
    private final List<Card> tableCards = new ArrayList<>();


    /**
     * Assigns a fixed number of cards to each player at the start of the game
     */
    public void assignCardsToPlayers(List<Player> players) {
        for (Player player : players) {
            if (!player.getHand().isEmpty()) {
                throw new IllegalArgumentException("Tried to assign cards to player who already have cards");
            }
            player.addToHand(deck.drawCard(MAX_HAND_SIZE));
        }
    }

    /**
     * Updates table cards based on the current round state
     * FLOP → 3 cards, TURN and RIVER → 1 card each
     */
    public void updateTableCards(RoundState roundState) {
        switch (roundState) {
            case FLOP -> addToTableCards(FLOP_CARD_COUNT);
            case TURN, RIVER -> addToTableCards(TURN_RIVER_CARD_COUNT);
            default -> {} // No extra cards needed for other states
        }
    }

    /**
     * Draws and adds the specified number of cards to the table.
     * Throws an exception if the table reaches its maximum card limit.
     */
    private void addToTableCards(int numOfCards) {
        int availableSlots = MAX_TABLE_CARDS - tableCards.size();
        if (numOfCards > availableSlots) {
            throw new IllegalStateException("Cannot add " + numOfCards +
                    " cards to the table. Table already has " + tableCards.size() + " cards");
        }
        tableCards.addAll(deck.drawCard(numOfCards));
    }

    /**
     * Resets the deck and clears the table cards
     * Should be called when starting a new round
     */
    public void resetDeck() {
        deck.reset();
        tableCards.clear();
    }

    /**
     * Returns a copy of the table cards to prevent external changes
     */
    public List<Card> getTableCards() {
        return new ArrayList<>(tableCards);
    }

}
