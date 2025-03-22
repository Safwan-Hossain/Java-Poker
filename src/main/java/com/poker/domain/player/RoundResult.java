package com.poker.domain.player;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RoundResult {
    private int totalPot;
    private int sharePerWinner;
    private List<Player> winners;
    private Map<Player, HandEvaluation> playerHandEvaluations;
}
