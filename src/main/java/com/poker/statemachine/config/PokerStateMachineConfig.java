package com.poker.statemachine.config;

import com.poker.enumeration.GameEvent;
import com.poker.enumeration.GameState;
import com.poker.statemachine.actions.impl.*;
import com.poker.statemachine.guards.impl.*;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@AllArgsConstructor
@EnableStateMachineFactory
public class PokerStateMachineConfig extends StateMachineConfigurerAdapter<GameState, GameEvent> {

    // ACTIONS
    private final RoundStartAction roundInitializationAction;
    private final RoundEndAction roundEndAction;
    private final GameOverAction gameOverAction;
    private final AssignFirstPlayerTurnAction assignFirstPlayerTurnAction;
    private final AssignNextPlayerTurnAction assignNextPlayerTurnAction;
    private final ApplyPlayerMoveAction applyPlayerMoveAction;
    private final AdvanceRoundAction advanceRoundAction;
    private final PlayerTimeoutAction playerTimeoutAction;

    // GUARDS
    private final IsPlayerActionValidGuard isPlayerActionValidGuard;
    private final IsOnlyOnePlayerUnfoldedGuard isOnlyOnePlayerUnfoldedGuard;
    private final IsBettingFinishedGuard isBettingFinishedGuard;
    private final IsBettingNotFinishedGuard isBettingNotFinishedGuard;
    private final IsRoundFinishedGuard isRoundFinishedGuard;
    private final IsRoundNotFinishedGuard isRoundNotFinishedGuard;
    private final IsPlayerCountInvalidGuard isPlayerCountInvalidGuard;

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
                .initial(GameState.ASSIGNING_FIRST_TURN)
                .state(GameState.ASSIGNING_FIRST_TURN, assignFirstPlayerTurnAction)
                .state(GameState.ASSIGNING_NEXT_TURN, assignNextPlayerTurnAction)
                .state(GameState.APPLYING_PLAYER_ACTION, applyPlayerMoveAction)
                .state(GameState.ADVANCING_ROUND_STATE, advanceRoundAction)
                .state(GameState.PROCESSING_PLAYER_TIMEOUT, playerTimeoutAction)
                .choice(GameState.EVALUATE_PLAYER_ACTION_RESULT);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<GameState, GameEvent> transitions) throws Exception {
        configureMainTransitions(transitions);
        configureBettingSubstateTransitions(transitions);
        configureDisconnectTransitions(transitions);
    }

    // Configures main game transitions
    private void configureMainTransitions(StateMachineTransitionConfigurer<GameState, GameEvent> transitions) throws Exception {
        transitions
                .withExternal()
                    .source(GameState.WAITING_FOR_HOST)
                    .target(GameState.ROUND_START)
                    .event(GameEvent.HOST_IS_READY)
                .and()
                .withExternal()
                    .source(GameState.ROUND_START)
                    .target(GameState.BETTING)
                    .event(GameEvent.ROUND_STARTED)
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
                .source(GameState.ASSIGNING_FIRST_TURN)
                .target(GameState.APPLYING_PLAYER_ACTION)
                .event(GameEvent.PLAYER_ACTION_RECEIVED)
                .guard(isPlayerActionValidGuard)
                .and()

                // Assign first turn -> Move to "Apply player move" (after player moves)
                .withExternal()
                .source(GameState.ASSIGNING_FIRST_TURN)
                .target(GameState.PROCESSING_PLAYER_TIMEOUT)
                .event(GameEvent.PLAYER_TURN_TIMED_OUT)
                .and()

                // Assign turn to the next player -> Move to "Apply player move" (after player moves)
                .withExternal()
                .source(GameState.ASSIGNING_NEXT_TURN)
                .target(GameState.APPLYING_PLAYER_ACTION)
                .event(GameEvent.PLAYER_ACTION_RECEIVED)
                .guard(isPlayerActionValidGuard)
                .and()

                .withExternal()
                .source(GameState.ASSIGNING_NEXT_TURN)
                .target(GameState.PROCESSING_PLAYER_TIMEOUT)
                .event(GameEvent.PLAYER_TURN_TIMED_OUT)
                .and()

                .withExternal()
                .source(GameState.PROCESSING_PLAYER_TIMEOUT)
                .target(GameState.EVALUATE_PLAYER_ACTION_RESULT)
                .event(GameEvent.PLAYER_TIMEOUT_PROCESSED)
                .and()

                // 1. Trigger choice based on PLAYER_ACTION_APPLIED
                .withExternal()
                .source(GameState.APPLYING_PLAYER_ACTION)
                .target(GameState.EVALUATE_PLAYER_ACTION_RESULT)
                .event(GameEvent.PLAYER_ACTION_APPLIED)
                .and()

                // 2. Define choice logic for EVALUATE_PLAYER_ACTION_RESULT
                .withChoice()
                .source(GameState.EVALUATE_PLAYER_ACTION_RESULT)
                .first(GameState.ROUND_END, isOnlyOnePlayerUnfoldedGuard)   // If all player but 1 player has folded then move to SHOWDOWN
                .then(GameState.ASSIGNING_NEXT_TURN, isBettingNotFinishedGuard)  // Player places a bet -> Move back to "Assign Next Player Turn" (if more players need to bet)
                .then(GameState.ADVANCING_ROUND_STATE, isBettingFinishedGuard) // If all players have bet or folded, move to "Advance Round" e.g. turn, river
                .last(GameState.ADVANCING_ROUND_STATE) // Fallback if none match
                .and()

                // Advance round logic -> Either give next player turn or move to ROUND_END e.g. showdown
                .withExternal()
                .source(GameState.ADVANCING_ROUND_STATE)
                .target(GameState.ASSIGNING_NEXT_TURN)
                .event(GameEvent.ROUND_STATE_ADVANCED)
                .guard(isRoundNotFinishedGuard)
                .and()

                // If betting is finished, move to ROUND_END
                .withExternal()
                .source(GameState.ADVANCING_ROUND_STATE)
                .target(GameState.ROUND_END)
                .event(GameEvent.ROUND_STATE_ADVANCED)
                .guard(isRoundFinishedGuard);
    }

    private void configureDisconnectTransitions(StateMachineTransitionConfigurer<GameState, GameEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(GameState.ROUND_START)
                .target(GameState.GAME_OVER)
                .event(GameEvent.PLAYER_DISCONNECTED)
                .guard(isPlayerCountInvalidGuard)
                .and()

                .withExternal()
                .source(GameState.BETTING)
                .target(GameState.GAME_OVER)
                .event(GameEvent.PLAYER_DISCONNECTED)
                .guard(isPlayerCountInvalidGuard)
                .and()

                .withExternal()
                .source(GameState.ROUND_END)
                .target(GameState.GAME_OVER)
                .event(GameEvent.PLAYER_DISCONNECTED)
                .guard(isPlayerCountInvalidGuard);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<GameState, GameEvent> config) throws Exception {
        config
                .withConfiguration()
                .autoStartup(false);
    }
}
