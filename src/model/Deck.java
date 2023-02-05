package model; /***********************
 *  Assignment 4
 *  Andreas Hadjigeorgiou
 *  ahh2131
 *  model.Deck class
 */

import model.Card;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class Deck implements Serializable
{
  
	private final int DECK_SIZE = 52;
	private final int SHUFFLE_EXCHANGES = 2000;
	public int stack_p = 0; //stack pointer 
	protected ArrayList<Card> flop = new ArrayList<Card>();

	Card[] deck = new Card[DECK_SIZE];
	Random r = new Random();
	
	// fill deck with cards
	public void fillDeck()  //as needed ----
	{
		int counter = 0;
		for (int suit = 1; suit <= 4; suit++)
		{
			for (int rank = 1; rank <= 13; rank++)
			{
				Card card = new Card(suit, rank);
				deck[counter] = card;
				counter++;
			}
		}
	}
	
	// shuffle deck
	public void shuffle()
	{
		for (int x = 0; x <= SHUFFLE_EXCHANGES; x++)
		{
			int number1 = r.nextInt(DECK_SIZE - stack_p) + stack_p; //gives us the ability to not shuffle the whole deck
			int number2 = r.nextInt(DECK_SIZE - stack_p) + stack_p;
			Card temp = deck[number1];
			deck[number1] = deck[number2];
			deck[number2] = temp;
		}
	}
	
	public void reset(){ //so that we don't have to remember to reset the pointer and flop when we wanna full-shuffle
		stack_p = 0;
		shuffle();
		flop = new ArrayList<Card>();
	}


	// draws x cards and returns them in an array
	public Card[] draw(int num) 
	{
		if (stack_p + num > DECK_SIZE)
		{
			num = DECK_SIZE - (stack_p + num);
			System.out.println("The deck is out of cards"); //might not be perfect for all game modes, but for now this should work (condition will never be met in holdem)
		}

		Card[] hand = new Card[num];
		for (int i = 0; i < num; i++)
		{
			hand[i] = deck[stack_p];
			stack_p++;
		}
		return hand;
	}

	//not useful for holdem but could be good for other game modes (future addition) - bin search for card, swap with card right under stack_p and stack_p--
	public void reinsert() 
	{

	}


	/*
	// deals cards for redraw
	public model.Card redeal()
	{
		model.Card nextCard = deck[restOfDeck];
		restOfDeck++;
		return nextCard;
	}

	//THESE DONT SEEM NECESSARY
	
	// refreshes deck position to 6 for next hand
	public void refreshDeckPosition()
	{
		restOfDeck = 6;
	}
	*/

}
