package enumeration;

/**
 * Enum representing the hand rankings in Texas Hold'em.
 * Each rank is assigned a value for comparison purposes.
 * The higher the rankValue the stronger the hand.
 */
public enum HandRank implements Comparable<HandRank> {
    /**
     * The highest card in the hand
     */
    HIGH_CARD(1),

    /**
     * Two cards of the same rank
     */
    ONE_PAIR(2),

    /**
     * Two different pairs
     */
    TWO_PAIR(3),

    /**
     * Three cards of the same rank
     */
    THREE_OF_A_KIND(4),

    /**
     * Five consecutive cards of different suits
     */
    STRAIGHT(5),

    /**
     * Five cards of the same suit
     */
    FLUSH(6),

    /**
     * Three of a kind and a pair
     */
    FULL_HOUSE(7),

    /**
     * Four cards of the same rank
     */
    FOUR_OF_A_KIND(8),

    /**
     * Five consecutive cards of the same suit
     */
    STRAIGHT_FLUSH(9),

    /**
     * A royal flush is a hand of A, K, Q, J, 10, where all the cards are of the same suit
     */
    ROYAL_FLUSH(10);

    private final int rankValue;

    HandRank(int value) {
        this.rankValue = value;
    }

    public int getRankValue() {
        return rankValue;
    }

    /**
     * Compares this hand rank with another hand rank based on their values.
     *
     * @param other the other hand rank to compare with
     * @return a negative integer, zero, or a positive integer as this hand rank is less than, equal to, or greater than the specified hand rank
     */
    public int compareRank(HandRank other) {
        return Integer.compare(this.rankValue, other.rankValue);
    }

    @Override
    public String toString() {
        return name().replace('_', ' ').toLowerCase();
    }
}
