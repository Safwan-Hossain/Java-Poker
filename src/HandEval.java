/***********************
 *  Assignment 4
 *  Andreas Hadjigeorgiou
 *  ahh2131
 *  Game class
 */

import java.util.ArrayList;


public class HandEval
{
	// TEMPORARY
	public static String getHandName(int[] score) {
		if (score[0] == 9 && score[1] == 14) {
			return "royal flush";
		}
		else if (score[0] == 8) {
			return "straight flush";
		}
		else if (score[0] == 7) {
			return "four of a kind";
		}
		else if (score[0] == 6) {
			return "full house";
		}
		else if (score[0] == 5) {
			return "flush";
		}
		else if (score[0] == 4) {
			return "straight";
		}
		else if (score[0] == 3) {
			return "triple";
		}
		else if (score[0] == 2) {
			return "two pairs";
		}
		else if (score[0] == 1) {
			return "pair";
		}
	return "high card";
	}
    /*
	private static ArrayList<Card> organizeCards(ArrayList<Card> cards) {

		ArrayList<Card> unorganizedCards = new ArrayList<>(cards);
		ArrayList<Card> organizedCards = new ArrayList<>();

		while (organizedCards.size() < cards.size()) {
			Card lowestCard = unorganizedCards.get(0);

			for (Card card: unorganizedCards) {
				if (card.getRank() < lowestCard.getRank() &&
					!organizedCards.contains(card)) {
					lowestCard = card;
				}
			}

			organizedCards.add(lowestCard);
			unorganizedCards.remove(lowestCard);
		}

		if (organizedCards.size() != cards.size()) {
			throw new RuntimeException("Hands dont match up");
		}

		return organizedCards;
	}
    */
	// evaluates the hand -- need to produce a score - ScoreT with [score for hand type, rank of highest card] - scores go from 0-9 while ranks go 2-14
	public int[] evaluate(ArrayList<Card> hand, ArrayList<Card> flop)
	{
		int h = highCard(hand);
        
		//ArrayList<Card> a = organizeCards(cards);
		ArrayList<Card> a = hand;
		for(Card c : flop){
			a = Services.insert(a, c);
		}
        
        for(int i = 0 ; i < a.size() ; i++){
            System.out.println(Integer.toString(a.get(i).rank));
        }

        int[] score = new int[] {0, 2};
		score[1] = h;
        boolean fl = false;
        boolean st = false;

		if (flush(a))
		{
            fl = true;
		}
        if (straight(a)){
            st = true;
        }

        if(fl && st){
            if(highCard(a) == 14){
//                System.out.println("You have a royal flush!");
                score[0] = 9;
            }
            else{
//                System.out.println("You have a straight flush!");
                score[0] = 8;
            }
        }

		else if (fourOfaKind(a))
		{
//			System.out.println("You have four of a kind!");
            score[0] = 7;
            score[1] = h;
		}
		else if (fullHouse(a))
		{
//			System.out.println("You have a full house!");
            score[0] = 6;
            score[1] = h;
		}
		else if (fl)
		{
//			System.out.println("You have a flush!");
            score[0] = 5;
		}
		else if (st)
		{
//			System.out.println("You have a straight!");
            score[0] = 4;
		}
		else if (triple(a))
		{
//			System.out.println("You have a triple!");
            score[0] = 3;
		}
		else if (twoPairs(a))
		{
//			System.out.println("You have two pairs!");
            score[0] = 2;
		}
		else if (pair(a))
		{
//			System.out.println("You have a pair!");
            score[0] = 1;
		}
		else
		{
//			System.out.println("Your highest card is " + highCard);
            score[0] = 0;
		}
        return score;
	}
	
	public static boolean flush(ArrayList<Card> a){
		if (a.get(0).suit == a.get(1).suit && a.get(1).suit == a.get(2).suit && a.get(2).suit == a.get(3).suit && a.get(3).suit == a.get(4).suit){
			return true;
		}
		return false;
	}

	public static boolean straight(ArrayList<Card> a){ //later need to account for a being able to be high or low
		int h = highCard(a);
		if(h == 14){
			boolean b1 = true;
			boolean b2 = true;

			for(int i = 0 ; i < a.size()- 1 ; i++){
				if (a.get(i+1).rank != a.get(i).rank + 1){
					b1 = false;
					break;
				} 
			}

            if(a.get(0).rank == 2){
			    ArrayList<Card> temp = new ArrayList<>();
			    temp.add(a.get(a.size()-1));
			    for(int i = 0 ; i < a.size() - 1 ; i++){
				    temp.add(a.get(i));
			    }
            
			    for(int i = 1 ; i < a.size()- 1 ; i++){
				    if (temp.get(i+1).rank != temp.get(i).rank + 1){
					    b2 = false;
					    break;
				    } 
			    }
            }
            else{
                b2 = false;
            }
			return (b1 || b2);
		}

		else{
			for(int i = 0 ; i < a.size()- 1 ; i++){
				if (a.get(i+1).rank != a.get(i).rank + 1){
					return false;
				} 
			}
			return true;
		}
	}

	public static int highCard(ArrayList<Card> a){
		return a.get(a.size() - 1).rank;
	}

	
	// checks for four of a kind
	public static boolean fourOfaKind(ArrayList<Card> a)
	{
		if (a.get(0).rank != a.get(3).rank && a.get(1).rank != a.get(4).rank)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	// checks for full house
	public static boolean fullHouse(ArrayList<Card> a)
	{
		if ((a.get(0).rank == a.get(1).rank && a.get(1).rank == a.get(2).rank && a.get(3).rank == a.get(4).rank) ||
		(a.get(2).rank == a.get(3).rank && a.get(3).rank == a.get(4).rank && a.get(0).rank == a.get(1).rank)){
			return true;
		}
		return false;
	}

	// checks for triple
	public static boolean triple(ArrayList<Card> a)
	{
		if (a.get(0).rank == a.get(2).rank || a.get(2).rank == a.get(4).rank)
		{
			return true;
		}
		return false;
	}
	
	// checks for two pairs
	public static boolean twoPairs(ArrayList<Card> a)
	{
		int check = 0;
		for(int counter = 1; counter < a.size(); counter++)
		{

			if (a.get(counter - 1).rank == a.get(counter).rank)
			{
				check++;
			}
		}
		if (check == 2)
		{
			return true;
		}
	
		return false;
		
	}
	
	// check for pair
	public static boolean pair(ArrayList<Card> a)
	{
		int check = 0;
		for(int counter = 1; counter < a.size(); counter++)
		{
			if (a.get(counter - 1).rank == a.get(counter).rank)
			{
				check++;
			}
		}
		if (check == 1)
		{
			return true;
		}
	
		return false;
	}

	public static void main(String[] args) {
		ArrayList<Card> hand = new ArrayList<>();
        ArrayList<Card> flop = new ArrayList<>();

        Card card1 = new Card();
        card1.suit = 4;
		card1.rank = 14;
        Card card2 = new Card();
        card2.suit = 4;
		card2.rank = 2;
        Card card3 = new Card();
        card3.suit = 4;
		card3.rank = 3;
        Card card4 = new Card();
        card4.suit = 4;
		card4.rank = 4;
        Card card5 = new Card();
        card5.suit = 4;
		card5.rank = 5;


        flop = Services.insert(flop, card1);
        flop = Services.insert(flop, card2);

        hand = Services.insert(hand, card3);
        hand = Services.insert(hand, card4);
        hand = Services.insert(hand, card5);
        

        HandEval H = new HandEval();

        int[] h = H.evaluate(hand, flop);
        System.out.println(Integer.toString(h[0]));
        System.out.println(Integer.toString(h[1]));

        /*
        for(int i = 0 ; i < hand.size() ; i++){
            System.out.println(Integer.toString(hand.get(i).rank));
        }

        for(int i = 0 ; i < flop.size() ; i++){
            System.out.println(Integer.toString(flop.get(i).rank));
        }
        */

        /*
		Card card1 = new Card();
		card1.suit = 4;
		card1.rank = 1;

		Card card2 = new Card();
		card2.suit = 1;
		card2.rank = 9;

		Card card3 = new Card();
		card3.suit = 1;
		card3.rank = 1;

		Card card4 = new Card();
		card4.suit = 4;
		card4.rank = 1;

		Card card5 = new Card();
		card5.suit = 4;
		card5.rank = 3;

		Card card6 = new Card();
		card6.suit = 1;
		card6.rank = 5;

		Card card7 = new Card();
		card7.suit = 2;
		card7.rank = 5;



		cards.add(card1);
		cards.add(card2);
		cards.add(card3);
		cards.add(card4);
		cards.add(card5);
		cards.add(card6);
		cards.add(card7);


		cards = organizeCards(cards);


		System.out.println(pair(cards));
		System.out.println(cards);
	*/
    }

	/* PUT SOMETHING LIKE THIS IN THE VIEW
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

	// tells player cards in hand
	public void dispHand()
	{
		for (int handCounter = 0; handCounter < HAND_SIZE; handCounter++)
		{
			this.display(hand[handCounter]);
		}
	}
	*/
}