package util;

import enumeration.HandRank;
import model.player.Card;
import model.player.Card.Rank;
import model.player.Card.Suit;
import model.player.HandEvaluation;

import java.util.*;
import java.util.stream.Collectors;

import static model.player.Card.Rank.*;

public class HandEvaluator {

	public static HandEvaluation evaluateHand(List<Card> playerCards, List<Card> tableCards) {
		if (playerCards.size() != 2 || tableCards.size() != 5) {
			throw new IllegalArgumentException("Player must have 2 cards and table must have 5 cards");
		}

		List<Card> allCards = new ArrayList<>(playerCards);
		allCards.addAll(tableCards);

		Map<Rank, Long> rankCount = getRankCount(allCards);
		Map<Suit, Long> suitCount = getSuitCount(allCards);
		HandRank handRank = determineHandRank(allCards, rankCount, suitCount);
		List<Card> bestFiveCardHand = getBestFiveCardHand(allCards, handRank, rankCount);

		return new HandEvaluation(handRank, bestFiveCardHand);
	}

	private static HandRank determineHandRank(List<Card> cards, Map<Rank, Long> rankCount, Map<Suit, Long> suitCount) {

		boolean isFlush = isFlush(suitCount);
		boolean isStraight = isStraight(rankCount);

		if (isFlush && isStraight && isStraightFlush(cards)) {
			if (isRoyal(cards)) {
				return HandRank.ROYAL_FLUSH;
			}
			return HandRank.STRAIGHT_FLUSH;
		}

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
		List<Card> sortedCards = cards.stream()
				.sorted(Comparator.comparingInt((Card card) -> card.getRank().getValue()).reversed())
				.collect(Collectors.toList());

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
		Map<Suit, List<Card>> suitMap = cards.stream()
				.collect(Collectors.groupingBy(Card::getSuit));
		for (List<Card> suitCards : suitMap.values()) {
			if (suitCards.size() >= 5) {
				return suitCards.subList(0, 5);
			}
		}
		return Collections.emptyList();
	}
	private static List<Card> getTwoPairCards(List<Card> cards) {
		Map<Rank, List<Card>> rankMap = cards.stream()
				.collect(Collectors.groupingBy(Card::getRank));

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
		pairs.sort((a, b) -> b.getRank().getValue() - a.getRank().getValue());
		kickers.sort((a, b) -> b.getRank().getValue() - a.getRank().getValue());

		if (pairs.size() < 4) {
			throw new RuntimeException("Not enough pairs for Two Pair hand");
		}

		// The best two pairs (4 cards) and the highest kicker (1 card)
		List<Card> result = new ArrayList<>(pairs.subList(0, 4));
		result.add(kickers.get(0));

		return result;
	}

	private static List<Card> getFourOfAKind(List<Card> cards) {
		return getNOfAKindCards(cards, 4);
	}
	private static List<Card> getThreeOfAKind(List<Card> cards) {
		return getNOfAKindCards(cards, 3);
	}
	private static List<Card> getOnePair(List<Card> cards) {
		return getNOfAKindCards(cards, 2);
	}
	private static List<Card> getNOfAKindCards(List<Card> cards, int n) {
		Map<Rank, List<Card>> rankMap = cards.stream()
				.collect(Collectors.groupingBy(Card::getRank));
		List<Card> result = new ArrayList<>();
		for (List<Card> rankCards : rankMap.values()) {
			if (rankCards.size() == n) {
				result.addAll(rankCards);
				break;
			}
		}
		for (Card card : cards) {
			if (!result.contains(card) && result.size() < 5) {
				result.add(card);
			}
		}
		return result;
	}

	private static List<Card> getFullHouseCards(List<Card> cards) {
		Map<Rank, List<Card>> rankMap = cards.stream()
				.collect(Collectors.groupingBy(Card::getRank));
		List<Card> threeOfAKind = new ArrayList<>();
		List<Card> pair = new ArrayList<>();
		for (List<Card> rankCards : rankMap.values()) {
			if (rankCards.size() >= 3 && threeOfAKind.isEmpty()) {
				threeOfAKind = rankCards.subList(0, 3);
			} else if (rankCards.size() >= 2 && pair.isEmpty()) {
				pair = rankCards.subList(0, 2);
			}
		}
		threeOfAKind.addAll(pair);
		return threeOfAKind;
	}

	private static List<Card> getStraightCards(List<Card> cards, Map<Rank, Long> rankCount) {

		List<Card> result = new ArrayList<>();
		List<Integer> ranks = rankCount.keySet().stream()
				.map(Rank::getValue)
				.sorted()
				.collect(Collectors.toList());

		// Check for ace low straight (A, 2, 3, 4, 5)
		if (ranks.contains(14) && new HashSet<>(ranks).containsAll(Arrays.asList(2, 3, 4, 5))) {
			result = cards.stream()
					.filter(card ->
							card.getRank() == Rank.ACE ||
							card.getRank() == Rank.TWO ||
							card.getRank() == Rank.THREE ||
							card.getRank() == Rank.FOUR ||
							card.getRank() == Rank.FIVE)
					.collect(Collectors.toList());
			return result;
		}

		// Check for standard straights
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
						.filter(card -> straightRanks.contains(card.getRank().getValue()))
						.collect(Collectors.toList());
				break;
			}
		}

		return result;
	}

	private static Map<Rank, Long> getRankCount(List<Card> cards) {
		return cards.stream().collect(Collectors.groupingBy(Card::getRank, Collectors.counting()));
	}

	private static Map<Suit, Long> getSuitCount(List<Card> cards) {
		return cards.stream().collect(Collectors.groupingBy(Card::getSuit, Collectors.counting()));
	}

	private static boolean isFlush(Map<Suit, Long> suitCount) {
		return suitCount.size() == 1 || suitCount.values().stream().anyMatch(count -> count >= 5);
	}

	private static boolean isStraight(Map<Rank, Long> rankCount) {
		List<Integer> ranks = rankCount.keySet().stream()
				.map(Rank::getValue)
				.sorted()
				.collect(Collectors.toList());

		// Check for ace low straight (A, 2, 3, 4, 5)
		if (ranks.contains(14) && new HashSet<>(ranks).containsAll(Arrays.asList(2, 3, 4, 5))) {
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

	private static boolean isStraightFlush(List<Card> cards) {
		return cards.stream()
				.collect(Collectors.groupingBy(Card::getSuit))
				.values().stream()
				.filter(suitedCards -> suitedCards.size() >= 5)
				.anyMatch(suitedCards -> {
					List<Integer> ranks = suitedCards.stream()
							.map(card -> card.getRank().getValue())
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
		return ranks.contains(14) && new HashSet<>(ranks).containsAll(Arrays.asList(2, 3, 4, 5));
	}
	private static boolean isRoyal(List<Card> cards) {
		Set<Rank> royalRanks = new HashSet<>(Arrays.asList(TEN, JACK, QUEEN, KING, ACE));

		// Map cards by suit
		Map<Suit, Set<Rank>> suitToRanksMap = new HashMap<>();
		for (Card card : cards) {
			suitToRanksMap.computeIfAbsent(card.getSuit(), k -> new HashSet<>()).add(card.getRank());
		}

		// Check if any key in map has all royal ranks
		for (Set<Rank> ranks : suitToRanksMap.values()) {
			if (ranks.containsAll(royalRanks)) {
				return true;
			}
		}
		return false;
	}
	private static boolean isFourOfAKind(Map<Rank, Long> rankCount) {
		return rankCount.containsValue(4L);
	}

	private static boolean isFullHouse(Map<Rank, Long> rankCount) {
		boolean hasThreeOfAKind = false;
		boolean hasPair = false;

		for (Map.Entry<Rank, Long> entry : rankCount.entrySet()) {
			long count = entry.getValue();
			if (count == 3) {
				hasThreeOfAKind = true;
			} else if (count == 2) {
				hasPair = true;
			}
		}

		return hasThreeOfAKind && hasPair;
	}

	private static boolean isThreeOfAKind(Map<Rank, Long> rankCount) {
		return rankCount.containsValue(3L);
	}

	private static boolean isTwoPair(Map<Rank, Long> rankCount) {
		return rankCount.values().stream().filter(count -> count == 2).count() == 2;
	}

	private static boolean isOnePair(Map<Rank, Long> rankCount) {
		return rankCount.containsValue(2L);
	}

}
