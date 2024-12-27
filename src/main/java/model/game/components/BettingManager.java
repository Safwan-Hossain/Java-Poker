package model.game.components;

import model.player.Player;

import java.util.*;
import java.util.stream.Collectors;

public class BettingManager {
    private final int smallBlind;
    private final int bigBlind;
    private int totalPot;
    private int minimumCallAmount;
    private int minimumBetAmount;
    private Player lastBetter;
    private final Map<Player, Integer> playerBettings;

    public BettingManager(int smallBlind) {
        this.smallBlind = smallBlind;
        this.bigBlind = smallBlind * 2;
        this.totalPot = 0;
        this.minimumCallAmount = bigBlind;
        this.minimumBetAmount = bigBlind * 2;
        this.playerBettings = new HashMap<>();
    }

    public void initializeBettings(List<Player> players) {
        for (Player player : players) {
            playerBettings.put(player, 0);
        }
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

    public int getTotalPot() {
        return totalPot;
    }


    public int getMinimumCallAmount() {
        return minimumCallAmount;
    }

    public int getMinimumBetAmount() {
        return minimumBetAmount;
    }

    /**
     * Returns an unmodifiable view of player bettings to ensure encapsulation.
     */
    public Map<Player, Integer> getPlayerBettings() {
        return Collections.unmodifiableMap(playerBettings);
    }


    public boolean isBettingEqualAmongActivePlayers() {
        Set<Integer> distinctBets = playerBettings.entrySet().stream()
                .filter(entry -> !entry.getKey().isFolded() && !entry.getKey().isBankrupt()) // Exclude folded and all-in players
                .map(Map.Entry::getValue)
                .collect(Collectors.toSet());

        return distinctBets.size() <= 1;
    }
}
