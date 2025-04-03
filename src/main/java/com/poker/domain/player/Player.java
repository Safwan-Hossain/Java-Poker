package com.poker.domain.player;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.poker.enumeration.PokerRole;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Player implements Serializable {

	@Getter(AccessLevel.NONE)
	@Serial
	private static final long serialVersionUID = 1L;

	@ToString.Include
	private final String name;

	@EqualsAndHashCode.Include
	private String playerId;

	private int chips;
	private List<Card> hand;
	private boolean hasTurn;
	private boolean isFolded;
	@Getter
	@Setter
	@JsonProperty("isHost")
	private boolean isHost;
	private PokerRole role;

	private int consecutiveMissedTurns = 0;

	public Player(String name, String playerId) {
		this.name = name;
		this.playerId = playerId;
		this.chips = 0;
		this.hand = new ArrayList<>();
		this.hasTurn = false;
		this.isFolded = false;
	}
	public Player(Player player) {
		this.name = player.name;
		this.playerId = player.playerId;
		this.chips = player.chips;
		this.hand = new ArrayList<>(player.hand);
		this.hasTurn = player.hasTurn;
		this.isFolded = player.isFolded;
		this.isHost = player.isHost;
		this.role = player.role;
	}


	public void takeChips(int amount) {
		if (amount > chips) {
			throw new IllegalArgumentException("Amount is higher than number of chips");
		}
		this.chips -= amount;
	}

	public void awardChips(int amount) {
		this.chips += amount;
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

	public void setFolded(boolean isFolded) {
		this.isFolded = isFolded;
	}

	public boolean isBankrupt() {
		return chips <= 0;
	}

	public boolean hasTurn() {
		return hasTurn;
	}

	public void setTurn(boolean hasTurn) {
		this.hasTurn = hasTurn;
	}

	public void setRole(PokerRole role) {
		this.role = role;
	}

	public void incrementMissedTurn() {
		consecutiveMissedTurns++;
	}

	public void resetMissedTurns() {
		consecutiveMissedTurns = 0;
	}

}
