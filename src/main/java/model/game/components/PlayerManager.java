package model.game.components;

import enumeration.PokerRole;
import model.player.Player;

import java.util.*;

import static enumeration.PokerRole.*;

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
        dealerIndex = incrementIndex(dealerIndex);
        smallBlindIndex = incrementIndex(dealerIndex);
        bigBlindIndex = incrementIndex(smallBlindIndex);
    }
    private int incrementIndex(int currentIndex) {
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

    public void assignFirstTurn() {
        playerWithTurnIndex = incrementIndex(bigBlindIndex);
        giveTurnToPlayer(playerWithTurnIndex);
    }

    private void giveTurnToPlayer(int index) {
        players.get(index).setTurn(true);
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

    public void setPlayerRoles(Map<PokerRole, Player> map) {
        Player dealer = map.get(PokerRole.DEALER);
        Player smallBlind = map.get(PokerRole.SMALL_BLIND);
        Player bigBlind = map.get(PokerRole.BIG_BLIND);

        getPlayer(dealer).setRole(PokerRole.DEALER);
        getPlayer(smallBlind).setRole(PokerRole.SMALL_BLIND);
        getPlayer(bigBlind).setRole(PokerRole.BIG_BLIND);

        dealerIndex = players.indexOf(getPlayer(dealer));
        smallBlindIndex = players.indexOf(getPlayer(smallBlind));
        bigBlindIndex = players.indexOf(getPlayer(bigBlind));
    }

    public Player getPlayer(Player player) {
        return players.stream()
                .filter(currPlayer -> currPlayer.equals(player))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cannot find player: " + player.getName() + ", " + player.getPlayerID()));
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

    private void validatePlayableState() {
        if (getNumberOfFoldedPlayers() >= players.size() - 1) {
            throw new RuntimeException("All players folded");
        }
    }

    private void takeTurnFromCurrentPlayer() {
        getPlayerWithTurn().setTurn(false);
    }

    private void giveTurnToNextPlayer() {
        do {
            playerWithTurnIndex = incrementIndex(playerWithTurnIndex);
        } while (getPlayerWithTurn().isFolded());
        giveTurnToPlayer(playerWithTurnIndex);
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
    public List<Player> getUnfoldedPlayers() {
        return players.stream()
                .filter(player -> !player.isFolded())
                .toList();
    }

    public void removePlayer(Player player) {
        this.players.remove(player);
    }

    public void setPlayers(List<Player> newPlayers) {
        players.removeIf(player -> !newPlayers.contains(player));
        for (Player player: newPlayers) {
            if (players.contains(player)) {
                getPlayer(player).setChips(player.getChips());
            }
            else {
                players.add(player);
            }
        }
    }

    public List<Player> getPlayersWithNoChips() {
        return players.stream()
                .filter(player -> player.getChips() <= 0)
                .toList();
    }
    public void removeLosers() {
        players.removeIf(player -> player.getChips() <= 0);
    }

    public boolean isEveryoneAllIn() {
        return players.stream().filter(Player::isBankrupt).count() >= players.size();
    }

}
