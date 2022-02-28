public class View {
    // generates string for each card in hand
	public void display(Card card)
	{
		
		if (card.rank == 2)
		{
			System.out.print("Two of ");
		}
		if (card.rank == 3)
		{
			System.out.print("Three of ");
		}
		if (card.rank == 4)
		{
			System.out.print("Four of ");
		}
		if (card.rank == 5)
		{
			System.out.print("Five of ");
		}
		if (card.rank == 6)
		{
			System.out.print("Six of ");
		}
		if (card.rank == 7)
		{
			System.out.print("Seven of ");
		}
		if (card.rank == 8)
		{
			System.out.print("Eight of ");
		}
		if (card.rank == 9)
		{
			System.out.print("Nine of ");
		}
		if (card.rank == 10)
		{
			System.out.print("Ten of ");
		}
		if (card.rank == 11)
		{
			System.out.print("Jack of ");
		}
		if (card.rank == 12)
		{
			System.out.print("Queen of ");
		}
		if (card.rank == 13)
		{
			System.out.print("King of ");
		}
		if (card.rank == 14){
			System.out.print("Ace of ");
		}
		if (card.suit == 1)
		{
			System.out.print("Spades");
			System.out.println();
		}
		if (card.suit == 2)
		{
			System.out.print("Hearts");
			System.out.println();
		}
		if (card.suit == 3)
		{
			System.out.print("Diamonds");
			System.out.println();
		}
		if (card.suit == 4)
		{
			System.out.print("Clubs");
			System.out.println();
		}

	}
}
