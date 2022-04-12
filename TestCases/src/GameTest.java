import static org.junit.Assert .*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

public class GameTest {


    private static Game game;
    private static ArrayList<Player> players;
    private static int buyIn;
    private static int smallBlind;
    private static int bigBlind;

    @Before
    public void setup() {
        smallBlind = 100;
        bigBlind = smallBlind * 2;
        buyIn = 10000;

        players = new ArrayList<>();
        players.add(new Player("Bob", "123"));
        players.add(new Player("Jim", "234"));
        players.add(new Player("Aisha", "435"));
        players.add(new Player("Tom", "654"));

        for (Player player : players) {
            player.set_chips(buyIn);
        }

        game = new Game(players, smallBlind);
    }


    @Test
    public void assignRolesTest() {
        game.assignRoles();
        boolean isDealerSet = false;
        boolean isSmallBlindSet = false;
        boolean isBigBlindSet = false;

        for (Player player: game.getPlayers()) {
            if (player.getRole() == PokerRole.DEALER) {
                isDealerSet = true;
            }
            else if (player.getRole() == PokerRole.SMALL_BLIND) {
                isSmallBlindSet = true;
            }
            else if (player.getRole() == PokerRole.BIG_BLIND) {
                isBigBlindSet = true;
            }
        }

        assert(isDealerSet && isSmallBlindSet && isBigBlindSet);
    }

    @Test
    public void assignCardsTest() {
        game.assignCards();
        for (Player player : game.getPlayers()) {
            assert player.getHand().size() == 2;
        }
        assert (true);
    }

    @Test
    public void assignFirstTurnTest() {
        game.assignRoles();
        game.assignFirstTurn();
        int index = 0;
        int turnIndex = 0;
        int numOfPlayersWithTurn = 0;
        for (Player player : game.getPlayers()) {
            if (player.hasTurn()) {
                turnIndex = index;
                numOfPlayersWithTurn++;
            }
            index++;
        }

        // asserts if only one player has the turn
        assert (numOfPlayersWithTurn == 1);

        // asserts if the player with first turn is on the right of the big blind
        if (turnIndex <= 0) {
            assert (game.getPlayers().get(players.size() - 1).getRole() == PokerRole.BIG_BLIND);
        }
        else {
            assert (game.getPlayers().get(turnIndex - 1).getRole() == PokerRole.BIG_BLIND);
        }
    }

    @Test
    public void takeChipsFromBlindsTest() {
        game.assignRoles();
        game.takeChipsFromBlinds();

        for (Player player : game.getPlayers()) {
            int playerChips = player.getChips();
            if (player.getRole() == PokerRole.SMALL_BLIND) {
                assert (playerChips == buyIn - smallBlind);
            }
            else if (player.getRole() == PokerRole.BIG_BLIND) {
                assert (playerChips == buyIn - bigBlind);
            }
            else {
                assert (playerChips == buyIn);
            }
        }
    }

    // Checks if bet gets recorded only to that player (and does not affect other players). Also checks if the
    // total pot is updated
    @Test
    public void performBetByPlayerTest() {
        game.initializeRound();
        int raiseAmount = smallBlind * 5;
        Player better = players.get(1);
        game.performBetByPlayer(players.get(1), raiseAmount);
        HashMap<Player, Integer> playerBettings = game.getPlayerBettings();

        assert (playerBettings.containsKey(better));

        for (Player player: playerBettings.keySet()) {
            if (player.equals(better)) {
                assert (player.getChips() == buyIn - raiseAmount);
                assert (playerBettings.get(player) == raiseAmount);
            }
            else if (player.getRole() == PokerRole.SMALL_BLIND) {
                assert (player.getChips() == buyIn - smallBlind);
                assert (playerBettings.get(player) == smallBlind);
            }
            else if (player.getRole() == PokerRole.BIG_BLIND) {
                assert (player.getChips() == buyIn - bigBlind);
                assert (playerBettings.get(player) == bigBlind);
            }
            else {
                assert (player.getChips() == buyIn);
                assert (playerBettings.get(player) == 0);
            }
        }
        assert (game.getTotalPot() == smallBlind + bigBlind + raiseAmount);
    }

    @Test
    public void performCallByPlayerTest() {
        game.initializeRound();
        Player smallBlindPlayer = game.getPlayersWithRoles().get(PokerRole.SMALL_BLIND);
        game.performCallByPlayer(smallBlindPlayer);

        HashMap<Player, Integer> playerBettings = game.getPlayerBettings();
        assert (playerBettings.containsKey(smallBlindPlayer));

        for (Player player: playerBettings.keySet()) {
            if (player.equals(smallBlindPlayer)) {
                assert (player.getChips() == buyIn - bigBlind);
                assert (playerBettings.get(player) == bigBlind);
            }
            else if (player.getRole() == PokerRole.SMALL_BLIND) {
                assert (player.getChips() == buyIn - smallBlind);
                assert (playerBettings.get(player) == smallBlind);
            }
            else if (player.getRole() == PokerRole.BIG_BLIND) {
                assert (player.getChips() == buyIn - bigBlind);
                assert (playerBettings.get(player) == bigBlind);
            }
            else {
                assert (player.getChips() == buyIn);
                assert (playerBettings.get(player) == 0);
            }
        }
        assert (game.getTotalPot() == 2 * bigBlind);
    }

    @Test
    public void removeLosersTest() {
        Player loser1 = new Player("Loser", "100");
        Player loser2 = new Player("Loser", "200");
        Player loser3 = new Player("Loser", "300");
        Player winner1 = new Player("Winner", "1210");
        Player winner2 = new Player("Winner", "1220");

        loser1.set_chips(0);
        loser2.set_chips(0);
        loser3.set_chips(0);
        winner1.set_chips(buyIn);
        winner2.set_chips(buyIn);

        ArrayList<Player> players = new ArrayList<>();
        players.add(loser1);
        players.add(loser2);
        players.add(loser3);
        players.add(winner1);
        players.add(winner2);

        game = new Game(players, smallBlind);
        game.removeLosers();

        ArrayList<Player> expectedPlayers = new ArrayList<>();
        expectedPlayers.add(winner1);
        expectedPlayers.add(winner2);

        ArrayList<Player> actualPlayers = game.getPlayers();

        assertEquals(actualPlayers, expectedPlayers);
    }

    @Test
    public void getPlayersWithNoChipsTest() {
        Player loser1 = new Player("Loser", "100");
        Player loser2 = new Player("Loser", "200");
        Player loser3 = new Player("Loser", "300");
        Player winner1 = new Player("Winner", "1210");
        Player winner2 = new Player("Winner", "1220");

        loser1.set_chips(0);
        loser2.set_chips(0);
        loser3.set_chips(0);
        winner1.set_chips(buyIn);
        winner2.set_chips(buyIn);

        ArrayList<Player> players = new ArrayList<>();
        players.add(loser1);
        players.add(loser2);
        players.add(loser3);
        players.add(winner1);
        players.add(winner2);

        game = new Game(players, smallBlind);

        ArrayList<Player> expectedPlayers = new ArrayList<>();
        expectedPlayers.add(loser1);
        expectedPlayers.add(loser2);
        expectedPlayers.add(loser3);

        ArrayList<Player> actualPlayers = game.getPlayersWithNoChips();

        assertEquals(actualPlayers, expectedPlayers);
    }

    @Test
    public void endRoundTest() {
        game.initializeRound();
        game.endRound();

        for (Player player: game.getPlayers()) {
            assertFalse(player.isFolded());
        }

        HashMap<Player, Integer> playerBettings = game.getPlayerBettings();
        for (Player player: playerBettings.keySet()) {
            assertEquals(0, (int) playerBettings.get(player));
        }

        assertEquals(0, game.getTableCards().size());
        assertEquals(0, game.getTotalPot());

        for (Player player: game.getPlayers()) {
            assertEquals(0, player.getHand().size());
        }
    }

    @Test
    public void isEveryBetTheSameTest() {
        game.initializeRound();
        assertFalse(game.isEveryBetTheSame());

        for (Player player: game.getPlayers()) {
            game.performCallByPlayer(player);
        }
        assertTrue(game.isEveryBetTheSame());

        game.performBetByPlayer(players.get(0), game.getMinimumBetAmount());
        assertFalse(game.isEveryBetTheSame());
    }

    @Test
    public void applyPlayerAction() {
        game.initializeRound();
        Player folder = game.getPlayer(players.get(0));
        Player better = game.getPlayer(players.get(1));
        Player caller = game.getPlayer(players.get(2));

        game.applyPlayerAction(folder, PlayerAction.FOLD, 0);

        int raiseToAmount = game.getMinimumBetAmount();
        game.applyPlayerAction(better, PlayerAction.BET, raiseToAmount);

        int callAmount = game.getMinimumCallAmount();
        game.applyPlayerAction(caller, PlayerAction.CALL, callAmount);

        HashMap<Player, Integer> playerBettings = game.getPlayerBettings();

        assertTrue(folder.isFolded());
        assertEquals(raiseToAmount, (int) playerBettings.get(better));
        assertEquals(callAmount, (int) playerBettings.get(caller));
    }

    @Test
    public void hasEveryoneHadATurnTest() {
        game.initializeRound();

        assertFalse(game.hasEveryoneHadATurn());
        for (Player player: players) {
            game.applyPlayerAction(player, PlayerAction.FOLD, 0);
        }
        assertTrue(game.hasEveryoneHadATurn());
    }

    @Test
    public void isRoundStateOverTest() {
        game.initializeRound();

        assertFalse(game.isRoundStateOver());
        for (Player player: players) {
            game.applyPlayerAction(player, PlayerAction.CALL, 0);
        }
        assertTrue(game.isRoundStateOver());

        game.applyPlayerAction(players.get(0), PlayerAction.BET, game.getMinimumBetAmount());
        assertFalse(game.isRoundStateOver());
    }

    @Test
    public void everyoneIsAllInTest() {
        game.initializeRound();
        assertFalse(game.everyoneIsAllIn());
        game.performBetByPlayer(players.get(0), buyIn);
        for (Player player: players) {
            game.performCallByPlayer(player);
        }
        assertTrue(game.everyoneIsAllIn());
    }

    @Test
    public void getPlayerWithTurnTest() {
        game.initializeRound();
        Player bigBlindPlayer = game.getPlayersWithRoles().get(PokerRole.BIG_BLIND);
        ArrayList<Player> players = game.getPlayers();

        int expectedPlayerWithTurnIndex = (players.indexOf(bigBlindPlayer) + 1) % players.size();
        Player expectedPlayerWithTurn = players.get(expectedPlayerWithTurnIndex);
        Player actualPlayerWithTurn = game.getPlayerWithTurn();
        assertEquals(expectedPlayerWithTurn, actualPlayerWithTurn);

        game.giveNextPlayerTurn();
        expectedPlayerWithTurnIndex = (expectedPlayerWithTurnIndex+ 1) % players.size();
        expectedPlayerWithTurn = players.get(expectedPlayerWithTurnIndex);
        actualPlayerWithTurn = game.getPlayerWithTurn();
        assertEquals(expectedPlayerWithTurn, actualPlayerWithTurn);

    }

    @Test
    public void giveNextPlayerTurnTest() {
        game.initializeRound();
        Player bigBlindPlayer = game.getPlayersWithRoles().get(PokerRole.BIG_BLIND);
        ArrayList<Player> players = game.getPlayers();

        int expectedPlayerWithTurnIndex = (players.indexOf(bigBlindPlayer) + 1) % players.size();

        for (int i = 0; i < players.size(); i++) {
            expectedPlayerWithTurnIndex = (expectedPlayerWithTurnIndex + i) % players.size();
            Player expectedPlayerWithTurn = players.get(expectedPlayerWithTurnIndex);
            Player actualPlayerWithTurn = game.getPlayerWithTurn();
            assertEquals(expectedPlayerWithTurn, actualPlayerWithTurn);
            game.giveNextPlayerTurn();
        }

    }
//
//    @Test(expected = RuntimeException.class)
//    public void negativeSizeTest() {
//        // THIS TEST SHOULD FAIL BECAUSE EXCEPTION IS NOT THROWN
//        box = new Box3D(new int[]{1, 2, 3}, new int[]{-1, 2, 3});
//    }
}
