package model.update.impl;

import enumeration.RoundState;
import enumeration.UpdateType;
import model.player.Card;
import model.player.Player;
import model.game.Game;
import model.update.GameUpdate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameStateUpdate extends GameUpdate {
    private final Game game;
    private final RoundState roundState;
    private final List<Player> players;
    private final Map<Player, List<Card>> playerHands;
    private final List<Card> tableCards;

    public GameStateUpdate(Game game, RoundState roundState, List<Player> players, Map<Player, List<Card>> playerHands, List<Card> tableCards) {
        super(UpdateType.GAME_STATE);
        this.game = game;
        this.roundState = roundState;
        this.players = players;
        this.playerHands = playerHands;
        this.tableCards = tableCards;
    }

    public Game getGame() {
        return game;
    }

    public RoundState getRoundState() {
        return roundState;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Map<Player, List<Card>> getPlayerHands() {
        return playerHands;
    }

    public List<Card> getTableCards() {
        return tableCards;
    }
}
