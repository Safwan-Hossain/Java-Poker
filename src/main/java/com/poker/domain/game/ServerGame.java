package com.poker.domain.game;

import com.poker.domain.player.Card;
import com.poker.domain.player.Player;
import com.poker.enumeration.PlayerAction;
import com.poker.enumeration.RoundState;
import com.poker.server.GameLobby;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class ServerGame {

    private Game mainGame;

    public void initializeGame(GameLobby gameLobby) {
        List<Player> allPlayers = gameLobby.getPlayersListCopy();
        mainGame = new Game(allPlayers, gameLobby.getGameSettings());
        mainGame.startGame();
    }
    public HashSet<PlayerAction> getValidActions(Player player) {
        return mainGame.getValidActions(player);
    }
    public int getMinimumCallAmount() {
        return mainGame.getMinimumCallAmount();
    }

    public int getMinimumBetAmount() {
        return mainGame.getMinimumBetAmount();
    }

    public int getPlayerBetting(String playerId) {
        return mainGame.getPlayerBetting(playerId);
    }

    public boolean isBetAmountValid(String playerId, int betAmount) {
       return mainGame.isBetAmountValid(playerId, betAmount);
    }

    public void applyPlayerAction(String playerId, PlayerAction playerAction, int betAmount) {
        if (playerId.isBlank()) {
            throw new IllegalArgumentException("Tried to apply action with empty player ID");
        }
        mainGame.applyPlayerAction(playerId, playerAction, betAmount);
    }

    public void giveNextPlayerTurn() {
        mainGame.giveNextPlayerTurn();
    }

    public void giveFirstPlayerTurn() {
        mainGame.giveFirstPlayerTurn();
    }
    public List<Player> getPlayersCopy() {
        return mainGame.getPlayersCopy();
    }

    public Player getPlayerCopy(String playerId) {
        return mainGame.getPlayerCopy(playerId);
    }

    public boolean isOnlyOnePlayerLeft() {
        return mainGame.isOnlyOnePlayerLeft();
    }

    public Map<String, Integer> getPlayerIdsToBettings() {
        return mainGame.getPlayerIdsToBettings();
    }

    public List<Card> getTableCards() {
        return mainGame.getTableCards();
    }

    public void giveChipsToPlayer(int numOfChips, Player player) { mainGame.giveChipsToPlayer(numOfChips, player); }

    public void initializeRound() {
        mainGame.initializeRound();
    }

    public List<Player> getBankruptPlayers() {
        return mainGame.getBankruptPlayers();
    }

    // ======== Getters and Setters ========= //

    public Player getPlayerWithTurn() { return mainGame.getPlayerWithTurn(); }


    public int getTotalPot() { return mainGame.getTotalPot(); }

    public boolean isBettingFinished() {
        return mainGame.isBettingFinished();
    }

    public void removePlayer(String playerId){
        mainGame.removePlayer(playerId);
    }

    
    public RoundState getRoundState() {
        return mainGame.getRoundState();
    }

    public void advanceRoundState() {
        mainGame.advanceRoundState();
    }

    public boolean hasGameEnded() {
        return mainGame.hasGameEnded();
    }

    public boolean isGameOver() {
        return mainGame.isGameOver();
    }

    public boolean isOnlyOnePlayerUnfolded() {
        return mainGame.isOnlyOnePlayerUnfolded();
    }

    public boolean everyoneIsAllIn() {
        return mainGame.isEveryoneAllIn();
    }

    public Player getRichestPlayer() {
        return mainGame.getRichestPlayer();
    }

}
