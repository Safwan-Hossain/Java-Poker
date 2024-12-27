package model.update.impl;

import enumeration.RoundState;
import enumeration.UpdateType;
import model.player.Card;
import model.player.Player;
import model.update.GameUpdate;

import java.util.List;

public class RoundStateUpdate extends GameUpdate {
    private final RoundState roundState;
    private final List<Card> tableCards;

    public RoundStateUpdate(RoundState roundState, List<Card> tableCards) {
        super(UpdateType.ROUND_STATE);
        this.roundState = roundState;
        this.tableCards = tableCards;
    }

    public RoundState getRoundState() {
        return roundState;
    }

    public List<Card> getTableCards() {
        return tableCards;
    }
}
