package model.game;

import enumeration.PlayerAction;
import enumeration.PokerRole;
import enumeration.RoundState;
import model.game.components.BettingManager;
import model.game.components.DeckManager;
import model.game.components.PlayerManager;
import model.player.Card;
import model.player.HandEvaluation;
import model.player.Player;
import util.HandEvaluator;
import util.RoundWinnerEvaluator;

import java.io.Serializable;
import java.util.*;


public class Game implements Serializable {
    private final int MAX_HAND_SIZE;
    private final int MAX_TABLE_CARDS;

    private final DeckManager deckManager;
    private final BettingManager bettingManager;
    private final PlayerManager playerManager;

    private boolean hasGameStarted;
    private boolean hasGameEnded;
    private RoundState roundState;

    public Game(List<Player> players, int smallBlind) {
        this.MAX_HAND_SIZE = 2;
        this.MAX_TABLE_CARDS = 5;

        this.deckManager = new DeckManager(this.MAX_TABLE_CARDS);
        this.bettingManager = new BettingManager(smallBlind);
        this.playerManager = new PlayerManager(players);

        this.hasGameStarted = false;
        this.hasGameEnded = false;
    }

    public boolean isGameOver() {
        return playerManager.getNumberOfTotalPlayers() <= 1;
    }

    public void startGame() {
        deckManager.shuffleDeck();
        this.hasGameStarted = true;
    }


    public void initializeRound() {
        playerManager.resetTurnCounter();
        roundState = RoundState.PRE_FLOP;
        bettingManager.resetBettings();

        assignRoles();
        assignPlayerHands();
        assignFirstTurn();
        takeChipsFromBlinds();
    }

    public void takeChipsFromBlinds() {
        Player smallBlindPlayer = playerManager.getPlayerByRole(PokerRole.SMALL_BLIND);
        Player bigBlindPlayer = playerManager.getPlayerByRole(PokerRole.BIG_BLIND);

        bettingManager.takeChipsFromBlinds(smallBlindPlayer, bigBlindPlayer);
    }

    public void assignRoles() {
        playerManager.assignRoles();
    }

    public void assignPlayerHands() {
        deckManager.assignCardsToPlayers(playerManager.getPlayers());
    }

    public void assignFirstTurn() {
        playerManager.assignFirstTurn();
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

    public void endRound() {
        endRoundState();


        playerManager.unfoldAllFoldedPlayers();
        bettingManager.resetBettings();
        deckManager.resetDeck();

        playerManager.resetPlayerHands();
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

    public void removePlayer(Player player) {
        playerManager.removePlayer(player);
    }

    public Map<PokerRole, Player> getPlayersWithRoles() {
        return playerManager.getPlayersWithRoles();
    }

    public void setPlayerRoles(Map<PokerRole, Player> roleMap) {
        this.playerManager.setPlayerRoles(roleMap);
    }

    public List<Card> getPlayerHand(Player player) {
        return getPlayer(player).getHand();
    }

    public void setPlayerHand(Player player, List<Card> playerHand) {
        getPlayer(player).setHand(playerHand);
    }

    public void setPlayerWithTurn(Player playerWithTurn) {
        this.playerManager.setPlayerWithTurn(playerWithTurn);
    }

    public List<Player> getPlayers() {
        return playerManager.getPlayers();
    }

    public synchronized void setPlayers(List<Player> newPlayers) {
        playerManager.setPlayers(newPlayers);
    }

    public synchronized Player getPlayer(Player player) {
        return this.playerManager.getPlayer(player);
    }

    public void applyPlayerAction(Player actingPlayer, PlayerAction playerAction, int betAmount) {
        Player player = getPlayer(actingPlayer);
        switch (playerAction) {
            case FOLD -> player.setFolded(true);
            case BET, RAISE -> performBetByPlayer(player, betAmount);
            case CALL -> performCallByPlayer(player);
            case CHECK, WAIT -> {}
        }
        playerManager.incrementTurnCounter();
    }

    //TODO - separate end round state from this method
    public void advanceRoundState() {
        endRoundState();
        this.roundState = RoundState.getNextRoundState(roundState);
        playerManager.unfoldAllFoldedPlayers();
        updateTableCards();
    }

    public void endRoundState() {
        bettingManager.resetBettings();
        playerManager.resetTurnCounter();
    }

    public void giveChipsToWinners() {
        List<Player> winners = getWinningPlayers();
        int winningShare = bettingManager.getTotalPot() / winners.size();
        winners.forEach(winner -> winner.setChips(winner.getChips() + winningShare));
    }

    public void giveChipsToLastPlayer() {
        playerManager.getUnfoldedPlayers().forEach(player -> player.setChips(player.getChips() + bettingManager.getTotalPot()));
    }

    public List<Card> getTableCards() {
        return deckManager.getTableCards();
    }
    public void setTableCards(List<Card> tableCards) {
        deckManager.setTableCards(tableCards);
    }

    public List<Player> getPlayersWithNoChips() {
        return playerManager.getPlayersWithNoChips();
    }

    public String getPlayerHandName(Player player) {
        if (player.getHand().size() < MAX_HAND_SIZE) {
            throw new RuntimeException("Hand not setup for player: " + player.getName());
        }

        HandEvaluation handEvaluation = HandEvaluator.evaluateHand(player.getHand(), deckManager.getTableCards());
        return handEvaluation.getHandRank().toString();
    }

    public List<Player> getWinningPlayers() {
        List<Player> playersList = playerManager.getPlayers();
        List<Card> tableCards = deckManager.getTableCards();
        return RoundWinnerEvaluator.determineWinners(playersList, tableCards);
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

        int playerBetPower = localPlayer.getChips() + getPlayerBettings().get(localPlayer);
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

    public boolean allOtherPlayersFolded() {
        return playerManager.getNumberOfUnfoldedPlayers() <= 1;
    }


//    GETTERS AND SETTERS
    public int getMinimumCallAmount() {
        return bettingManager.getMinimumCallAmount();
    }

    public int getMinimumBetAmount() {
        return bettingManager.getMinimumBetAmount();
    }

    public RoundState getRoundState() {
        return roundState;
    }

    public void setRoundState(RoundState roundState) {
        this.roundState = roundState;
    }

    public boolean hasGameStarted() {
        return hasGameStarted;
    }

    public void setHasGameStarted(boolean hasGameStarted) {
        this.hasGameStarted = hasGameStarted;
    }

    public boolean hasGameEnded() {
        return hasGameEnded;
    }

    public void setHasGameEnded(boolean hasGameEnded) {
        this.hasGameEnded = hasGameEnded;
    }

    public int getTotalPot() {
        return bettingManager.getTotalPot();
    }

    public Map<Player, Integer> getPlayerBettings() {
        return bettingManager.getPlayerBettings();
    }

}
