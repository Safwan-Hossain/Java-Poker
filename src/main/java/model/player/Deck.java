package model.player;

import model.player.Card.Rank;
import model.player.Card.Suit;

import java.io.Serializable;
import java.util.Random;

/**
 * Simulates a deck of cards.
 * This class stores a deck of cards inside an array. It also utilizes a stack pointer to keep track of which cards have
 * been drawn. The stack pointer starts at 0 and increments by 1 each time a card is drawn. The cards that are stored in
 * the array between the indices 0 and (stackPointer - 1) are cards that have already been drawn. The cards that are stored
 * between the indices stackPointer and the last index (DECK_SIZE - 1) are cards that have not been drawn.
 * and the  (stackPointer - 1)
 */
public class Deck implements Serializable
{
//	Max deck size. Standard is 52.
	private final int DECK_SIZE = 52;

//	Keeps track of which cards have been drawn
	private int stackPointer;
	private final Card[] deck;

//	Random is more efficient and less biased than Math.random()
	private Random randomGenerator;

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
	public void reset() {
		stackPointer = 0;
		shuffleDrawPile();
	}
	/**
	 * Draws a specified number of cards and returns them in an array
	 * @throws IllegalArgumentException thrown if the requested number of cards to draw is larger than
	 * the number of cards left in the draw pile.
	 */
	public Card[] drawCard(int numOfCardsToDraw)
	{
		if (numOfCardsToDraw > numOfCardsLeft())
		{
			throw new IllegalArgumentException("The deck does not have enough cards. Game tried to draw too many cards");
		}

		Card[] newCards = new Card[numOfCardsToDraw];
		for (int i = 0; i < numOfCardsToDraw; i++) {
			newCards[i] = drawCard();
		}
		return newCards;
	}

	private synchronized Card drawCard() {
		if (isEmpty()) {
			throw new IllegalStateException("The deck is empty. No cards to draw.");
		}
		return deck[stackPointer++];
	}


	/** Generates a random index of a card from the draw pile.
	 * @implNote (DECK_SIZE - stackPointer) is the number of card left in the deck */
	private int generateRandomIndexFromDrawPile() {
		return randomGenerator.nextInt(DECK_SIZE - stackPointer) + stackPointer;
	}

	/** Calculates the number of cards left in the deck */
	private int numOfCardsLeft() {
		return DECK_SIZE - stackPointer;
	}

	public boolean isEmpty() {
		return stackPointer >= DECK_SIZE;
	}

	/**
	 * Prints the deck for debugging purposes.
	 */
	public void printDeck() {
		for (int i = 0; i < DECK_SIZE; i++) {
			System.out.println(deck[i]);
		}
	}

}
