package com.poker.util;

import com.poker.enumeration.HandRank;
import com.poker.domain.player.Card;
import com.poker.domain.player.Card.Rank;
import com.poker.domain.player.Card.Suit;
import com.poker.domain.player.HandEvaluation;

import java.util.*;
import java.util.stream.Collectors;

import static com.poker.domain.player.Card.Rank.*;

public class HandEvaluator {


	private static final int PLAYER_CARDS_COUNT = 2;
	private static final int TABLE_CARDS_COUNT = 5;
	private static final int TOTAL_HAND_SIZE = 5;
	private static final int FOUR_OF_A_KIND_COUNT = 4;
	private static final int THREE_OF_A_KIND_COUNT = 3;
	private static final int PAIR_COUNT = 2;
	private static final int TWO_PAIR_CARD_COUNT = 4;
	private static final int ROYAL_FLUSH_HIGH_CARD = ACE.getValue();
	private static final List<Integer> ACE_LOW_STRAIGHT = List.of(2, 3, 4, 5);

	// Royal is when you have 10, Jack, Queen, King, Ace of the same suit
	private static final Set<Rank> ROYAL_RANKS = Set.of(TEN, JACK, QUEEN, KING, ACE);

	public static HandEvaluation evaluateHand(List<Card> playerCards, List<Card> tableCards) {
		if (playerCards.size() != PLAYER_CARDS_COUNT || tableCards.size() != TABLE_CARDS_COUNT) {
			throw new IllegalArgumentException("Player must have 2 cards and table must have 5 cards");
		}

		List<Card> allCards = new ArrayList<>(playerCards);
		allCards.addAll(tableCards);

		// Count card ranks and suits
		Map<Rank, Long> rankCount = getRankCount(allCards);
		Map<Suit, Long> suitCount = getSuitCount(allCards);

		// Determine best hand type (e.g. flush, pair, etc.)
		HandRank handRank = determineHandRank(allCards, rankCount, suitCount);

		// Get best 5 card hand for this hand type
		List<Card> bestFiveCardHand = getBestFiveCardHand(allCards, handRank, rankCount);

		return new HandEvaluation(handRank, bestFiveCardHand);
	}


	private static HandRank determineHandRank(List<Card> cards, Map<Rank, Long> rankCount, Map<Suit, Long> suitCount) {

		boolean isFlush = isFlush(suitCount);
		boolean isStraight = isStraight(rankCount);

		// Check for higher level hands that don't need further calculations first
		if (isFlush && isStraight && isStraightFlush(cards)) {
			if (isRoyal(cards)) {
				return HandRank.ROYAL_FLUSH;
			}
			return HandRank.STRAIGHT_FLUSH;
		}

		// Check remaining hands in descending order of strength
		if (isFourOfAKind(rankCount)) return HandRank.FOUR_OF_A_KIND;
		if (isFullHouse(rankCount)) return HandRank.FULL_HOUSE;
		if (isFlush) return HandRank.FLUSH;
		if (isStraight) return HandRank.STRAIGHT;
		if (isThreeOfAKind(rankCount)) return HandRank.THREE_OF_A_KIND;
		if (isTwoPair(rankCount)) return HandRank.TWO_PAIR;
		if (isOnePair(rankCount)) return HandRank.ONE_PAIR;

		return HandRank.HIGH_CARD;
	}

	private static List<Card> getBestFiveCardHand(List<Card> cards, HandRank handRank, Map<Rank, Long> rankCount) {
		// Sort cards by descending rank
		List<Card> sortedCards = cards.stream()
				.sorted(Comparator.comparingInt((Card card) -> card.rank().getValue()).reversed())
				.collect(Collectors.toList());

		// Based on the hand rank get the cards. (e.g. if the rank is 3 of a kind, then get the 3 cards from the full 5 cards)
		return switch (handRank) {
			case ROYAL_FLUSH, STRAIGHT_FLUSH, FLUSH -> getFlushCards(sortedCards);
			case FOUR_OF_A_KIND -> getFourOfAKind(sortedCards);
			case FULL_HOUSE -> getFullHouseCards(sortedCards);
			case STRAIGHT -> getStraightCards(sortedCards, rankCount);
			case THREE_OF_A_KIND -> getThreeOfAKind(sortedCards);
			case TWO_PAIR -> getTwoPairCards(sortedCards);
			case ONE_PAIR -> getOnePair(sortedCards);
			case HIGH_CARD -> sortedCards.subList(0, 5);
		};
	}


	private static List<Card> getFlushCards(List<Card> cards) {
		// Group cards by suit
		Map<Suit, List<Card>> suitMap = cards.stream().collect(Collectors.groupingBy(Card::suit));

		for (List<Card> suitCards : suitMap.values()) {
			// Return the first flush of 5 cards
			if (suitCards.size() >= TOTAL_HAND_SIZE) {
				return suitCards.subList(0, TOTAL_HAND_SIZE);
			}
		}
		return Collections.emptyList();
	}
	private static List<Card> getTwoPairCards(List<Card> cards) {
		// Group cards by rank
		Map<Rank, List<Card>> rankMap = cards.stream().collect(Collectors.groupingBy(Card::rank));

		List<Card> pairs = new ArrayList<>();
		List<Card> kickers = new ArrayList<>();

		for (List<Card> rankCards : rankMap.values()) {
			if (rankCards.size() >= 2) {
				pairs.addAll(rankCards.subList(0, 2));
			} else {
				kickers.addAll(rankCards);
			}
		}

		// Sort pairs and kickers in descending order
		pairs.sort((a, b) -> b.rank().getValue() - a.rank().getValue());
		kickers.sort((a, b) -> b.rank().getValue() - a.rank().getValue());

		if (pairs.size() < TWO_PAIR_CARD_COUNT) {
			throw new RuntimeException("Not enough pairs for Two Pair hand");
		}

		// The best two pairs (4 cards) and the highest kicker (1 card)
		List<Card> result = new ArrayList<>(pairs.subList(0, TWO_PAIR_CARD_COUNT));
		result.add(kickers.getFirst());

		return result;
	}

	private static List<Card> getFourOfAKind(List<Card> cards) {
		return getNOfAKindCards(cards, FOUR_OF_A_KIND_COUNT);
	}
	private static List<Card> getThreeOfAKind(List<Card> cards) {
		return getNOfAKindCards(cards, THREE_OF_A_KIND_COUNT);
	}
	private static List<Card> getOnePair(List<Card> cards) {
		return getNOfAKindCards(cards, PAIR_COUNT);
	}


	private static List<Card> getNOfAKindCards(List<Card> cards, int n) {
		// Group by rank
		Map<Rank, List<Card>> rankMap = cards.stream().collect(Collectors.groupingBy(Card::rank));
		List<Card> result = new ArrayList<>();

		for (List<Card> rankCards : rankMap.values()) {

			// Find the group with exactly n cards
			if (rankCards.size() == n) {
				result.addAll(rankCards);
				break;
			}
		}

		// Add highest kickers to make total 5 cards
		for (Card card : cards) {
			if (!result.contains(card) && result.size() < TOTAL_HAND_SIZE) {
				result.add(card);
			}
		}
		return result;
	}

	private static List<Card> getFullHouseCards(List<Card> cards) {
		// group by rank
		Map<Rank, List<Card>> rankMap = cards.stream().collect(Collectors.groupingBy(Card::rank));
		List<Card> threeOfAKind = new ArrayList<>();
		List<Card> pair = new ArrayList<>();

		// First find the 3 of a kind and the pair
		for (List<Card> rankCards : rankMap.values()) {
			if (rankCards.size() >= 3 && threeOfAKind.isEmpty()) {
				threeOfAKind = rankCards.subList(0, 3);
			} else if (rankCards.size() >= 2 && pair.isEmpty()) {
				pair = rankCards.subList(0, 2);
			}
		}

		// Then combine both into a full house
		threeOfAKind.addAll(pair);
		return threeOfAKind;
	}

	private static List<Card> getStraightCards(List<Card> cards, Map<Rank, Long> rankCount) {

		List<Card> result = new ArrayList<>();

		// Get rank values as a sorted array
		List<Integer> ranks = rankCount.keySet().stream()
				.map(Rank::getValue)
				.sorted()
				.collect(Collectors.toList());

		// Check for ace low straight (A, 2, 3, 4, 5)
		if (ranks.contains(ROYAL_FLUSH_HIGH_CARD) && new HashSet<>(ranks).containsAll(ACE_LOW_STRAIGHT)) {
			result = cards.stream()
					.filter(card ->
							card.rank() == Rank.ACE ||
							card.rank() == Rank.TWO ||
							card.rank() == Rank.THREE ||
							card.rank() == Rank.FOUR ||
							card.rank() == Rank.FIVE)
					.collect(Collectors.toList());
			return result;
		}

		// Check for standard straights (5 cards with consecutive numbers)
		for (int i = 0; i <= ranks.size() - 5; i++) {
			boolean isStraight = true;
			for (int j = 0; j < 4; j++) {
				if (ranks.get(i + j + 1) - ranks.get(i + j) != 1) {
					isStraight = false;
					break;
				}
			}
			if (isStraight) {
				List<Integer> straightRanks = ranks.subList(i, i + 5);
				result = cards.stream()
						.filter(card -> straightRanks.contains(card.rank().getValue()))
						.collect(Collectors.toList());
				break;
			}
		}

		return result;
	}

	// Count how many of each rank exist
	private static Map<Rank, Long> getRankCount(List<Card> cards) {
		return cards.stream().collect(Collectors.groupingBy(Card::rank, Collectors.counting()));
	}

	// Count how many of each suit exist
	private static Map<Suit, Long> getSuitCount(List<Card> cards) {
		return cards.stream().collect(Collectors.groupingBy(Card::suit, Collectors.counting()));
	}


	// True if here are 5 or more values of the same suit type (e.g. if there are 5 hearts then there is a flush)
	private static boolean isFlush(Map<Suit, Long> suitCount) {
		return suitCount.size() == 1 || suitCount.values().stream().anyMatch(count -> count >= 5);
	}

	// True if here are 5 or more cards with consecutive numbers (e.g. 2, 3, 4, 5, 6 )
	private static boolean isStraight(Map<Rank, Long> rankCount) {
		List<Integer> ranks = rankCount.keySet().stream()
				.map(Rank::getValue)
				.sorted()
				.collect(Collectors.toList());

		// Check for ace low straight (A, 2, 3, 4, 5)
		if (ranks.contains(ROYAL_FLUSH_HIGH_CARD) && new HashSet<>(ranks).containsAll(ACE_LOW_STRAIGHT)) {
			return true;
		}

		// Check for standard straights
		for (int i = 0; i <= ranks.size() - 5; i++) {
			if (isConsecutive(ranks.subList(i, i + 5))) {
				return true;
			}
		}

		return false;
	}

	// True if there is both a straight and a flush (e.g. 2, 3, 4, 5, 6 and all 5 of those cards are spades)
	private static boolean isStraightFlush(List<Card> cards) {
		return cards.stream()
				.collect(Collectors.groupingBy(Card::suit))
				.values().stream()
				.filter(suitedCards -> suitedCards.size() >= 5)
				.anyMatch(suitedCards -> {
					List<Integer> ranks = suitedCards.stream()
							.map(card -> card.rank().getValue())
							.sorted()
							.collect(Collectors.toList());
					return isConsecutive(ranks);
				});
	}

	private static boolean isConsecutive(List<Integer> ranks) {
		for (int i = 0; i <= ranks.size() - 5; i++) {
			if (ranks.get(i + 4) - ranks.get(i) == 4 &&
					ranks.subList(i, i + 5).stream().distinct().count() == 5) {
				return true;
			}
		}
		// Check for ace low straight (5 high)
		return ranks.contains(ROYAL_FLUSH_HIGH_CARD) && new HashSet<>(ranks).containsAll(ACE_LOW_STRAIGHT);
	}
	private static boolean isRoyal(List<Card> cards) {
        // Map cards by suit
		Map<Suit, Set<Rank>> suitToRanksMap = new HashMap<>();
		for (Card card : cards) {
			suitToRanksMap.computeIfAbsent(card.suit(), k -> new HashSet<>()).add(card.rank());
		}

		// Check if any key in map has all royal ranks
		for (Set<Rank> ranks : suitToRanksMap.values()) {
			if (ranks.containsAll(ROYAL_RANKS)) {
				return true;
			}
		}
		return false;
	}

	private static boolean isFullHouse(Map<Rank, Long> rankCount) {
		boolean hasThreeOfAKind = false;
		boolean hasPair = false;

		for (Map.Entry<Rank, Long> entry : rankCount.entrySet()) {
			long count = entry.getValue();
			if (count == THREE_OF_A_KIND_COUNT) {
				hasThreeOfAKind = true;
			} else if (count == PAIR_COUNT) {
				hasPair = true;
			}
		}

		return hasThreeOfAKind && hasPair;
	}

	private static boolean isFourOfAKind(Map<Rank, Long> rankCount) {
		return rankCount.containsValue((long) FOUR_OF_A_KIND_COUNT);
	}

	private static boolean isThreeOfAKind(Map<Rank, Long> rankCount) {
		return rankCount.containsValue((long) THREE_OF_A_KIND_COUNT);
	}

	private static boolean isTwoPair(Map<Rank, Long> rankCount) {
		// Count how many ranks occur exactly twice (e.g. for [2, 2, 3, 3, 4] 2 and 3 occur exactly twice  )
		return rankCount.values().stream().filter(count -> count == PAIR_COUNT).count() == 2;
	}


	private static boolean isOnePair(Map<Rank, Long> rankCount) {
		return rankCount.containsValue((long) PAIR_COUNT);
	}

}
