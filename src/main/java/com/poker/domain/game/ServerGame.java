package com.poker.domain.game;

import com.poker.enumeration.PlayerAction;
import com.poker.enumeration.RoundState;
import com.poker.domain.player.Card;
import com.poker.domain.player.Player;
import com.poker.server.GameLobby;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public int getPlayerBetting(String playerId) {
        return mainGame.getPlayerBetting(playerId);
    }

    public void applyPlayerAction(String playerId, PlayerAction playerAction, int betAmount) {
        if (playerId.isBlank()) {
            throw new IllegalArgumentException("Tried to apply action with empty player ID");
        }
        mainGame.applyPlayerAction(playerId, playerAction,betAmount);
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

    public List<Player> getPlayersWithNoChips() {
        return mainGame.getPlayersWithNoChips();
    }

    // ======== Getters and Setters ========= //

    public Player getPlayerWithTurn() { return mainGame.getPlayerWithTurn(); }

    public boolean isBettingFinished() {
        return mainGame.isBettingFinished();
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

}
