package model.game.components;

import enumeration.RoundState;
import model.player.Card;
import model.player.Deck;
import model.player.Player;

import java.util.ArrayList;
import java.util.List;

public class DeckManager {
    private final Deck deck;
    private List<Card> tableCards;
    private final int MAX_TABLE_CARDS;

    public DeckManager(int MAX_TABLE_CARDS) {
        this.deck = new Deck();
        this.tableCards = new ArrayList<>();
        this.MAX_TABLE_CARDS = MAX_TABLE_CARDS;
    }

    public void shuffleDeck() {
        deck.shuffleDrawPile();
    }

    public void assignCardsToPlayers(List<Player> players) {
        for (Player player : players) {
            player.addToHand(deck.drawCard(2));
        }
    }

    public void updateTableCards(RoundState roundState) {
        switch (roundState) {
            case FLOP -> addToTableCards(3);
            case TURN, RIVER -> addToTableCards(1);
            default -> {}
        }
    }

    private void addToTableCards(int numOfCards) {
        int availableSlots = MAX_TABLE_CARDS - tableCards.size();
        if (numOfCards > availableSlots) {
            throw new IllegalStateException("Cannot add " + numOfCards + " cards to the table. Table already has " + tableCards.size() + " cards");
        }
        tableCards.addAll(deck.drawCard(numOfCards));
    }

    public void resetDeck() {
        deck.reset();
        tableCards.clear();
    }

    public List<Card> getTableCards() {
        return new ArrayList<>(tableCards);
    }

    public void setTableCards(List<Card> newCards) {
        if (tableCards.size() > MAX_TABLE_CARDS) {
            throw new IllegalArgumentException("Too many table cards");
        }
        this.tableCards = new ArrayList<>(newCards);
    }
}
