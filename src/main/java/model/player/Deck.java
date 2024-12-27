package model.player;

import model.player.Card.Rank;
import model.player.Card.Suit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Simulates a deck of cards.
 * This class stores a deck of cards inside an array. It also utilizes a stack pointer to keep track of which cards have
 * been drawn. The stack pointer starts at 0 and increments by 1 each time a card is drawn. The cards that are stored in
 * the array between the indices 0 and (stackPointer - 1) are cards that have already been drawn. The cards that are stored
 * between the indices stackPointer and the last index (DECK_SIZE - 1) are cards that have not been drawn.
 */
public class Deck implements Serializable {
	// Max deck size. Standard is 52.
	private static final int DECK_SIZE = 52;

	// Keeps track of which cards have been drawn
	private int stackPointer;
	private final Card[] deck;

	// Random is more efficient and less biased than Math.random()
	private final Random randomGenerator;

	public Deck() {
		stackPointer = 0;
		deck = new Card[DECK_SIZE];
		randomGenerator = new Random();
		fillDeck();
	}

	/**
	 * Fills the deck of cards with new cards
	 */
	private void fillDeck() {
		int counter = 0;
		for (Suit suit : Suit.values()) {
			for (Rank rank : Rank.values()) {
				deck[counter++] = new Card(suit, rank);
			}
		}
	}

	/**
	 * Shuffles the draw pile (the remaining deck) using the Fisher-Yates algorithm.
	 */
	public synchronized void shuffleDrawPile() {
		for (int i = stackPointer; i < DECK_SIZE; i++) {
			int randomIndex = i + randomGenerator.nextInt(DECK_SIZE - i);
			Card temp = deck[i];
			deck[i] = deck[randomIndex];
			deck[randomIndex] = temp;
		}
	}

	/**
	 * Takes back all the cards and shuffles them
	 */
	public synchronized void reset() {
		stackPointer = 0;
		shuffleDrawPile();
	}

	/**
	 * Draws a specified number of cards and returns them in a list
	 * @throws IllegalArgumentException thrown if the requested number of cards to draw is larger than
	 * the number of cards left in the draw pile.
	 */
	public synchronized List<Card> drawCard(int numOfCardsToDraw) {
		int cardsLeft = numOfCardsLeft();
		if (numOfCardsToDraw > cardsLeft) {
			throw new IllegalArgumentException("The deck does not have enough cards. Tried to draw "
					+ numOfCardsToDraw + " cards, but only " + cardsLeft + " are left.");
		}

		List<Card> newCards = new ArrayList<>(numOfCardsToDraw);
		for (int i = 0; i < numOfCardsToDraw; i++) {
			newCards.add(drawSingleCard());
		}
		return newCards;
	}

	private synchronized Card drawSingleCard() {
		if (isEmpty()) {
			throw new IllegalStateException("The deck is empty. No cards to draw.");
		}
		return deck[stackPointer++];
	}

	private int numOfCardsLeft() {
		return DECK_SIZE - stackPointer;
	}

	public synchronized boolean isEmpty() {
		return stackPointer >= DECK_SIZE;
	}

	/**
	 * Prints the deck for debugging purposes.
	 */
	public synchronized void printDeck() {
		for (Card card : deck) {
			System.out.println(card);
		}
	}
}
