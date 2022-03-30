import java.io.Serializable;
import java.util.ArrayList;

/***********************
 *  Assignment 4
 *  Andreas Hadjigeorgiou
 *  ahh2131
 *  Player class
 */

public class Player implements Serializable
{
	public String getName() {
		return name;
	}

	public String name;
	private String playerID;
	protected int chips;
	private ArrayList<Card> hand;
	private boolean hasTurn;
	private PokerRole role;


	// gets 5 cards from deck
	public Player(String name, int chips)
	{
		this.name = name;
		this.chips = chips;
		this.hasTurn = false;
		this.hand = new ArrayList<>();
	}
	public Player(String name, String playerID)
	{
		this.name = name;
		this.playerID = playerID;
		this.hasTurn = false;
		this.hand = new ArrayList<>();
	}

	public boolean isBankrupt() {
		return chips <= 0;
	}

	public String getPlayerID() {
		return playerID;
	}

	public void setPlayerID(String playerID) {
		this.playerID = playerID;
	}

	public boolean hasTurn() {
		return this.hasTurn;
	}

	public void setTurn(boolean hasTurn) {
		this.hasTurn = hasTurn;
	}

	public void giveTurn() {
		this.hasTurn = true;
	}

	public void takeTurn() {
		this.hasTurn = false;
	}

	public PokerRole getRole() {
		return role;
	}

	public void setRole(PokerRole role) {
		this.role = role;
	}

	public void addToHand(Card card) {
		hand.add(card);
	}

	public void setHand(ArrayList<Card> hand) {
		this.hand = hand;
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

	public ArrayList<Card> getHand(){
		return hand;
	}


	@Override
	public boolean equals(Object obj) {

		if (this == obj) return true;

		if (!(obj instanceof Player)) {
			return false;
		}
		Player player = (Player) obj;

		return this.getPlayerID().equals(player.getPlayerID());
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
