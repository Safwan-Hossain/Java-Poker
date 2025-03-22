package com.poker.statemachine.config;

import com.poker.enumeration.GameEvent;
import com.poker.enumeration.GameState;
import com.poker.statemachine.actions.impl.*;
import com.poker.statemachine.guards.ApplyPlayerMoveGuard;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory
public class PokerStateMachineConfig extends StateMachineConfigurerAdapter<GameState, GameEvent> {

    private final Action<GameState, GameEvent> roundInitializationAction;
    private final Action<GameState, GameEvent> roundEndAction;
    private final Action<GameState, GameEvent> gameOverAction;
    private final Action<GameState, GameEvent> assignFirstPlayerTurnAction;
    private final Action<GameState, GameEvent> assignNextPlayerTurnAction;
    private final Action<GameState, GameEvent> applyPlayerMoveAction;
    private final Action<GameState, GameEvent> advanceRoundAction;
    private final Guard<GameState, GameEvent> applyPlayerMoveGuard;

    public PokerStateMachineConfig(
            RoundStartAction roundInitializationAction,
            RoundEndAction roundEndAction,
            GameOverAction gameOverAction,
            AssignFirstPlayerTurnAction assignFirstPlayerTurnAction,
            AssignNextPlayerTurnAction assignNextPlayerTurnAction,
            ApplyPlayerMoveAction applyPlayerMoveAction,
            AdvanceRoundAction advanceRoundAction,
            ApplyPlayerMoveGuard applyPlayerMoveGuard) {
        this.roundInitializationAction = roundInitializationAction;
        this.roundEndAction = roundEndAction;
        this.gameOverAction = gameOverAction;
        this.assignFirstPlayerTurnAction = assignFirstPlayerTurnAction;
        this.assignNextPlayerTurnAction = assignNextPlayerTurnAction;
        this.applyPlayerMoveAction = applyPlayerMoveAction;
        this.advanceRoundAction = advanceRoundAction;
        this.applyPlayerMoveGuard = applyPlayerMoveGuard;
    }

    @Override
    public void configure(StateMachineStateConfigurer<GameState, GameEvent> states) throws Exception {
        states.withStates()
                .initial(GameState.WAITING_FOR_HOST)
                .state(GameState.ROUND_START, roundInitializationAction)
                .state(GameState.BETTING) // No action here. Internal states will handle logic
                .state(GameState.ROUND_END, roundEndAction)
                .state(GameState.GAME_OVER, gameOverAction)
                .states(EnumSet.allOf(GameState.class));

        configureBettingSubstates(states);
    }

    // Configuration the BETTING substates
    private void configureBettingSubstates(StateMachineStateConfigurer<GameState, GameEvent> states) throws Exception {
        states.withStates()
                .parent(GameState.BETTING)
                .initial(GameState.BETTING_ASSIGN_FIRST_PLAYER_TURN)
                .state(GameState.BETTING_ASSIGN_FIRST_PLAYER_TURN, assignFirstPlayerTurnAction)
                .state(GameState.BETTING_ASSIGN_NEXT_PLAYER_TURN, assignNextPlayerTurnAction)
                .state(GameState.BETTING_APPLY_PLAYER_MOVE, applyPlayerMoveAction)
                .state(GameState.BETTING_ADVANCE_ROUND, advanceRoundAction);
    }


    @Override
    public void configure(StateMachineTransitionConfigurer<GameState, GameEvent> transitions) throws Exception {
        configureMainTransitions(transitions);
        configureBettingSubstateTransitions(transitions);
    }

    // Configures main game transitions
    private void configureMainTransitions(StateMachineTransitionConfigurer<GameState, GameEvent> transitions) throws Exception {
        transitions
                .withExternal()
                    .source(GameState.WAITING_FOR_HOST)
                    .target(GameState.ROUND_START)
                    .event(GameEvent.HOST_READY)
                .and()
                .withExternal()
                    .source(GameState.ROUND_START)
                    .target(GameState.BETTING)
                    .event(GameEvent.ROUND_INITIALIZED)
                .and()
                .withExternal()
                    .source(GameState.BETTING)
                    .target(GameState.ROUND_END)
                    .event(GameEvent.ROUND_END)
                .and()
                .withExternal()
                    .source(GameState.ROUND_END)
                    .target(GameState.ROUND_START)
                    .event(GameEvent.START_NEXT_ROUND)
                .and()
                    .withExternal()
                    .source(GameState.ROUND_END)
                    .target(GameState.GAME_OVER)
                    .event(GameEvent.GAME_OVER);
    }

    // Configures transitions within the BETTING state
    private void configureBettingSubstateTransitions(StateMachineTransitionConfigurer<GameState, GameEvent> transitions) throws Exception {
        transitions

                // Assign first turn -> Move to "Apply player move" (after player moves)
                .withExternal()
                .source(GameState.BETTING_ASSIGN_FIRST_PLAYER_TURN)
                .target(GameState.BETTING_APPLY_PLAYER_MOVE)
                .event(GameEvent.PLAYER_ACTION)
                .guard(applyPlayerMoveGuard) // Guard applied
                .and()

                // Assign turn to the next player -> Move to "Apply player move" (after player moves)
                .withExternal()
                .source(GameState.BETTING_ASSIGN_NEXT_PLAYER_TURN)
                .target(GameState.BETTING_APPLY_PLAYER_MOVE)
                .event(GameEvent.PLAYER_ACTION)
                .guard(applyPlayerMoveGuard) // Guard applied
                .and()

                // Player places a bet -> Move back to "Assign Next Player Turn" (if more players need to bet)
                .withExternal()
                .source(GameState.BETTING_APPLY_PLAYER_MOVE)
                .target(GameState.BETTING_ASSIGN_NEXT_PLAYER_TURN)
                .event(GameEvent.ASSIGN_NEXT_PLAYER_TURN)
                .and()

                // If all players have bet or folded, move to "Advance Round" e.g. turn, river
                .withExternal()
                .source(GameState.BETTING_APPLY_PLAYER_MOVE)
                .target(GameState.BETTING_ADVANCE_ROUND)
                .event(GameEvent.BETTING_IS_FINISHED)
                .and()

                // Advance round logic -> Either give next player turn or move to ROUND_END e.g. showdown
                .withExternal()
                .source(GameState.BETTING_ADVANCE_ROUND)
                .target(GameState.BETTING_ASSIGN_NEXT_PLAYER_TURN)
                .event(GameEvent.ASSIGN_NEXT_PLAYER_TURN)
                .and()

                // If betting is finished, move to ROUND_END
                .withExternal()
                .source(GameState.BETTING_ADVANCE_ROUND)
                .target(GameState.ROUND_END)
                .event(GameEvent.ROUND_END);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<GameState, GameEvent> config) throws Exception {
        config
                .withConfiguration()
                .autoStartup(false);
    }
}
