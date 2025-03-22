package com.poker.statemachine.actions.impl;

import com.poker.application.orchestrator.GameLogicHandler;
import com.poker.enumeration.GameEvent;
import com.poker.enumeration.GameState;
import com.poker.services.TimeoutService;
import com.poker.statemachine.actions.BaseAction;
import com.poker.statemachine.events.GameStateEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class GameOverAction extends BaseAction {


    protected GameOverAction(GameLogicHandler gameLogicHandler, TimeoutService timeoutService, GameStateEventPublisher eventPublisher) {
        super(gameLogicHandler, timeoutService, eventPublisher);
    }

    @Override
    public void execute(StateContext<GameState, GameEvent> context) {
        // TODO
        // Broadcast the final game state.
//        GameInfo gameOverInfo = new GameInfo("Server", "Server");
//        gameOverInfo.setUpdateType(UpdateType.GAME_ENDED);
//        gameOverInfo.setWinningPlayers(getServerGame(context).getPlayersCopy());
//        ClientHandler.updateAllClients(gameOverInfo);
        System.out.println("Game over.");
    }
}
