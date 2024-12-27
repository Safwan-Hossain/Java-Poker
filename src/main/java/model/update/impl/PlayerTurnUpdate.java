package model.update.impl;

import enumeration.UpdateType;
import model.player.Player;
import model.update.GameUpdate;

public class PlayerTurnUpdate extends GameUpdate {
    private final Player playerWithTurn;

    public PlayerTurnUpdate(Player playerWithTurn) {
        super(UpdateType.PLAYER_TURN);
        this.playerWithTurn = playerWithTurn;
    }

    public Player getPlayerWithTurn() {
        return playerWithTurn;
    }
}
