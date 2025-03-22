package com.poker.domain.player;

import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

//@Getter
public record Card(Suit suit, Rank rank) implements Serializable, Comparable<Card> {

	public enum Suit {
		CLUBS,
		DIAMONDS,
		HEARTS,
		SPADES
	}

	@Getter
	public enum Rank {
		TWO(2),
		THREE(3),
		FOUR(4),
		FIVE(5),
		SIX(6),
		SEVEN(7),
		EIGHT(8),
		NINE(9),
		TEN(10),
		JACK(11),
		QUEEN(12),
		KING(13),
		ACE(14);

		private final int value;

		Rank(int value) {
			this.value = value;
		}

	}

	public Card(Suit suit, Rank rank) {
		this.suit = Objects.requireNonNull(suit, "Suit cannot be null");
		this.rank = Objects.requireNonNull(rank, "Rank cannot be null");
	}

	@Override
	public String toString() {
		return rank.name() + " of " + suit.name();
	}

	@Override
	public int compareTo(Card other) {
		return Integer.compare(this.rank.getValue(), other.rank.getValue());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		Card card = (Card) obj;
		return suit == card.suit && rank == card.rank;
	}

}
