package com.poker.domain.player;

// This class avoids creating multiple instances of the same card across multiple decks
public class CardFactory {
    private static final Card[][] CARD_CACHE = new Card[Card.Suit.values().length][Card.Rank.values().length];

    static {
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values()) {
                CARD_CACHE[suit.ordinal()][rank.ordinal()] = new Card(suit, rank);
            }
        }
    }

    public static Card getCard(Card.Suit suit, Card.Rank rank) {
        return CARD_CACHE[suit.ordinal()][rank.ordinal()];
    }
}
