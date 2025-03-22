package util;

import com.poker.domain.player.Card;
import com.poker.domain.player.Card.Rank;
import com.poker.domain.player.Card.Suit;
import com.poker.domain.player.HandEvaluation;
import com.poker.util.HandEvaluator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.poker.enumeration.HandRank.*;
import static com.poker.domain.player.Card.Rank.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


// This class generates a large number of different combinations of cards for each poker hand ranked between
// Royal Flush and (Regular) Flush. The reason for this is that the algorithms needed to calculate hands below a
// regular flush are straightforward and do not require extensive testing.
@Disabled
public class HandEvaluatorGeneratedTest {

    private static final Set<Card.Rank> ROYAL_RANKS = Set.of(TEN, JACK, QUEEN, KING, ACE);
    private static final List<Suit> SUITS = List.of(Suit.values());
    private static final List<Rank> RANKS = List.of(Rank.values());
    private static final List<Card> ALL_CARDS = createAllCards();

    private static List<Card> createAllCards() {
        List<Card> allCards = new ArrayList<>();
        for (Suit suit : SUITS) {
            for (Rank rank : RANKS) {
                allCards.add(new Card(suit, rank));
            }
        }
        return allCards;
    }

    @BeforeEach
    public void setUp() {
        HandEvaluatorTestUtil.resetSeed();
    }


    @ParameterizedTest
    @MethodSource("provideRoyalFlushHands")
    public void testRoyalFlush(List<Card> playerCards, List<Card> tableCards) {
        HandEvaluation evaluation = HandEvaluator.evaluateHand(playerCards, tableCards);
        assertEquals(ROYAL_FLUSH, evaluation.getHandRank());
    }

    private static Stream<Object[]> provideRoyalFlushHands() {
        List<Object[]> testCases = new ArrayList<>();
        for (Card.Suit suit : SUITS) {
            List<Card> royalFlush = HandEvaluatorTestUtil.createRoyalFlush(suit);
            testCases.addAll(generateTestCases(royalFlush));
        }
        return testCases.stream();
    }

    @ParameterizedTest
    @MethodSource("provideStraightFlushHands")
    public void testStraightFlush(List<Card> playerCards, List<Card> tableCards) {
        HandEvaluation evaluation = HandEvaluator.evaluateHand(playerCards, tableCards);
        assertEquals(STRAIGHT_FLUSH, evaluation.getHandRank());
    }

    private static Stream<Object[]> provideStraightFlushHands() {
        List<Object[]> testCases = new ArrayList<>();
        for (Card.Suit suit : SUITS) {
            for (int i = 0; i <= RANKS.size() - 5; i++) {
                if (HandEvaluatorTestUtil.isRoyalFlushRange(i)) continue;

                List<Card> straightFlush = HandEvaluatorTestUtil.createStraightFlush(suit, i);
                testCases.addAll(generateTestCases(straightFlush));
            }
        }
        return testCases.stream();
    }



    @ParameterizedTest
    @MethodSource("provideFourOfAKindHands")
    public void testFourOfAKind(List<Card> playerCards, List<Card> tableCards) {
        HandEvaluation evaluation = HandEvaluator.evaluateHand(playerCards, tableCards);
        assertEquals(FOUR_OF_A_KIND, evaluation.getHandRank());
    }

    private static Stream<Object[]> provideFourOfAKindHands() {
        List<Object[]> testCases = new ArrayList<>();
        for (Card.Rank rank : RANKS) {
            if (ROYAL_RANKS.contains(rank)) continue;

            List<Card> fourOfAKind = HandEvaluatorTestUtil.createFourOfAKind(rank);
            testCases.addAll(generateTestCases(fourOfAKind));
        }
        return testCases.stream();
    }


    @ParameterizedTest
    @MethodSource("provideFullHouseHands")
    public void testFullHouse(List<Card> playerCards, List<Card> tableCards) {
        HandEvaluation evaluation = HandEvaluator.evaluateHand(playerCards, tableCards);
        assertEquals(FULL_HOUSE, evaluation.getHandRank());
    }

    private static Stream<Object[]> provideFullHouseHands() {
        List<Object[]> testCases = new ArrayList<>();

        for (Card.Rank threeOfAKindRank : RANKS) {
            for (Card.Rank pairRank : RANKS) {
                if (threeOfAKindRank == pairRank) continue;

                List<Card> threeOfAKind = SUITS.stream()
                        .limit(3)
                        .map(suit -> new Card(suit, threeOfAKindRank))
                        .toList();

                List<Card> pair = SUITS.stream()
                        .limit(2)
                        .map(suit -> new Card(suit, pairRank))
                        .toList();

                List<Card> fullHouse = new ArrayList<>(threeOfAKind);
                fullHouse.addAll(pair);

                // Generate all combinations of player cards and table cards
                for (int i = 0; i < fullHouse.size(); i++) {
                    for (int j = i + 1; j < fullHouse.size(); j++) {
                        List<Card> playerCards = Arrays.asList(fullHouse.get(i), fullHouse.get(j));
                        List<Card> tableCards = new ArrayList<>(fullHouse);
                        tableCards.removeAll(playerCards);

                        // Avoid cards that would complete a royal flush or straight flush
                        List<Card> avoidCards = new ArrayList<>(fullHouse);
                        avoidCards.addAll(HandEvaluatorTestUtil.createFourOfAKind(threeOfAKindRank));
                        avoidCards.addAll(HandEvaluatorTestUtil.createFourOfAKind(pairRank));

                        // Add more random cards to complete 5 table cards
                        while (tableCards.size() < 5) {
                            avoidCards.addAll(HandEvaluatorTestUtil.createStraightFlushCompleters(playerCards, tableCards));
                            Optional<Card> randomCard = HandEvaluatorTestUtil.generateRandomCard(avoidCards);
                            if (randomCard.isEmpty()) {
                                break;
                            }
                            tableCards.add(randomCard.get());
                        }

                        if (tableCards.size() == 5) {
                            testCases.add(new Object[]{playerCards, tableCards});
                        }
                    }
                }
            }
        }

        return testCases.stream();
    }
    @ParameterizedTest
    @MethodSource("provideFlushHands")
    public void testFlush(List<Card> playerCards, List<Card> tableCards) {
        HandEvaluation evaluation = HandEvaluator.evaluateHand(playerCards, tableCards);
        assertEquals(FLUSH, evaluation.getHandRank());
    }
    private static Stream<Object[]> provideFlushHands() {
        List<Object[]> testCases = new ArrayList<>();

        for (Card.Suit suit : SUITS) {
            List<Card> flushCards = ALL_CARDS.stream()
                    .filter(card -> card.suit() == suit)
                    .collect(Collectors.toList());

            // Generate all combinations of 5 cards of the same suit
            for (List<Card> flush : HandEvaluatorTestUtil.generateFlushCombinations(flushCards)) {
                // Exclude Straight Flush
                if (HandEvaluatorTestUtil.isStraightFlush(flush)) {
                    continue;
                }

                // Generate all combinations of player cards and table cards
                for (int j = 0; j < flush.size(); j++) {
                    for (int k = j + 1; k < flush.size(); k++) {
                        List<Card> playerCards = Arrays.asList(flush.get(j), flush.get(k));
                        List<Card> tableCards = new ArrayList<>(flush);
                        tableCards.removeAll(playerCards);

                        List<Card> avoidCards = new ArrayList<>(flush);

                        while (tableCards.size() < 5) {
                            // Avoid cards that would complete a straight flush
                            avoidCards.addAll(HandEvaluatorTestUtil.createStraightFlushCompleters(playerCards, tableCards));
                            Optional<Card> randomCard = HandEvaluatorTestUtil.generateRandomCard(avoidCards);
                            if (randomCard.isEmpty()) {
                                break;
                            }
                            tableCards.add(randomCard.get());
                        }

                        if (tableCards.size() == 5) {
                            testCases.add(new Object[]{playerCards, tableCards});
                        }
                    }
                }
            }
        }

        return testCases.stream();
    }




    private static List<Object[]> generateTestCases(List<Card> hand) {
        List<Object[]> testCases = new ArrayList<>();
        for (int i = 0; i < hand.size(); i++) {
            for (int j = i + 1; j < hand.size(); j++) {
                List<Card> playerCards = Arrays.asList(hand.get(i), hand.get(j));
                List<Card> tableCards = new ArrayList<>(hand);
                tableCards.removeAll(playerCards);

                List<Card> avoidCards = new ArrayList<>(hand);
                avoidCards.addAll(HandEvaluatorTestUtil.createRoyalFlush(SUITS));
                while (tableCards.size() < 5) {
                    tableCards.add(HandEvaluatorTestUtil.generateRandomCard(avoidCards).get()); // todo
                }

                testCases.add(new Object[]{playerCards, tableCards});
            }
        }
        return testCases;
    }




}
