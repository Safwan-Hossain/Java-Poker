package com.poker.server;

import com.poker.domain.player.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
public class GameLobby {

    @Getter
    private final GameSettings gameSettings;
    private final Map<String, Player> playerMap = new HashMap<>();

    public List<Player> getPlayersListCopy() {
        return playerMap.values().stream().map(Player::new).collect(Collectors.toList());
    }

    public Player addPlayer(String playerName, boolean isHost) {
        String playerId = generateUniquePlayerID();
        Player player = new Player(playerName, playerId);
        player.awardChips(gameSettings.getMinBuyIn());
        player.setHost(isHost);
        playerMap.put(playerId, player);
        return player;
    }

    public void removePlayer(String playerId) {
        playerMap.remove(playerId);
    }

    private String generateUniquePlayerID() {
        String playerId;
        do {
            playerId = UUID.randomUUID().toString();
        } while (playerMap.containsKey(playerId));
        return playerId;
    }

}
