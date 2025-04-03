package com.poker.domain.game.manager;

import com.poker.domain.player.Player;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

public class BettingManager {
    private final int smallBlind;
    private final int bigBlind;
    @Getter
    private int totalPot = 0;
    @Getter
    private int minimumCallAmount;
    @Getter
    private int minimumBetAmount;
    private Player lastBetter;
    private final Map<Player, Integer> playerBettings = new HashMap<>();

    public BettingManager(List<Player> players, int smallBlind, int bigBlind) {
        this.smallBlind = smallBlind;
        this.bigBlind = bigBlind;
        this.minimumCallAmount = bigBlind;
        this.minimumBetAmount = bigBlind;
        initializeBettings(players);
    }

    public void initializeBettings(List<Player> players) {
        for (Player player : players) {
            playerBettings.put(player, 0);
        }
    }


    public int getBetPower(Player player) {
        return player.getChips() + playerBettings.get(player);
    }

    public void takeChipsFromBlinds(Player smallBlindPlayer, Player bigBlindPlayer) {
        postBlind(smallBlindPlayer, smallBlind);
        postBlind(bigBlindPlayer, bigBlind);
    }

    private void postBlind(Player player, int blindAmount) {
        int adjustedBlind = getAdjustedBetAmount(player, blindAmount);
        if (adjustedBlind <= 0) {
            throw new IllegalArgumentException("Player does not have enough chips to post the blind.");
        }
        int totalPlayerBet = playerBettings.getOrDefault(player, 0) + adjustedBlind;
        updatePlayerBet(player, adjustedBlind, totalPlayerBet);
    }

    private void updatePlayerBet(Player player, int betAmount, int newTotalBet) {
        player.takeChips(betAmount);
        totalPot += betAmount;
        playerBettings.put(player, newTotalBet);

        if (newTotalBet > minimumCallAmount) {
            int lastRaiseAmount = newTotalBet - minimumCallAmount;
            minimumCallAmount = newTotalBet;
            minimumBetAmount = minimumCallAmount + lastRaiseAmount;

        }

        lastBetter = player;
    }

    private int getAdjustedBetAmount(Player player, int requestedAmount) {
        return Math.min(requestedAmount, player.getChips());
    }

    public void placeBet(Player player, int betAmount) {
        if (betAmount > player.getChips()) {
            throw new IllegalArgumentException("Bet amount is higher than player chips amount");
        }
        if (betAmount < minimumBetAmount) {
            throw new IllegalArgumentException("Bet amount must be at least the minimum bet amount: " + minimumBetAmount);
        }

        int totalPlayerBet = playerBettings.getOrDefault(player, 0) + betAmount;
        updatePlayerBet(player, betAmount, totalPlayerBet);
    }


    public void placeCall(Player player) {
        int currentBet = playerBettings.getOrDefault(player, 0);
        int actualCallAmount = minimumCallAmount - currentBet;

        if (actualCallAmount <= 0) {
            return;
        }

        if (actualCallAmount > player.getChips()) {
            actualCallAmount = player.getChips();
        }

        player.takeChips(actualCallAmount);
        totalPot += actualCallAmount;

        playerBettings.put(player, currentBet + actualCallAmount);
    }


    public void resetMinimumBetForNewRoundState() {
        playerBettings.replaceAll((player, bet) -> 0);
        lastBetter = null;
        minimumCallAmount = bigBlind;
        minimumBetAmount = bigBlind;
    }

    public void resetBettingsForEndOfRound() {
        playerBettings.replaceAll((player, bet) -> 0);
        lastBetter = null;
        minimumCallAmount = bigBlind;
        minimumBetAmount = bigBlind;
        totalPot = 0;
    }

    public boolean canCheck() {
        return canPlaceFirstBet() && playerBettings.values().stream().noneMatch(bet -> bet > 0);
    }


    public boolean canPlaceFirstBet() {
        return lastBetter == null;
    }

    public int getPlayerBet(Player player) {
        return playerBettings.getOrDefault(player, 0);
    }

    /**
     * Returns an unmodifiable view of player bettings to ensure encapsulation.
     */
    public Map<Player, Integer> getPlayerBettings() {
        return Collections.unmodifiableMap(playerBettings);
    }

    public Map<String, Integer> getPlayerIdsToBettings() {
        return playerBettings.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().getPlayerId(),
                        Map.Entry::getValue
                ));
    }


    public boolean isBettingEqualAmongActivePlayers() {
        Set<Integer> distinctBets = playerBettings.entrySet().stream()
                .filter(entry -> !entry.getKey().isFolded() && !entry.getKey().isBankrupt()) // Exclude folded and all-in players
                .map(Map.Entry::getValue)
                .collect(Collectors.toSet());

        return distinctBets.size() <= 1;
    }

}
