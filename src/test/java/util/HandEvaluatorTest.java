package util;

import model.player.Card;
import model.player.Card.Rank;
import model.player.Card.Suit;
import model.player.HandEvaluation;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static enumeration.HandRank.*;
import static org.junit.jupiter.api.Assertions.*;

class HandEvaluatorTest {
    @Test
    public void testRoyalFlush() {
        List<Card> playerCards = Arrays.asList(
                new Card(Suit.SPADES, Rank.ACE),
                new Card(Suit.SPADES, Rank.KING)
        );
        List<Card> tableCards = Arrays.asList(
                new Card(Suit.SPADES, Rank.QUEEN),
                new Card(Suit.SPADES, Rank.JACK),
                new Card(Suit.SPADES, Rank.TEN),
                new Card(Suit.HEARTS, Rank.TWO),
                new Card(Suit.CLUBS, Rank.THREE)
        );

        HandEvaluation evaluation = HandEvaluator.evaluateHand(playerCards, tableCards);
        assertEquals(ROYAL_FLUSH, evaluation.getHandRank());
    }

    @Test
    public void testStraightFlush() {
        List<Card> playerCards = Arrays.asList(
                new Card(Suit.HEARTS, Rank.NINE),
                new Card(Suit.HEARTS, Rank.EIGHT)
        );
        List<Card> tableCards = Arrays.asList(
                new Card(Suit.HEARTS, Rank.SEVEN),
                new Card(Suit.HEARTS, Rank.SIX),
                new Card(Suit.HEARTS, Rank.FIVE),
                new Card(Suit.CLUBS, Rank.TWO),
                new Card(Suit.DIAMONDS, Rank.THREE)
        );

        HandEvaluation evaluation = HandEvaluator.evaluateHand(playerCards, tableCards);
        assertEquals(STRAIGHT_FLUSH, evaluation.getHandRank());
    }

    @Test
    public void testFourOfAKind() {
        List<Card> playerCards = Arrays.asList(
                new Card(Suit.CLUBS, Rank.NINE),
                new Card(Suit.HEARTS, Rank.NINE)
        );
        List<Card> tableCards = Arrays.asList(
                new Card(Suit.SPADES, Rank.NINE),
                new Card(Suit.DIAMONDS, Rank.NINE),
                new Card(Suit.HEARTS, Rank.KING),
                new Card(Suit.HEARTS, Rank.QUEEN),
                new Card(Suit.HEARTS, Rank.JACK)
        );

        HandEvaluation evaluation = HandEvaluator.evaluateHand(playerCards, tableCards);
        assertEquals(FOUR_OF_A_KIND, evaluation.getHandRank());
    }

    @Test
    public void testFullHouse() {
        List<Card> playerCards = Arrays.asList(
                new Card(Suit.CLUBS, Rank.KING),
                new Card(Suit.HEARTS, Rank.KING)
        );
        List<Card> tableCards = Arrays.asList(
                new Card(Suit.SPADES, Rank.KING),
                new Card(Suit.DIAMONDS, Rank.TWO),
                new Card(Suit.HEARTS, Rank.TWO),
                new Card(Suit.CLUBS, Rank.THREE),
                new Card(Suit.CLUBS, Rank.FOUR)
        );

        HandEvaluation evaluation = HandEvaluator.evaluateHand(playerCards, tableCards);
        assertEquals(FULL_HOUSE, evaluation.getHandRank());
    }

    @Test
    public void testFlush() {
        List<Card> playerCards = Arrays.asList(
                new Card(Suit.CLUBS, Rank.ACE),
                new Card(Suit.CLUBS, Rank.TEN)
        );
        List<Card> tableCards = Arrays.asList(
                new Card(Suit.CLUBS, Rank.SEVEN),
                new Card(Suit.CLUBS, Rank.SIX),
                new Card(Suit.CLUBS, Rank.FIVE),
                new Card(Suit.HEARTS, Rank.KING),
                new Card(Suit.SPADES, Rank.QUEEN)
        );

        HandEvaluation evaluation = HandEvaluator.evaluateHand(playerCards, tableCards);
        assertEquals(FLUSH, evaluation.getHandRank());
    }

    @Test
    public void testStraight() {
        List<Card> playerCards = Arrays.asList(
                new Card(Suit.CLUBS, Rank.FOUR),
                new Card(Suit.DIAMONDS, Rank.FIVE)
        );
        List<Card> tableCards = Arrays.asList(
                new Card(Suit.HEARTS, Rank.SIX),
                new Card(Suit.SPADES, Rank.SEVEN),
                new Card(Suit.CLUBS, Rank.EIGHT),
                new Card(Suit.HEARTS, Rank.KING),
                new Card(Suit.SPADES, Rank.QUEEN)
        );

        HandEvaluation evaluation = HandEvaluator.evaluateHand(playerCards, tableCards);
        assertEquals(STRAIGHT, evaluation.getHandRank());
    }

    @Test
    public void testThreeOfAKind() {
        List<Card> playerCards = Arrays.asList(
                new Card(Suit.CLUBS, Rank.THREE),
                new Card(Suit.DIAMONDS, Rank.THREE)
        );
        List<Card> tableCards = Arrays.asList(
                new Card(Suit.HEARTS, Rank.THREE),
                new Card(Suit.SPADES, Rank.SEVEN),
                new Card(Suit.CLUBS, Rank.KING),
                new Card(Suit.HEARTS, Rank.TWO),
                new Card(Suit.SPADES, Rank.QUEEN)
        );

        HandEvaluation evaluation = HandEvaluator.evaluateHand(playerCards, tableCards);
        assertEquals(THREE_OF_A_KIND, evaluation.getHandRank());
    }

    @Test
    public void testTwoPair() {
        List<Card> playerCards = Arrays.asList(
                new Card(Suit.CLUBS, Rank.FOUR),
                new Card(Suit.DIAMONDS, Rank.FOUR)
        );
        List<Card> tableCards = Arrays.asList(
                new Card(Suit.HEARTS, Rank.SIX),
                new Card(Suit.SPADES, Rank.SIX),
                new Card(Suit.CLUBS, Rank.KING),
                new Card(Suit.HEARTS, Rank.TEN),
                new Card(Suit.SPADES, Rank.JACK)
        );

        HandEvaluation evaluation = HandEvaluator.evaluateHand(playerCards, tableCards);
        assertEquals(TWO_PAIR, evaluation.getHandRank());
    }

    @Test
    public void testOnePair() {
        List<Card> playerCards = Arrays.asList(
                new Card(Suit.CLUBS, Rank.SEVEN),
                new Card(Suit.SPADES, Rank.SEVEN)
        );
        List<Card> tableCards = Arrays.asList(
                new Card(Suit.CLUBS, Rank.THREE),
                new Card(Suit.DIAMONDS, Rank.FOUR),
                new Card(Suit.SPADES, Rank.KING),
                new Card(Suit.CLUBS, Rank.TWO),
                new Card(Suit.HEARTS, Rank.FIVE)
        );

        HandEvaluation evaluation = HandEvaluator.evaluateHand(playerCards, tableCards);
        assertEquals(ONE_PAIR, evaluation.getHandRank());
    }

    @Test
    public void testHighCard() {
        List<Card> playerCards = Arrays.asList(
                new Card(Suit.CLUBS, Rank.ACE),
                new Card(Suit.DIAMONDS, Rank.KING)
        );
        List<Card> tableCards = Arrays.asList(
                new Card(Suit.HEARTS, Rank.NINE),
                new Card(Suit.SPADES, Rank.EIGHT),
                new Card(Suit.CLUBS, Rank.SEVEN),
                new Card(Suit.HEARTS, Rank.TWO),
                new Card(Suit.SPADES, Rank.THREE)
        );

        HandEvaluation evaluation = HandEvaluator.evaluateHand(playerCards, tableCards);
        assertEquals(HIGH_CARD, evaluation.getHandRank());
    }
}