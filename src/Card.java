/***********************
 *  Assignment 4
 *  Andreas Hadjigeorgiou
 *  ahh2131
 *  Card class
 */

public class Card
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
	

}
