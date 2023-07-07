package util;
import model.Card;

import java.util.ArrayList;


public class HandEval {
	// TEMPORARY
	public static String getHandName(int[] score) {
		if (score[0] == 9 && score[1] == 14) {
			return "royal flush";
		} else if (score[0] == 8) {
			return "straight flush";
		} else if (score[0] == 7) {
			return "four of a kind";
		} else if (score[0] == 6) {
			return "full house";
		} else if (score[0] == 5) {
			return "flush";
		} else if (score[0] == 4) {
			return "straight";
		} else if (score[0] == 3) {
			return "triple";
		} else if (score[0] == 2) {
			return "two pairs";
		} else if (score[0] == 1) {
			return "pair";
		}
		return "high card";
	}

	/*
	private static ArrayList<model.Card> organizeCards(ArrayList<model.Card> cards) {

		ArrayList<model.Card> unorganizedCards = new ArrayList<>(cards);
		ArrayList<model.Card> organizedCards = new ArrayList<>();

		while (organizedCards.size() < cards.size()) {
			model.Card lowestCard = unorganizedCards.get(0);

			for (model.Card card: unorganizedCards) {
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
	public int[] evaluate(ArrayList<Card> hand, ArrayList<Card> flop) {

		//ArrayList<model.Card> a = organizeCards(cards);
		ArrayList<Card> a = new ArrayList<Card>();
		for (Card c : hand){
			a.add(c);
		}
		for (Card c : flop) {
			a = Services.insert(a, c);
		}

		//for(int i = 0 ; i < a.size() ; i++){
		//    System.out.println(Integer.toString(a.get(i).rank));
		// }

		int[] score = new int[]{0, 2};
		//score[1] = h;

		int fl = flush(a);
		int str = 0;
		int strAce = 0;


		if (fl > 0) {
			ArrayList<Card> fland = a; //modifies a but its safe
			for(int i = fland.size() - 1; i >= 0 ; i--){
				if(fland.get(i).getSuit() != fl){
					fland.remove(i);
				}
			}

			str = straight(fland, fl);
			strAce = straightLowAce(fland, fl);
			if (str == 14) { //royal flush
				score[0] = 9;
				score[1] = 14;
			} else if (strAce == 14) { //straight flush with low ace
				score[0] = 8;
				score[1] = 14;
			} else if (str > 0) { //straight flush
				score[0] = 8;
				score[1] = str;
			} else {//flush
				score[0] = 5;
				score[1] = highCard(fland);
			}

		}

		//can't have flush and four of a kind / fullhouse (everything else is lower score than flush) - important mutex
		else {
			int fok = fourOfaKind(a);
			str = straight(a, 0);
			strAce = straightLowAce(a, 0);
			int fh = fullHouse(a); //these need to not modify it - insert values into temp, don't point to a
			int tp = twoPairs(a); //this too
			int trp = triple(a);
			int p = pair(a);
			//System.out.println("trp: " + Integer.toString(trp));

			if (fok > 0) //fourOfaKind
			{
//			System.out.println("You have four of a kind!");
				score[0] = 7;
				score[1] = fok;
			} else if (fh > 0) {
//			System.out.println("You have a full house!");
				score[0] = 6;
				score[1] = fh;
			} else if (str > 0 || strAce > 0) {
//			System.out.println("You have a straight!");
				score[0] = 4;
				score[1] = Math.max(str, strAce);
			} else if (trp > 0) {
//			System.out.println("You have a triple!");
				score[0] = 3;
				score[1] = trp;
			} else if (tp > 0) {
//			System.out.println("You have two pairs!");
				score[0] = 2;
				score[1] = tp;
			} else if (p > 0) {
//			System.out.println("You have a pair!");
				score[0] = 1;
				score[1] = p;
			} else {
//			System.out.println("Your highest card is " + highCard);
				score[0] = 0;
				score[1] = highCard(a);
			}
		}

		return score;
	}



	//tested - returns flush suit and high card
	public static int flush(ArrayList<Card> a) {

		for (int i = 1; i <= 4; i++) {
			int count = 0;
			//int high = 0;
			for (int j = a.size() - 1 ; j >= 0 ; j--) {
				if (a.get(j).getSuit() == i) {
					count++;
					int r = a.get(j).getRank();
					//if (r >= high) {
					//	high = r;
					//}
					if (count >= 5) {
						//return new int[]{i, high};
						return i;
					}
				}
			}
		}
		return 0;

		//if (a.get(0).suit == a.get(1).suit && a.get(1).suit == a.get(2).suit && a.get(2).suit == a.get(3).suit && a.get(3).suit == a.get(4).suit){
		//	return true;
		//}
		//return false;
	}

	//tested - returns 14 if theres a straight with low ace under given suit, else 0
	public static int straightLowAce(ArrayList<Card> a, int suit) {

		ArrayList<Card> temp1 = new ArrayList<Card>();
		for (Card c : a) {
			if (c.getRank() == 14) {
				temp1.add(c);
			}
		}
		for (Card c : a) {
			if (c.getRank() != 14) {
				temp1.add(c);
			}
		}


		ArrayList<Card> temp = temp1;
		if (suit != 0) {

			for (int j = temp.size() - 1; j >= 0; j--) {
				if (temp.get(j).getSuit() != suit) {
					temp.remove(j);
				}
			}

		}
		else{
			for(int j = temp.size() - 1 ; j >= 1 ; j--){
				if(temp.get(j).getRank() == 14){
					temp.remove(j);
				}
			}
		}
		//for(model.Card c : temp){
		//	System.out.println(Integer.toString(c.suit) + ", " + Integer.toString(c.rank)); //fine
		//}


		int count = 1;
		//int high = highCard(temp);
		//check this tomorrow
		//for (int j = 0; j < temp.size(); j++) { //don't need this loop i dont think - checking specifically for low ace - can just do for 1 ace
			int current = 1;
			//if (temp.get(j).rank == 14) {

				for (int i = 0; i < temp.size() - 1; i++) {

					if (current + 1 == temp.get(i + 1).getRank()) {
						count++;
						current++;
						if (count >= 5){
							return 14;
						}
						//System.out.println("Hi!");
					} else if (temp.get(i).getRank() == temp.get(i + 1).getRank()) {
						continue;
					} else {
						return 0;
					}
				}
				if (count >= 5) {
					return 14;
				}
			//}
		//}
		return 0;
	}


	//tested
	public static int straight(ArrayList<Card> a, int suit) {

		ArrayList<Card> temp = new ArrayList<Card>();
		if (suit != 0) {

			for (int j = a.size() - 1; j >= 0; j--) {
				if (a.get(j).getSuit() == suit) {
					temp.add(a.get(j));
				}
			}

		}

		else{
			temp = a;
		}

		int count = 1;
		int high = temp.get(temp.size() - 1).getRank();
		for (int i = temp.size() - 1; i > 0; i--) {
			int r = temp.get(i).getRank();

			if (r == temp.get(i - 1).getRank() + 1) {
				count++;
				//System.out.println("HI" + Integer.toString(i));
				if(count >= 5){
					return high;
				}
			} else if (r == temp.get(i - 1).getRank()) {
				continue;
			} else {
				//System.out.println("HIGH" + Integer.toString(i));
				count = 1;
				high = temp.get(i-1).getRank();
			}
		}

		return 0;

	}

	//fine
	public static int highCard(ArrayList<Card> a) {
		return a.get(a.size() - 1).getRank();
	}

	/*
	public static void main(String[] args) {
		ArrayList<model.Card> hand = new ArrayList<>();
		ArrayList<model.Card> flop = new ArrayList<>();

		model.Card card1 = new model.Card();
		card1.suit = 4;
		card1.rank = 10;
		model.Card card2 = new model.Card();
		card2.suit = 3;
		card2.rank = 2;
		model.Card card3 = new model.Card();
		card3.suit = 3;
		card3.rank = 4;
		model.Card card4 = new model.Card();
		card4.suit = 3;
		card4.rank = 5;
		model.Card card5 = new model.Card();
		card5.suit = 3;
		card5.rank = 3;

		model.Card card6 = new model.Card();
		card6.suit = 3;
		card6.rank = 14;

		model.Card card7 = new model.Card();
		card7.suit = 3;
		card7.rank = 6;


		flop = util.Services.insert(flop, card1);
		flop = util.Services.insert(flop, card2);
		flop = util.Services.insert(flop, card6);
		flop = util.Services.insert(flop, card7);
		flop = util.Services.insert(flop, card3);
		//flop = util.Services.insert(flop, card4);
		//flop = util.Services.insert(flop, card5);

		hand = util.Services.insert(hand, card4);
		hand = util.Services.insert(hand, card5);


		util.HandEval H = new util.HandEval();

		int[] h = H.evaluate(hand, flop);
		System.out.println(Integer.toString(h[0]));
		System.out.println(Integer.toString(h[1]));

	}
	*/


	// tested
	public static int fourOfaKind(ArrayList<Card> a) {
		int count = 1;
		for (int i = 0; i < a.size() - 1; i++) {
			if (a.get(i).getRank() == a.get(i + 1).getRank()) {
				count++;
				if (count == 4) {
					return a.get(i).getRank();
				}
			} else {
				count = 1;
			}
		}
		return 0;
	}

	// tested
	public static int fullHouse(ArrayList<Card> a) {
		int t = triple(a);
		if (t > 0) {
			//ArrayList<model.Card> temp = a; // this modifies? - yup
			ArrayList<Card> temp = new ArrayList<Card>();

			for (int i = a.size() - 1; i >= 0; i--) {
				if (a.get(i).getRank() != t) {
					temp.add(a.get(i));
				}
			}

			int p = pair(temp);
			if (p > 0) {
				return Math.max(t, p);
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}

	// tested
	public static int triple(ArrayList<Card> a) {
		int Max = 0;
		int count = 1;
		for (int i = 0; i < a.size() - 1; i++) {

			if (a.get(i).getRank() == a.get(i + 1).getRank()) {
				count++;

				if (count == 3) {
					//System.out.println("Here!");
					Max = a.get(i).getRank();
				}
			} else {
				count = 1;
			}
		}
		return Max;
	}

	// tested
	public static int twoPairs(ArrayList<Card> a) {
		int p1 = pair(a);
		//System.out.println(Integer.toString(p1));
		if (p1 > 0) {
			ArrayList<Card> temp = new ArrayList<Card>();
			for (int i = a.size() - 1; i >= 0; i--) {
				//System.out.println("temp:");
				if (a.get(i).getRank() != p1) {
					temp.add(a.get(i));
					//System.out.print(Integer.toString(a.get(i).rank));
				}
			}
			int p2 = pair(temp);

			if (p2 > 0) {
				return p1;
			} else {
				return 0;
			}
		} else {
			return 0;
		}

	}

	// tested
	public static int pair(ArrayList<Card> a) {
		int Max = 0;
		int count = 1;
		for (int i = 0; i < a.size() - 1; i++) {
			if (a.get(i).getRank() == a.get(i + 1).getRank()) {
				count++;
				if (count == 2) {
					if (Max <= a.get(i).getRank()){ //shouldn't need this
						Max = a.get(i).getRank();
					}
				}
			} else {
				count = 1;
			}
		}


		return Max;
	}

}

        /*
        for(int i = 0 ; i < hand.size() ; i++){
            System.out.println(Integer.toString(hand.get(i).rank));
        }

        for(int i = 0 ; i < flop.size() ; i++){
            System.out.println(Integer.toString(flop.get(i).rank));
        }
        */

        /*
		model.Card card1 = new model.Card();
		card1.suit = 4;
		card1.rank = 1;

		model.Card card2 = new model.Card();
		card2.suit = 1;
		card2.rank = 9;

		model.Card card3 = new model.Card();
		card3.suit = 1;
		card3.rank = 1;

		model.Card card4 = new model.Card();
		card4.suit = 4;
		card4.rank = 1;

		model.Card card5 = new model.Card();
		card5.suit = 4;
		card5.rank = 3;

		model.Card card6 = new model.Card();
		card6.suit = 1;
		card6.rank = 5;

		model.Card card7 = new model.Card();
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


	/* PUT SOMETHING LIKE THIS IN THE VIEW
	// generates string for each card in hand
	public void display(model.Card card)
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
