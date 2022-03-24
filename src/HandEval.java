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
			System.out.println("a royal flush!");
		}
		else if (score[0] == 8) {
			System.out.println("a straight flush!");
		}
		else if (score[0] == 7) {
			return "four of a kind!";
		}
		else if (score[0] == 6) {
			return "a full house!";
		}
		else if (score[0] == 5) {
			return "a flush!";
		}
		else if (score[0] == 4) {
			return "a straight!";
		}
		else if (score[0] == 3) {
			return "a triple!";
		}
		else if (score[0] == 2) {
			return "two pairs!";
		}
	return "a high card!";
	}

	//NOTE: FOR NOW ACES ARE ONLY HIGH
	// evaluates the hand -- need to produce a score - ScoreT with [score for hand type, rank of highest card] - scores go from 0-9 while ranks go 2-14
	public int[] evaluate(ArrayList<Card> a)
	{
        int[] score = new int[] {0, 2};

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
                score[1] = 14;
            }
            else{
//                System.out.println("You have a straight flush!");
                score[0] = 8;
                score[1] = highCard(a);
            }
        }

		else if (fourOfaKind(a))
		{
//			System.out.println("You have four of a kind!");
            score[0] = 7;
            score[1] = highCard(a);
		}
		else if (fullHouse(a))
		{
//			System.out.println("You have a full house!");
            score[0] = 6;
            score[1] = highCard(a);
		}
		else if (fl)
		{
//			System.out.println("You have a flush!");
            score[0] = 5;
            score[1] = highCard(a);
		}
		else if (st)
		{
//			System.out.println("You have a straight!");
            score[0] = 4;
            score[1] = highCard(a);
		}
		else if (triple(a))
		{
//			System.out.println("You have a triple!");
            score[0] = 3;
            score[1] = highCard(a);
		}
		else if (twoPairs(a))
		{
//			System.out.println("You have two pairs!");
            score[0] = 2;
            score[1] = highCard(a);
		}
		else if (pair(a))
		{
//			System.out.println("You have a pair!");
            score[0] = 1;
            score[1] = highCard(a);
		}
		else
		{
			int highCard = highCard(a);
//			System.out.println("Your highest card is " + highCard);
            score[0] = 0;
            score[1] = highCard(a);
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
		for(int i = 0 ; i < a.size()- 1 ; i++){
			if (a.get(i+1).rank != a.get(i).rank + 1){
				return false;
			} 
		}
		return true;
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
