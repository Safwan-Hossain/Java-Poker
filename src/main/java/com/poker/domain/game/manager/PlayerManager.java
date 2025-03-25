package com.poker.domain.game.manager;

import com.poker.enumeration.PokerRole;
import com.poker.domain.player.Player;

import java.util.*;
import java.util.stream.Collectors;

import static com.poker.enumeration.PokerRole.*;

public class PlayerManager {
    private int dealerIndex;
    private int smallBlindIndex;
    private int bigBlindIndex;
    private int playerWithTurnIndex;
    private int turnCounter;
    private List<Player> players;


    public PlayerManager(List<Player> players) {
        this.players = new ArrayList<>(players);
        this.dealerIndex = 0;
        this.turnCounter = 0;
    }

    public void assignRoles() {
        updateRoleIndices();
        clearRoles();
        setRoles();
    }
    private void updateRoleIndices() {
        dealerIndex = getNextIndex(dealerIndex);
        smallBlindIndex = getNextIndex(dealerIndex);
        bigBlindIndex = getNextIndex(smallBlindIndex);
    }
    private int getNextIndex(int currentIndex) {
        return (currentIndex + 1) % players.size();
    }
    private void clearRoles() {
        players.forEach(player -> player.setRole(NONE));
    }
    private void setRoles() {
        players.get(dealerIndex).setRole(DEALER);
        players.get(smallBlindIndex).setRole(SMALL_BLIND);
        players.get(bigBlindIndex).setRole(BIG_BLIND);
    }


    private void giveTurnToPlayer(int index) {
        players.get(index).setTurn(true);
        incrementTurnCounter();
    }

    public boolean hasEveryoneHadATurn() {
        return turnCounter >= players.size() - getNumberOfFoldedPlayers();
    }

    public int getNumberOfFoldedPlayers() {
        return (int) players.stream().filter(Player::isFolded).count();
    }
    public int getNumberOfTotalPlayers() {
        return players.size();
    }
    public int getNumberOfUnfoldedPlayers() {
        return (int) players.stream().filter(player -> !player.isFolded()).count();
    }
    public Map<PokerRole, Player> getPlayersWithRoles() {
        Map<PokerRole, Player> roleMap = new HashMap<>();
        roleMap.put(PokerRole.DEALER, players.get(dealerIndex));
        roleMap.put(PokerRole.SMALL_BLIND, players.get(smallBlindIndex));
        roleMap.put(PokerRole.BIG_BLIND, players.get(bigBlindIndex));
        return roleMap;
    }


    // NOTE - It would be more efficient to use a hashmap and lookup by player Ids, but
    // we would then need a hashmap for the lookup and still a list for the ordering. So its acceptable for the purposes
    // of this project to use a list
    public Player getPlayer(Player player) {
        return players.stream()
                .filter(currPlayer -> currPlayer.equals(player))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cannot find player: " + player.getName() + ", " + player.getPlayerId()));
    }

    public Player getPlayer(String playerId) {
        return players.stream()
                .filter(player -> player.getPlayerId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cannot find player with ID:" + playerId));
    }

    public void unfoldAllFoldedPlayers() {
        players.forEach(player -> player.setFolded(false));
    }

    public Player getPlayerWithTurn() {
        return players.get(playerWithTurnIndex);
    }

    public void giveNextPlayerTurn() {
        validatePlayableState();
        takeTurnFromCurrentPlayer();
        giveTurnToNextPlayer();
    }

    public void giveFirstPlayerTurn() {
        validatePlayableState();
        takeTurnFromCurrentPlayer();
        assignFirstTurn();
    }

    public void assignFirstTurn() {
        do {
            playerWithTurnIndex = getNextIndex(bigBlindIndex);
        } while (getPlayerWithTurn().isBankrupt());
        giveTurnToPlayer(playerWithTurnIndex);
    }

    private void giveTurnToNextPlayer() {
        do {
            playerWithTurnIndex = getNextIndex(playerWithTurnIndex);
        } while (getPlayerWithTurn().isFolded() || getPlayerWithTurn().isBankrupt());
        giveTurnToPlayer(playerWithTurnIndex);
    }
    private void validatePlayableState() {
        if (getNumberOfFoldedPlayers() >= players.size() - 1) {
            throw new RuntimeException("All players folded");
        }
    }

    private void takeTurnFromCurrentPlayer() {
        getPlayerWithTurn().setTurn(false);
    }
    public void incrementTurnCounter() {
        turnCounter++;
    }
    public void resetTurnCounter() {
        this.turnCounter = 0;
    }

    public Player getPlayerByRole(PokerRole role) {
        return switch (role) {
            case DEALER -> players.get(dealerIndex);
            case SMALL_BLIND -> players.get(smallBlindIndex);
            case BIG_BLIND -> players.get(bigBlindIndex);
            default -> throw new IllegalArgumentException("Could not find player by role for role: " + role);
        };
    }

    public void setPlayerWithTurn(Player playerWithTurn) {
        players.forEach(player -> player.setTurn(player.equals(playerWithTurn)));
    }
    public void resetPlayerHands() {
        players.forEach(Player::resetHand);
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public List<Player> getPlayersCopy() {
        return players.stream().map(Player::new).collect(Collectors.toList());
    }


    public List<Player> getUnfoldedPlayers() {
        return players.stream()
                .filter(player -> !player.isFolded())
                .toList();
    }

    public void removePlayer(Player player) {
        this.players.remove(player);
    }


    public List<Player> getPlayersWithNoChips() {
        return players.stream()
                .filter(player -> player.getChips() <= 0)
                .toList();
    }
    public void removeLosers() {
        // TODO - add logic for handling role changes and check if current turn is valid
        players.removeIf(player -> player.getChips() <= 0);
    }

    public boolean isEveryoneAllIn() {
        return players.stream().filter(Player::isBankrupt).count() >= players.size();
    }

}
