package view;

import model.Card;

public class View {
    // generates string for each card in hand
	public void display(Card card)
	{
		
		if (card.getRank() == 2)
		{
			System.out.print("Two of ");
		}
		if (card.getRank() == 3)
		{
			System.out.print("Three of ");
		}
		if (card.getRank() == 4)
		{
			System.out.print("Four of ");
		}
		if (card.getRank() == 5)
		{
			System.out.print("Five of ");
		}
		if (card.getRank() == 6)
		{
			System.out.print("Six of ");
		}
		if (card.getRank() == 7)
		{
			System.out.print("Seven of ");
		}
		if (card.getRank() == 8)
		{
			System.out.print("Eight of ");
		}
		if (card.getRank() == 9)
		{
			System.out.print("Nine of ");
		}
		if (card.getRank() == 10)
		{
			System.out.print("Ten of ");
		}
		if (card.getRank() == 11)
		{
			System.out.print("Jack of ");
		}
		if (card.getRank() == 12)
		{
			System.out.print("Queen of ");
		}
		if (card.getRank() == 13)
		{
			System.out.print("King of ");
		}
		if (card.getRank() == 14){
			System.out.print("Ace of ");
		}
		if (card.getSuit() == 1)
		{
			System.out.print("Spades");
			System.out.println();
		}
		if (card.getSuit() == 2)
		{
			System.out.print("Hearts");
			System.out.println();
		}
		if (card.getSuit() == 3)
		{
			System.out.print("Diamonds");
			System.out.println();
		}
		if (card.getSuit() == 4)
		{
			System.out.print("Clubs");
			System.out.println();
		}

	}
}
