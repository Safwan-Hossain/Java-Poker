package model.update.impl;

import enumeration.PlayerAction;
import enumeration.UpdateType;
import model.player.Player;
import model.update.GameUpdate;

public class PlayerActionUpdate extends GameUpdate {
    private final Player player;
    private final PlayerAction action;
    private final int betAmount;

    public PlayerActionUpdate(Player player, PlayerAction action, int betAmount) {
        super(UpdateType.PLAYER_ACTION);
        this.player = player;
        this.action = action;
        this.betAmount = betAmount;
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerAction getAction() {
        return action;
    }

    public int getBetAmount() {
        return betAmount;
    }
}
