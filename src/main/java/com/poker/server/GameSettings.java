package com.poker.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameSettings {

    private int buyIn = 1000;
    private int smallBlind = 10;
    private int bigBlind = 25;
    private int minBuyIn = 500;
    private int minPlayers = 2;
    private int maxPlayers = 9;
    private int playerHandSize = 2;
}
