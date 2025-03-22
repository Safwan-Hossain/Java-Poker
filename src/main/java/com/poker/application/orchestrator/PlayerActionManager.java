package com.poker.application.orchestrator;

import com.poker.enumeration.GameEvent;
import com.poker.enumeration.PlayerAction;
import com.poker.domain.game.ServerGame;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.poker.enumeration.GameEvent.*;

@Component
@RequiredArgsConstructor
public class PlayerActionManager {

    public GameEvent handle(ServerGame game, String playerId, PlayerAction action, int betAmount) {
        game.applyPlayerAction(playerId, action, betAmount);

        if (game.allOtherPlayersFolded()) return ROUND_END;
        if (game.isBettingFinished()) return BETTING_IS_FINISHED;
        return ASSIGN_NEXT_PLAYER_TURN;
    }
}
