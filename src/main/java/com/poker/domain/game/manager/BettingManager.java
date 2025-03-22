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
        this.minimumBetAmount = bigBlind * 2;
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
        placeBet(smallBlindPlayer, smallBlind);
        placeBet(bigBlindPlayer, bigBlind);
    }

    public void placeBet(Player player, int raiseToAmount) {
        int existingBet = playerBettings.getOrDefault(player, 0);
        int betAmount = raiseToAmount - existingBet;

        if (betAmount > player.getChips()) {
            betAmount = player.getChips();
            raiseToAmount = betAmount + existingBet;
        }

        if (raiseToAmount > minimumCallAmount) {
            minimumCallAmount = raiseToAmount;
            minimumBetAmount = raiseToAmount * 2;
        }

        player.takeChips(betAmount);
        totalPot += betAmount;
        playerBettings.put(player, raiseToAmount);
        lastBetter = player;
    }

    public void placeCall(Player player) {
        int actualCallAmount = minimumCallAmount - playerBettings.getOrDefault(player, 0);
        if (actualCallAmount > player.getChips()) {
            actualCallAmount = player.getChips();
        }
        player.takeChips(actualCallAmount);
        totalPot += actualCallAmount;
        int totalCallAmount = playerBettings.get(player) + actualCallAmount;
        playerBettings.put(player, totalCallAmount);
    }

    public void resetBettings() {
        playerBettings.replaceAll((player, bet) -> 0);
        totalPot = 0;
        lastBetter = null;
        minimumCallAmount = 0;
        minimumBetAmount = smallBlind;
    }

    public boolean canCheck(Player player) {
        if (playerBettings.get(player) == minimumCallAmount) {
            return true;
        }

        for (Player currPlayer : playerBettings.keySet()) {
            if (playerBettings.get(currPlayer) > 0) {
                return false;
            }
        }
        return true;
    }

    public boolean canBet() {
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
                        entry -> entry.getKey().getPlayerId(), // Convert player to player ID
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
