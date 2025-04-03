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
        return playerManager.getNonBankruptPlayers().size() <= 1;
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
        bettingManager.resetBettingsForEndOfRound();

        playerManager.assignRoles();
        deckManager.assignCardsToPlayers(playerManager.getPlayers());
        takeChipsFromBlinds();
    }


    public void takeChipsFromBlinds() {
        Player smallBlindPlayer = playerManager.getPlayerByRole(PokerRole.SMALL_BLIND);
        Player bigBlindPlayer = playerManager.getPlayerByRole(PokerRole.BIG_BLIND);

        bettingManager.takeChipsFromBlinds(smallBlindPlayer, bigBlindPlayer);
    }


    public void performBetByPlayer(Player player, int betAmount) {
        bettingManager.placeBet(player, betAmount);
    }

    public void performCallByPlayer(Player player) {
        bettingManager.placeCall(player);
    }

    public void removeBankruptPlayers() {
        playerManager.removeBankruptPlayers();
    }

    public boolean isBettingEqualAmongActivePlayers() {
        return bettingManager.isBettingEqualAmongActivePlayers();
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

    public void removePlayer(String playerId) {
        playerManager.getPlayer(playerId).setFolded(true);
        playerManager.removePlayer(playerId);
    }

    public List<Player> getPlayersCopy() {
        return playerManager.getPlayersCopy();
    }
    public Player getPlayerCopy(String playerId) {
        return playerManager.getPlayerCopy(playerId);
    }


    public Player getRichestPlayer() {
        Optional<Player> richestPlayer = playerManager.getPlayers()
                .stream()
                .max(Comparator.comparingInt(Player::getChips));

        return richestPlayer.orElse(null);
    }

    public boolean isOnlyOnePlayerLeft() {
        return playerManager.getNumberOfTotalPlayers() <= 1;
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
            case QUIT -> removePlayer(playerId);
            case BET, RAISE -> performBetByPlayer(player, betAmount);
            case CALL -> performCallByPlayer(player);
            case CHECK, WAIT -> {}
        }
    }

    public void advanceRoundState() {
        this.roundState = this.roundState.getNextRoundState();
        bettingManager.resetMinimumBetForNewRoundState();
        playerManager.resetTurnCounter();
        updateTableCards();
    }


    public List<Card> getTableCards() {
        return deckManager.getTableCards();
    }


    public List<Player> getBankruptPlayers() {
        return playerManager.getBankruptPlayers();
    }



    private boolean canCheck() {
        return  bettingManager.canCheck();
    }

    public boolean canPlaceFirstBet() {
        return bettingManager.canPlaceFirstBet();
    }

    public HashSet<PlayerAction> getValidActions(Player player) {
        Player localPlayer = getPlayer(player);
        HashSet<PlayerAction> validActions = new HashSet<>();

        if (localPlayer.isFolded()) {
            return new HashSet<>();
        }

        int playerBetPower = localPlayer.getChips();
        if (playerBetPower > getMinimumCallAmount()) {
            if (canPlaceFirstBet()) {
                validActions.add(PlayerAction.BET);
            }
            else {
                validActions.add(PlayerAction.RAISE);
            }
        }
        if (canCheck()) {
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

    public boolean isBetAmountValid(String playerId, int betAmount) {
        Player player = getPlayerById(playerId);
        int playerChips = player.getChips();
        int minimumBet = bettingManager.getMinimumBetAmount();

        boolean isAboveMinimum = betAmount >= minimumBet;
        boolean isWithinPlayerChips = betAmount <= playerChips;

        boolean isAllIn = betAmount == playerChips;
        return (isAboveMinimum || isAllIn) && isWithinPlayerChips;
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
