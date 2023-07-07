package model;

import java.io.Serializable;

public class Card implements Serializable, Comparable<Card>
{
  // I.V.s are suit and rank
	private int suit;
	private int rank;

	public Card() {

	}

	public Card(int suit, int rank) {
		this.suit = suit;
		this.rank = rank;
	}

	public int getSuit() {
		return suit;
	}

	public int getRank() {
		return rank;
	}
	
	public boolean greater_than(Card o) 
	{
	     if (this.rank >= o.rank)
	            return true;
	     else
	           return false;
	}

	@Override
	public String toString() {
		String suitName = "";
		switch (suit) {
			case 1 -> suitName = "clubs";
			case 2 -> suitName = "diamonds";
			case 3 -> suitName = "hearts";
			case 4 -> suitName = "spades";
		}

		String rankName = "";
		switch (rank) {
			case 14 -> rankName = "Ace";
			case 11 -> rankName = "Jack";
			case 12 -> rankName = "Queen";
			case 13 -> rankName = "King";
			default -> rankName = Integer.toString(rank);
		}

		return rankName + " of " + suitName;
	}

	@Override
	public int compareTo(Card card) {
		return Integer.compare(this.rank, card.rank);
	}
}