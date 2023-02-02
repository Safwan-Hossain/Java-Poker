package game;

import enumeration.game.PokerRole;
import enumeration.game.RoundState;
import model.Card;
import model.Client;
import model.GameInfo;
import model.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class ServerGame {
    private static Game mainGame;

    private final ArrayList<Player> players;

    public void setHostReady(boolean hostReady) {
        this.isHostReady = hostReady;
    }

    // TODO
    private int buyIn = 10000;
    private int smallBlind = 25;
    private volatile boolean isHostReady;
    private volatile boolean hasPlayerResponded;
    private volatile GameInfo gameInfo;

    public ServerGame() {
        players = new ArrayList<>();
    }

    public void startGame() {
        for (Player player: players) {
            player.set_chips(buyIn);
        }

        mainGame = new Game(players, smallBlind);
        mainGame.startGame();
        this.isHostReady = true;
    }

    public void addPlayer(Client client) {
        Player player = createPlayer(client);
        players.add(player);
    }

    public void removePlayer(Client client) {
        if (!playerExists(client)) {return;}
        Player player = findPlayer(client);
        players.remove(player);
    }

    // ====== TEMP =====
    public void addPlayer(String name, String ID) {
        players.add(new Player(name, ID));
    }

    // =================

    private Player createPlayer(Client client) {
        return new Player(client.getClientName(), client.getClientID());
    }

    private boolean playerExists(Client client) {
        for (Player player: players) {
            if (Objects.equals(player.getPlayerID(), client.getClientID())) {
                return true;
            }
        }
        return false;
    }

    private Player findPlayer(Client client) {
        for (Player player: players) {
            if (Objects.equals(player.getPlayerID(), client.getClientID())) {
                return player;
            }
        }
        throw new RuntimeException("Cannot find player");
    }

    public void applyPlayerAction(GameInfo gameInfo) {
        if (gameInfo.getPlayerWithTurn() == null) {
            throw new NullPointerException();
        }
        mainGame.applyPlayerAction(gameInfo.getPlayerWithTurn(), gameInfo.getPlayerAction(), gameInfo.getBetAmount());
    }

    public void giveNextPlayerTurn() {
        mainGame.giveNextPlayerTurn();
    }

    public ArrayList<Player> getPlayers() {
        return mainGame.getPlayers();
    }

    public HashMap<PokerRole, Player> getPlayersWithRoles() {
        return mainGame.getPlayersWithRoles();
    }

    public ArrayList<Card> getTableCards() {
        return mainGame.getTableCards();
    }

    public void giveChipsToWinners() {
        mainGame.giveChipsToWinners();
    }

    public void removeLosers() {
        mainGame.removeLosers();
    }

    public ArrayList<Player> getWinningPlayers() {
        return mainGame.getWinningPlayers();
    }
    public ArrayList<Player> getPlayersWithNoChips() {
        return mainGame.getPlayersWithNoChips();
    }
    // ======== Getters and Setters ========= //

    public Game getMainGame() {
        return mainGame;
    }

    public boolean isHostReady() {
        return this.isHostReady;
    }

    public void initializeRound() {
        mainGame.initializeRound();
    }

    public Player getPlayerWithTurn() { return mainGame.getPlayerWithTurn(); }

    public boolean isRoundStateOver() {
        return mainGame.isRoundStateOver();
    }

    public boolean hasPlayerResponded() {
        return hasPlayerResponded;
    }

    public void setHasPlayerResponded(boolean hasPlayerResponded) {
        this.hasPlayerResponded = hasPlayerResponded;
    }

    public GameInfo getGameInfo() {
        return gameInfo;
    }

    public void setGameInfo(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
    }

    public RoundState getRoundState() {
        return mainGame.getRoundState();
    }

    public void setRoundState(RoundState roundState) {
        mainGame.setRoundState(roundState);
    }

    public void advanceRoundState() {
        mainGame.advanceRoundState();
    }

    public boolean hasGameStarted() {
        if (mainGame == null) {
            return false;
        }
        return mainGame.hasGameStarted();
    }

    public boolean hasGameEnded() {
        return mainGame.hasGameEnded();
    }

    public void updateTableCards() {
        mainGame.updateTableCards();
    }

    public void endRound() {
        mainGame.endRound();
    }

    public boolean isGameOver() {
        return mainGame.isGameOver();
    }

    public void giveChipsToLastPlayer() {
        mainGame.giveChipsToLastPlayer();
    }

    public boolean allOtherPlayersFolded() {
        return mainGame.allOtherPlayersFolded();
    }

    public boolean everyoneIsAllIn() {
        return mainGame.everyoneIsAllIn();
    }

    public void setBuyIn(int buyIn) {
        this.buyIn = buyIn;
    }

    public void setSmallBlind(int smallBlind) {
        this.smallBlind = smallBlind;
    }
}
