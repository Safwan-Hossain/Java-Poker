package model.player;

import enumeration.PokerRole;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Player implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private final String name;
	private String playerID;
	private int chips;
	private List<Card> hand;
	private boolean hasTurn;
	private boolean isFolded;
	private PokerRole role;

	public Player(String name, int chips) {
		this(name, generateUniquePlayerID());
		this.chips = chips;
	}

	public Player(String name, String playerID) {
		this.name = name;
		this.playerID = playerID;
		this.chips = 0;
		this.hand = new ArrayList<>();
		this.hasTurn = false;
		this.isFolded = false;
	}

	public void takeChips(int amount) {
		if (amount > chips) {
			throw new IllegalArgumentException("Amount is higher than number of chips");
		}
		this.chips -= amount;
	}

	public void resetHand() {
		this.hand = new ArrayList<>();
	}
	public List<Card> getHand() {
		return new ArrayList<>(hand);
	}

	public void setHand(List<Card> hand) {
		this.hand = new ArrayList<>(hand);
	}

	public void addToHand(Card card) {
		hand.add(card);
	}
	public void addToHand(List<Card> cards) {
		for (Card card : cards) {
			addToHand(card);
		}
	}

	public String getName() {
		return name;
	}

	public boolean isFolded() {
		return isFolded;
	}

	public void setFolded(boolean isFolded) {
		this.isFolded = isFolded;
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
		return hasTurn;
	}

	public void setTurn(boolean hasTurn) {
		this.hasTurn = hasTurn;
	}

	public PokerRole getRole() {
		return role;
	}

	public void setRole(PokerRole role) {
		this.role = role;
	}

	public int getChips() {
		return chips;
	}

	public void setChips(int chips) {
		this.chips = chips;
	}

	public void giveTurn() {
		this.hasTurn = true;
	}

	public void takeTurn() {
		this.hasTurn = false;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Player)) return false;
		Player player = (Player) obj;
		return Objects.equals(playerID, player.playerID);
	}

	@Override
	public int hashCode() {
		return Objects.hash(playerID);
	}

	@Override
	public String toString() {
		return name;
	}

	private static String generateUniquePlayerID() {
		// Implement a method to generate a unique player ID
		return UUID.randomUUID().toString();
	}
}
