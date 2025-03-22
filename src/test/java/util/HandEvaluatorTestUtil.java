package util;

import com.poker.domain.player.Card;

import java.util.*;
import java.util.stream.Collectors;

import static com.poker.domain.player.Card.Rank.*;

public class HandEvaluatorTestUtil {

    private static final Set<Card.Rank> ROYAL_RANKS = Set.of(TEN, JACK, QUEEN, KING, ACE);
    private static final List<Card.Suit> SUITS = List.of(Card.Suit.values());
    private static final List<Card.Rank> RANKS = List.of(Card.Rank.values());
    private static final List<Card> ALL_CARDS = createAllCards();
    private static final Random RANDOM = new Random(12345); // Fixed seed for reproducibility


    private static List<Card> createAllCards() {
        List<Card> allCards = new ArrayList<>();
        for (Card.Suit suit : SUITS) {
            for (Card.Rank rank : RANKS) {
                allCards.add(new Card(suit, rank));
            }
        }
        return allCards;
    }

    public static void resetSeed() {
        RANDOM.setSeed(12345);
    }

    public static List<Card> createFourOfAKind(Card.Rank rank) {
        return SUITS.stream()
                .map(suit -> new Card(suit, rank))
                .toList();
    }
    public static boolean isRoyalFlushRange(int i) {
        return RANKS.get(i) == TEN && RANKS.get(i + 4) == ACE;
    }
    public static List<List<Card>> generateFlushCombinations(List<Card> cards) {
        List<List<Card>> combinations = new ArrayList<>();
        generateCombinations(combinations, new ArrayList<>(), cards, 5, 0);
        return combinations;
    }
    public static void generateCombinations(List<List<Card>> combinations, List<Card> currentCombination, List<Card> cards, int combinationSize, int start) {
        if (currentCombination.size() == combinationSize) {
            combinations.add(new ArrayList<>(currentCombination));
            return;
        }

        for (int i = start; i < cards.size(); i++) {
            currentCombination.add(cards.get(i));
            generateCombinations(combinations, currentCombination, cards, combinationSize, i + 1);
            currentCombination.remove(currentCombination.size() - 1);
        }
    }

    public static List<Card> createStraightFlushCompleters(List<Card> playerCards, List<Card> tableCards) {
        List<Card> straightFlushCompleters = new ArrayList<>();
        for (Card.Suit suit : SUITS) {
            for (int i = 0; i <= RANKS.size() - 5; i++) {
                List<Card> straightFlush = createStraightFlush(suit, i);
                for (Card card : straightFlush) {
                    if (!playerCards.contains(card) && !tableCards.contains(card)) {
                        List<Card> potentialHand = new ArrayList<>(playerCards);
                        potentialHand.addAll(tableCards);
                        potentialHand.add(card);
                        if (isStraightFlush(potentialHand)) {
                            straightFlushCompleters.add(card);
                        }
                    }
                }
            }
        }
        return straightFlushCompleters;
    }

    public static boolean isStraightFlush(List<Card> cards) {
        Map<Card.Suit, List<Card>> suitToCardsMap = cards.stream()
                .collect(Collectors.groupingBy(Card::suit));

        for (List<Card> suitedCards : suitToCardsMap.values()) {
            if (suitedCards.size() >= 5) {
                List<Integer> ranks = suitedCards.stream()
                        .map(card -> card.rank().getValue())
                        .distinct()
                        .sorted()
                        .toList();

                if (isConsecutive(ranks)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isConsecutive(List<Integer> ranks) {
        for (int i = 0; i <= ranks.size() - 5; i++) {
            if (ranks.get(i + 4) - ranks.get(i) == 4) {
                return true;
            }
        }
        // Check for ace low straight flush (5 high)
        return ranks.contains(14) && ranks.containsAll(Arrays.asList(2, 3, 4, 5));
    }

    public static List<Card> createStraightFlush(Card.Suit suit, int startIndex) {
        return Arrays.asList(
                new Card(suit, RANKS.get(startIndex)),
                new Card(suit, RANKS.get(startIndex + 1)),
                new Card(suit, RANKS.get(startIndex + 2)),
                new Card(suit, RANKS.get(startIndex + 3)),
                new Card(suit, RANKS.get(startIndex + 4))
        );
    }

    public static Optional<Card> generateRandomCard(List<Card> avoidCards) {
        List<Card> availableCards = new ArrayList<>(ALL_CARDS);
        availableCards.removeAll(avoidCards);

        if (availableCards.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(availableCards.get(RANDOM.nextInt(availableCards.size())));
    }


    public static List<Card> createRoyalFlush(Card.Suit suit) {
        return ROYAL_RANKS.stream()
                .map(rank -> new Card(suit, rank))
                .toList();
    }

    public static List<Card> createRoyalFlush(List<Card.Suit> suits) {
        List<Card> royalFlush = new ArrayList<>();
        for (Card.Suit suit : suits) {
            royalFlush.addAll(createRoyalFlush(suit));
        }
        return royalFlush;
    }

}
