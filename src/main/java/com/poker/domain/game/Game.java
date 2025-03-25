package com.poker.domain.game;

import com.poker.domain.game.manager.BettingManager;
import com.poker.domain.game.manager.DeckManager;
import com.poker.domain.game.manager.PlayerManager;
import com.poker.enumeration.PlayerAction;
import com.poker.enumeration.PokerRole;
import com.poker.enumeration.RoundState;
import com.poker.domain.player.Card;
import com.poker.domain.player.Player;
import com.poker.server.GameSettings;
import lombok.Getter;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


public class Game implements Serializable {
    private final DeckManager deckManager;
    private final BettingManager bettingManager;
    private final PlayerManager playerManager;

    private boolean hasGameStarted;
    private boolean hasGameEnded;
    @Getter
    private RoundState roundState;

    public Game(List<Player> players, GameSettings gameSettings) {
        this.deckManager = new DeckManager();
        this.bettingManager = new BettingManager(players, gameSettings.getSmallBlind(), gameSettings.getBigBlind());
        this.playerManager = new PlayerManager(players);

        this.hasGameStarted = false;
        this.hasGameEnded = false;
    }

    public boolean isGameOver() {
        return playerManager.getNumberOfTotalPlayers() <= 1;
    }

    public void startGame() {
        deckManager.resetDeck();
        this.hasGameStarted = true;
    }

    public void initializeRound() {
        roundState = RoundState.PRE_FLOP;
        playerManager.resetTurnCounter();
        playerManager.unfoldAllFoldedPlayers();;
        playerManager.resetPlayerHands();
        bettingManager.resetBettings();

        playerManager.assignRoles();
        deckManager.assignCardsToPlayers(playerManager.getPlayers());
        takeChipsFromBlinds();
    }


    public void takeChipsFromBlinds() {
        Player smallBlindPlayer = playerManager.getPlayerByRole(PokerRole.SMALL_BLIND);
        Player bigBlindPlayer = playerManager.getPlayerByRole(PokerRole.BIG_BLIND);

        bettingManager.takeChipsFromBlinds(smallBlindPlayer, bigBlindPlayer);
    }


    public void performBetByPlayer(Player player, int raiseToAmount) {
        bettingManager.placeBet(player, raiseToAmount);
    }

    public void performCallByPlayer(Player player) {
        bettingManager.placeCall(player);
    }

    public void removeLosers() {
        playerManager.removeLosers();
    }

    public boolean isBettingEqualAmongActivePlayers() {
        return bettingManager.isBettingEqualAmongActivePlayers();
    }

    public boolean isRoundStateOver() {
        if (playerManager.getNumberOfUnfoldedPlayers() <= 1) {
            return true; // End the round if only one player is left
        }
        return isBettingEqualAmongActivePlayers() && playerManager.hasEveryoneHadATurn();
    }

    public boolean isBettingFinished() {
        return isBettingEqualAmongActivePlayers() && playerManager.hasEveryoneHadATurn();
    }

    public boolean isEveryoneAllIn() {
        return playerManager.isEveryoneAllIn();
    }

    public void updateTableCards() {
        deckManager.updateTableCards(roundState);
    }

    private int getNumberOfFoldedPlayers() {
        return playerManager.getNumberOfFoldedPlayers();
    }

    public Player getPlayerWithTurn() {
        return playerManager.getPlayerWithTurn();
    }

    public void giveNextPlayerTurn() {
        playerManager.giveNextPlayerTurn();
    }

    public void giveFirstPlayerTurn() {
        playerManager.giveFirstPlayerTurn();
    }

    public void removePlayer(Player player) {
        playerManager.removePlayer(player);
    }

    public List<Player> getPlayersCopy() {
        return playerManager.getPlayersCopy();
    }


    public Map<String, Integer> getPlayerIdsToBettings() {
        return bettingManager.getPlayerIdsToBettings();
    }
    public List<String> getPlayerIds() {
        return playerManager.getPlayers().stream()
                .map(Player::getPlayerId)
                .collect(Collectors.toList());
    }


    public Player getPlayer(Player player) {
        return this.playerManager.getPlayer(player);
    }

    public Player getPlayerById(String playerId) {
        return this.playerManager.getPlayer(playerId);
    }

    public void applyPlayerAction(String playerId, PlayerAction playerAction, int betAmount) {
        Player player = getPlayerById(playerId);
        switch (playerAction) {
            case FOLD -> player.setFolded(true);
            case BET, RAISE -> performBetByPlayer(player, betAmount);
            case CALL -> performCallByPlayer(player);
            case CHECK, WAIT -> {}
        }
    }

    public void advanceRoundState() {
        this.roundState = this.roundState.getNextRoundState();
        playerManager.resetTurnCounter();
        updateTableCards();
    }


    public List<Card> getTableCards() {
        return deckManager.getTableCards();
    }


    public List<Player> getPlayersWithNoChips() {
        return playerManager.getPlayersWithNoChips();
    }


    private boolean canCheck(Player player) {
        return  bettingManager.canCheck(player);
    }

    private boolean canBet() {
        return bettingManager.canBet();
    }

    public HashSet<PlayerAction> getValidActions(Player player) {
        Player localPlayer = getPlayer(player);
        HashSet<PlayerAction> validActions = new HashSet<>();

        int playerBetPower = bettingManager.getBetPower(player);
        if (playerBetPower > getMinimumCallAmount()) {
            if (canBet()) {
                validActions.add(PlayerAction.BET);
            }
            else {
                validActions.add(PlayerAction.RAISE);
            }
        }
        if (canCheck(localPlayer)) {
            validActions.add(PlayerAction.CHECK);
        }
        else {
            validActions.add(PlayerAction.CALL);
        }
        validActions.add(PlayerAction.FOLD);

        return validActions;
    }

    public int getPlayerSidePot(Player player) {
        Map<Player, Integer> playerBettings = bettingManager.getPlayerBettings();
        if (playerBettings.containsKey(player)) {
            return playerBettings.get(player);
        }
        return playerBettings.get(getPlayer(player));
    }

    public int getPlayerBettingPower(Player player) {
        Player localPlayer = getPlayer(player);
        return getPlayerBettings().get(localPlayer) + localPlayer.getChips();
    }

    public boolean isOnlyOnePlayerUnfolded() {
        return playerManager.getNumberOfUnfoldedPlayers() == 1;
    }


//    GETTERS AND SETTERS
    public int getMinimumCallAmount() {
        return bettingManager.getMinimumCallAmount();
    }

    public int getMinimumBetAmount() {
        return bettingManager.getMinimumBetAmount();
    }

    public boolean hasGameStarted() {
        return hasGameStarted;
    }

    public boolean hasGameEnded() {
        return hasGameEnded;
    }
    public int getTotalPot() {
        return bettingManager.getTotalPot();
    }

    public Map<Player, Integer> getPlayerBettings() {
        return bettingManager.getPlayerBettings();
    }

    public void giveChipsToPlayer(int numOfChips, Player player) {
        playerManager.getPlayer(player).awardChips(numOfChips);
    }

    public int getPlayerBetting(String playerId) {
        Player player = getPlayerById(playerId);
        return bettingManager.getPlayerBet(player);
    }
}
