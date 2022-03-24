import java.io.Serializable;

/***********************
 *  Assignment 4
 *  Andreas Hadjigeorgiou
 *  ahh2131
 *  Card class
 */

public class Card implements Serializable
{
  // I.V.s are suit and rank
	public int suit;
	public int rank;
	
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
			case 1 -> rankName = "Ace";
			case 11 -> rankName = "Jack";
			case 12 -> rankName = "Queen";
			case 13 -> rankName = "King";
			default -> rankName = Integer.toString(rank);
		}

		return rankName + " of " + suitName;
	}
}
