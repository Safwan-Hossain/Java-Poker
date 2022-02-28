import java.util.ArrayList;

/***********************
 *  Assignment 4
 *  Andreas Hadjigeorgiou
 *  ahh2131
 *  Player class
 */

public class Player 
{
	public String name;
	protected int chips;
	private ArrayList<Card> hand;
	private boolean hasTurn;

	// gets 5 cards from deck
	public Player(String name, int chips)
	{
		this.name = name;
		this.chips = chips;
		this.hasTurn = false;
	}
	
	public boolean hasTurn() {
		return this.hasTurn;
	}

	public void giveTurn() {
		this.hasTurn = true;
	}

	public void takeTurn() {
		this.hasTurn = false;
	}

	public void set_chips(int c){ //for now just int - later normalized to standard 5, 10, 20 values
		chips = c;
	}

	public int getChips() {
		return this.chips;
	}

	public void takeChips(int amount) {
		if (amount > chips) {
			throw new IllegalArgumentException("Amount is higher than number of chips");
		}
		this.chips -= amount;
	}

	public void insert(Card new_c){ //ensures player hand remains sorted by rank from smallest to largest
		ArrayList<Card> a = hand;

        if (a.size() == 0){
            a.add(new_c);
        }
        else if (a.get(0).greater_than(new_c)){
            a.add(0, new_c);
        }
        else{
			int i;
            for(i = 0 ; i < a.size() - 1 ; i++){
                if ((new_c.greater_than(a.get(i))) && a.get(i+1).greater_than(new_c)){
                    break;
                }
            }
            a.add(i, new_c);
        }
		hand = a;
    }

	public ArrayList<Card> get_hand(){
		return hand;
	}


	/*
	// switches card for a new card
	public Card redraw(int counter, Deck deck)
	{
		Card card = deck.redeal();
		return card;
	}
	*/

}
