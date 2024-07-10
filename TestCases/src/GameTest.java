//import static org.junit.Assert .*;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.main.util.ArrayList;
//import java.main.util.Arrays;
//import java.main.util.HashMap;
//import java.main.util.HashSet;
//
//public class GameTest {
//
//
//    private static Game main.model.game;
//    private static ArrayList<Player> players;
//    private static int buyIn;
//    private static int smallBlind;
//    private static int bigBlind;
//
//    @Before
//    public void setup() {
//        smallBlind = 100;
//        bigBlind = smallBlind * 2;
//        buyIn = 10000;
//
//        players = new ArrayList<>();
//        players.add(new Player("Bob", "123"));
//        players.add(new Player("Jim", "234"));
//        players.add(new Player("Aisha", "435"));
//        players.add(new Player("Tom", "654"));
//
//        for (Player player : players) {
//            player.set_chips(buyIn);
//        }
//
//        main.model.game = new Game(players, smallBlind);
//    }
//
//
//    @Test
//    public void assignRolesTest() {
//        main.model.game.assignRoles();
//        boolean isDealerSet = false;
//        boolean isSmallBlindSet = false;
//        boolean isBigBlindSet = false;
//
//        for (Player player: main.model.game.getPlayers()) {
//            if (player.getRole() == PokerRole.DEALER) {
//                isDealerSet = true;
//            }
//            else if (player.getRole() == PokerRole.SMALL_BLIND) {
//                isSmallBlindSet = true;
//            }
//            else if (player.getRole() == PokerRole.BIG_BLIND) {
//                isBigBlindSet = true;
//            }
//        }
//
//        assert(isDealerSet && isSmallBlindSet && isBigBlindSet);
//    }
//
//    @Test
//    public void assignCardsTest() {
//        main.model.game.assignCards();
//        for (Player player : main.model.game.getPlayers()) {
//            assert player.getHand().size() == 2;
//        }
//        assert (true);
//    }
//
//    @Test
//    public void assignFirstTurnTest() {
//        main.model.game.assignRoles();
//        main.model.game.assignFirstTurn();
//        int index = 0;
//        int turnIndex = 0;
//        int numOfPlayersWithTurn = 0;
//        for (Player player : main.model.game.getPlayers()) {
//            if (player.hasTurn()) {
//                turnIndex = index;
//                numOfPlayersWithTurn++;
//            }
//            index++;
//        }
//
//        // asserts if only one player has the turn
//        assert (numOfPlayersWithTurn == 1);
//
//        // asserts if the player with first turn is on the right of the big blind
//        if (turnIndex <= 0) {
//            assert (main.model.game.getPlayers().get(players.size() - 1).getRole() == PokerRole.BIG_BLIND);
//        }
//        else {
//            assert (main.model.game.getPlayers().get(turnIndex - 1).getRole() == PokerRole.BIG_BLIND);
//        }
//    }
//
//    @Test
//    public void takeChipsFromBlindsTest() {
//        main.model.game.assignRoles();
//        main.model.game.takeChipsFromBlinds();
//
//        for (Player player : main.model.game.getPlayers()) {
//            int playerChips = player.getChips();
//            if (player.getRole() == PokerRole.SMALL_BLIND) {
//                assert (playerChips == buyIn - smallBlind);
//            }
//            else if (player.getRole() == PokerRole.BIG_BLIND) {
//                assert (playerChips == buyIn - bigBlind);
//            }
//            else {
//                assert (playerChips == buyIn);
//            }
//        }
//    }
//
//    // Checks if bet gets recorded only to that player (and does not affect other players). Also checks if the
//    // total pot is updated
//    @Test
//    public void performBetByPlayerTest() {
//        main.model.game.startGame();
//        main.model.game.initializeRound();
//        int raiseAmount = smallBlind * 5;
//        Player better = players.get(1);
//        main.model.game.performBetByPlayer(players.get(1), raiseAmount);
//        HashMap<Player, Integer> playerBettings = main.model.game.getPlayerBettings();
//
//        assert (playerBettings.containsKey(better));
//
//        for (Player player: playerBettings.keySet()) {
//            if (player.equals(better)) {
//                assert (player.getChips() == buyIn - raiseAmount);
//                assert (playerBettings.get(player) == raiseAmount);
//            }
//            else if (player.getRole() == PokerRole.SMALL_BLIND) {
//                assert (player.getChips() == buyIn - smallBlind);
//                assert (playerBettings.get(player) == smallBlind);
//            }
//            else if (player.getRole() == PokerRole.BIG_BLIND) {
//                assert (player.getChips() == buyIn - bigBlind);
//                assert (playerBettings.get(player) == bigBlind);
//            }
//            else {
//                assert (player.getChips() == buyIn);
//                assert (playerBettings.get(player) == 0);
//            }
//        }
//        assert (main.model.game.getTotalPot() == smallBlind + bigBlind + raiseAmount);
//    }
//
//    @Test
//    public void performCallByPlayerTest() {
//        main.model.game.startGame();
//        main.model.game.initializeRound();
//        Player smallBlindPlayer = main.model.game.getPlayersWithRoles().get(PokerRole.SMALL_BLIND);
//        main.model.game.performCallByPlayer(smallBlindPlayer);
//
//        HashMap<Player, Integer> playerBettings = main.model.game.getPlayerBettings();
//        assert (playerBettings.containsKey(smallBlindPlayer));
//
//        for (Player player: playerBettings.keySet()) {
//            if (player.equals(smallBlindPlayer)) {
//                assert (player.getChips() == buyIn - bigBlind);
//                assert (playerBettings.get(player) == bigBlind);
//            }
//            else if (player.getRole() == PokerRole.SMALL_BLIND) {
//                assert (player.getChips() == buyIn - smallBlind);
//                assert (playerBettings.get(player) == smallBlind);
//            }
//            else if (player.getRole() == PokerRole.BIG_BLIND) {
//                assert (player.getChips() == buyIn - bigBlind);
//                assert (playerBettings.get(player) == bigBlind);
//            }
//            else {
//                assert (player.getChips() == buyIn);
//                assert (playerBettings.get(player) == 0);
//            }
//        }
//        assert (main.model.game.getTotalPot() == 2 * bigBlind);
//    }
//
//    private Game getNewGameWithLosers() {
//        Player loser1 = new Player("Loser", "100");
//        Player loser2 = new Player("Loser", "200");
//        Player loser3 = new Player("Loser", "300");
//        Player winner1 = new Player("Winner", "1210");
//        Player winner2 = new Player("Winner", "1220");
//
//        loser1.set_chips(0);
//        loser2.set_chips(0);
//        loser3.set_chips(0);
//        winner1.set_chips(buyIn);
//        winner2.set_chips(buyIn);
//
//        ArrayList<Player> players = new ArrayList<>();
//        players.add(loser1);
//        players.add(loser2);
//        players.add(loser3);
//        players.add(winner1);
//        players.add(winner2);
//
//        return new Game(players, smallBlind);
//
//    }
//    @Test
//    public void removeLosersTest() {
//        main.model.game = getNewGameWithLosers();
//        main.model.game.removeLosers();
//
//        ArrayList<Player> expectedPlayers = new ArrayList<>();
//        for (Player player: main.model.game.getPlayers()) {
//            if (player.getChips() > 0) {
//                expectedPlayers.add(player);
//            }
//        }
//
//        ArrayList<Player> actualPlayers = main.model.game.getPlayers();
//        assertEquals(actualPlayers, expectedPlayers);
//    }
//
//    @Test
//    public void getPlayersWithNoChipsTest() {
//        main.model.game = getNewGameWithLosers();
//
//        ArrayList<Player> expectedPlayers = new ArrayList<>();
//        for (Player player: main.model.game.getPlayers()) {
//            if (player.getChips() <= 0) {
//                expectedPlayers.add(player);
//            }
//        }
//        ArrayList<Player> actualPlayers = main.model.game.getPlayersWithNoChips();
//        assertEquals(actualPlayers, expectedPlayers);
//    }
//
//    @Test
//    public void endRoundTest() {
//        main.model.game.startGame();
//        main.model.game.initializeRound();
//        main.model.game.endRound();
//
//        for (Player player: main.model.game.getPlayers()) {
//            assertFalse(player.isFolded());
//        }
//
//        HashMap<Player, Integer> playerBettings = main.model.game.getPlayerBettings();
//        for (Player player: playerBettings.keySet()) {
//            assertEquals(0, (int) playerBettings.get(player));
//        }
//
//        assertEquals(0, main.model.game.getTableCards().size());
//        assertEquals(0, main.model.game.getTotalPot());
//
//        for (Player player: main.model.game.getPlayers()) {
//            assertEquals(0, player.getHand().size());
//        }
//    }
//
//    @Test
//    public void isEveryBetTheSameTest() {
//        main.model.game.startGame();
//        main.model.game.initializeRound();
//        assertFalse(main.model.game.isEveryBetTheSame());
//
//        for (Player player: main.model.game.getPlayers()) {
//            main.model.game.performCallByPlayer(player);
//        }
//        assertTrue(main.model.game.isEveryBetTheSame());
//
//        main.model.game.performBetByPlayer(players.get(0), main.model.game.getMinimumBetAmount());
//        assertFalse(main.model.game.isEveryBetTheSame());
//    }
//
//    @Test
//    public void applyPlayerAction() {
//        main.model.game.startGame();
//        main.model.game.initializeRound();
//        Player folder = main.model.game.getPlayer(players.get(0));
//        Player better = main.model.game.getPlayer(players.get(1));
//        Player caller = main.model.game.getPlayer(players.get(2));
//
//        main.model.game.applyPlayerAction(folder, PlayerAction.FOLD, 0);
//
//        int raiseToAmount = main.model.game.getMinimumBetAmount();
//        main.model.game.applyPlayerAction(better, PlayerAction.BET, raiseToAmount);
//
//        int callAmount = main.model.game.getMinimumCallAmount();
//        main.model.game.applyPlayerAction(caller, PlayerAction.CALL, callAmount);
//
//        HashMap<Player, Integer> playerBettings = main.model.game.getPlayerBettings();
//
//        assertTrue(folder.isFolded());
//        assertEquals(raiseToAmount, (int) playerBettings.get(better));
//        assertEquals(callAmount, (int) playerBettings.get(caller));
//    }
//
//    @Test
//    public void hasEveryoneHadATurnTest() {
//        main.model.game.startGame();
//        main.model.game.initializeRound();
//
//        assertFalse(main.model.game.hasEveryoneHadATurn());
//        for (Player player: players) {
//            main.model.game.applyPlayerAction(player, PlayerAction.FOLD, 0);
//        }
//        assertTrue(main.model.game.hasEveryoneHadATurn());
//    }
//
//    @Test
//    public void isRoundStateOverTest() {
//        main.model.game.startGame();
//        main.model.game.initializeRound();
//
//        assertFalse(main.model.game.isRoundStateOver());
//        for (Player player: players) {
//            main.model.game.applyPlayerAction(player, PlayerAction.CALL, 0);
//        }
//        assertTrue(main.model.game.isRoundStateOver());
//
//        main.model.game.applyPlayerAction(players.get(0), PlayerAction.BET, main.model.game.getMinimumBetAmount());
//        assertFalse(main.model.game.isRoundStateOver());
//    }
//
//    @Test
//    public void everyoneIsAllInTest() {
//        main.model.game.startGame();
//        main.model.game.initializeRound();
//        assertFalse(main.model.game.everyoneIsAllIn());
//        main.model.game.performBetByPlayer(players.get(0), buyIn);
//        for (Player player: players) {
//            main.model.game.performCallByPlayer(player);
//        }
//        assertTrue(main.model.game.everyoneIsAllIn());
//    }
//
//    @Test
//    public void getPlayerWithTurnTest() {
//        main.model.game.startGame();
//        main.model.game.initializeRound();
//        Player bigBlindPlayer = main.model.game.getPlayersWithRoles().get(PokerRole.BIG_BLIND);
//        ArrayList<Player> players = main.model.game.getPlayers();
//
//        int expectedPlayerWithTurnIndex = (players.indexOf(bigBlindPlayer) + 1) % players.size();
//        Player expectedPlayerWithTurn = players.get(expectedPlayerWithTurnIndex);
//        Player actualPlayerWithTurn = main.model.game.getPlayerWithTurn();
//        assertEquals(expectedPlayerWithTurn, actualPlayerWithTurn);
//
//        main.model.game.giveNextPlayerTurn();
//        expectedPlayerWithTurnIndex = (expectedPlayerWithTurnIndex+ 1) % players.size();
//        expectedPlayerWithTurn = players.get(expectedPlayerWithTurnIndex);
//        actualPlayerWithTurn = main.model.game.getPlayerWithTurn();
//        assertEquals(expectedPlayerWithTurn, actualPlayerWithTurn);
//
//    }
//
//    @Test
//    public void giveNextPlayerTurnTest() {
//        main.model.game.startGame();
//        main.model.game.initializeRound();
//        Player bigBlindPlayer = main.model.game.getPlayersWithRoles().get(PokerRole.BIG_BLIND);
//        ArrayList<Player> players = main.model.game.getPlayers();
//
//
//        for (int i = 0; i < players.size(); i++) {
//            int expectedPlayerWithTurnIndex = (players.indexOf(bigBlindPlayer) + 1 + i) % players.size();
//            Player expectedPlayerWithTurn = players.get(expectedPlayerWithTurnIndex);
//            Player actualPlayerWithTurn = main.model.game.getPlayerWithTurn();
//            assertEquals(expectedPlayerWithTurn, actualPlayerWithTurn);
//            main.model.game.giveNextPlayerTurn();
//        }
//    }
//
//    @Test
//    public void setPlayerRolesTest() {
//        HashMap<PokerRole, Player> expectedPlayerRoles = new HashMap<>();
//        expectedPlayerRoles.put(PokerRole.DEALER, players.get(0));
//        expectedPlayerRoles.put(PokerRole.SMALL_BLIND, players.get(1));
//        expectedPlayerRoles.put(PokerRole.BIG_BLIND, players.get(2));
//
//        main.model.game.setPlayerRoles(expectedPlayerRoles);
//        HashMap<PokerRole, Player> actualPlayerRoles = main.model.game.getPlayersWithRoles();
//
//        assertEquals(expectedPlayerRoles, actualPlayerRoles);
//        for (PokerRole role: actualPlayerRoles.keySet()) {
//            Player player =  actualPlayerRoles.get(role);
//            assertEquals(role,player.getRole());
//        }
//    }
//
//    @Test
//    public void advanceRoundStateTest() {
//        main.model.game.startGame();
//        main.model.game.initializeRound();
//
//        while (!main.model.game.getRoundState().equals(RoundState.SHOWDOWN)) {
//            RoundState expectedNextRoundState = RoundState.getNextRoundState(main.model.game.getRoundState());
//            main.model.game.advanceRoundState();
//            RoundState actualNextRoundState = main.model.game.getRoundState();
//
//            assertEquals(expectedNextRoundState, actualNextRoundState);
//        }
//    }
//
//    @Test
//    public void updateTableCardsTest() {
//        main.model.game.startGame();
//        main.model.game.initializeRound();
//
//        int expectedTableCards = 3;
//        while (!main.model.game.getRoundState().equals(RoundState.SHOWDOWN)) {
//            main.model.game.advanceRoundState();
//            assertEquals(expectedTableCards, main.model.game.getTableCards().size());
//
//            if (!main.model.game.getRoundState().equals(RoundState.RIVER)) {
//                expectedTableCards++;
//            }
//        }
//    }
//
//    private ArrayList<Card> getArbitraryTableCards() {
//        ArrayList<Card> tableCards = new ArrayList<>();
//        tableCards.add(new Card(1, 5));
//        tableCards.add(new Card(4, 9));
//        tableCards.add(new Card(1, 6));
//        tableCards.add(new Card(2, 10));
//        tableCards.add(new Card(1, 14));
//        return tableCards;
//
//    }
//
//    @Test
//    public void getScoreTest() {
//        Player player = new Player("Player", "123");
//        player.addToHand(new Card(2, 2));
//        player.addToHand(new Card(3, 2));
//
//        ArrayList<Card> tableCards = getArbitraryTableCards();
//        main.model.game.setTableCards(tableCards);
//
//        int[] expectedScore = new HandEval().evaluate(player.getHand(), tableCards);
//        int[] actualScore = main.model.game.getScore(player);
//
//        assertArrayEquals(expectedScore, actualScore);
//    }
//
//
//    @Test
//    public void getPlayerHandNameTest() {
//        Player player = new Player("Player", "123");
//        player.addToHand(new Card(2, 2));
//        player.addToHand(new Card(3, 2));
//        main.model.game.setTableCards(getArbitraryTableCards());
//
//        String expectedName = HandEval.getHandName(main.model.game.getScore(player));
//        String actualName = main.model.game.getPlayerHandName(player);
//
//        assertEquals(expectedName, actualName);
//    }
//
//    @Test
//    public void getHighestScoreTest() {
//        main.model.game.setTableCards(getArbitraryTableCards());
//
//        int[] expectedHighestScore = {0, 0};
//        for (Player player: main.model.game.getPlayers()) {
//            int[] playerScore = main.model.game.getScore(player);
//            if (Arrays.compare(playerScore, expectedHighestScore) > 0) {
//                expectedHighestScore = playerScore;
//            }
//        }
//        int[] actualHighestScore = main.model.game.getHighestScore();
//
//        assertArrayEquals(expectedHighestScore, actualHighestScore);
//    }
//
//    @Test
//    public void getWinningPlayersTest() {
//        main.model.game.startGame();
//        main.model.game.initializeRound();
//        main.model.game.setTableCards(getArbitraryTableCards());
//
//        ArrayList<Player> expectedWinningPlayers = new ArrayList<>();
//        ArrayList<Player> actualWinningPlayers = main.model.game.getWinningPlayers();
//        for (Player player: main.model.game.getPlayers()) {
//            if (Arrays.equals(main.model.game.getScore(player), main.model.game.getHighestScore())) {
//                expectedWinningPlayers.add(player);
//            }
//        }
//
//        assertEquals(expectedWinningPlayers, actualWinningPlayers);
//    }
//
//    @Test
//    public void giveChipsToWinnersTest() {
//        main.model.game.startGame();
//        main.model.game.initializeRound();
//        main.model.game.setTableCards(getArbitraryTableCards());
//        int totalPot = main.model.game.getTotalPot();
//        ArrayList<Player> winners = main.model.game.getWinningPlayers();
//        int winningShare = totalPot/winners.size();
//        main.model.game.giveChipsToWinners();
//
//        for (Player player: winners) {
//            if (player.getRole() == PokerRole.SMALL_BLIND) {
//                assertEquals(player.getChips(), buyIn - smallBlind + winningShare);
//            }
//            else if (player.getRole() == PokerRole.BIG_BLIND) {
//                assertEquals(player.getChips(), buyIn - bigBlind + winningShare);
//            }
//            else {
//                assertEquals(player.getChips(), buyIn + winningShare);
//            }
//        }
//    }
//
//    @Test
//    public void allOtherPlayersFoldedTest() {
//        main.model.game.startGame();
//        main.model.game.initializeRound();
//
//        for (int i = 0; i < players.size() - 1; i++) {
//            main.model.game.applyPlayerAction(players.get(i), PlayerAction.FOLD, 0);
//        }
//
//        assertTrue(main.model.game.allOtherPlayersFolded());
//    }
//
//    @Test
//    public void giveChipsToLastPlayerTest() {
//        main.model.game.startGame();
//        main.model.game.initializeRound();
//        int totalPot = main.model.game.getTotalPot();
//
//        for (int i = 0; i < players.size() - 1; i++) {
//            main.model.game.applyPlayerAction(players.get(i), PlayerAction.FOLD, 0);
//        }
//        Player lastPlayer = main.model.game.getPlayer(players.get(players.size() - 1));
//
//        main.model.game.giveChipsToLastPlayer();
//
//        if (lastPlayer.getRole() == PokerRole.SMALL_BLIND) {
//            assertEquals(buyIn - smallBlind + totalPot, lastPlayer.getChips());
//        }
//        else if (lastPlayer.getRole() == PokerRole.BIG_BLIND) {
//            assertEquals(buyIn - bigBlind + totalPot, lastPlayer.getChips());
//        }
//        else {
//            assertEquals(buyIn + totalPot, lastPlayer.getChips());
//        }
//    }
//
//    @Test
//    public void getValidActionsTest() {
//        main.model.game.startGame();
//        main.model.game.initializeRound();
//
//        HashSet<PlayerAction> expectedValidActions = new HashSet<>();
//        expectedValidActions.add(PlayerAction.CALL);
//        expectedValidActions.add(PlayerAction.RAISE);
//        expectedValidActions.add(PlayerAction.FOLD);
//
//        HashSet<PlayerAction> actualValidActions = main.model.game.getValidActions(players.get(0));
//
//        for (Player player: players) {
//            main.model.game.performCallByPlayer(player);
//        }
//
//        main.model.game.endRoundState();
//        main.model.game.advanceRoundState();
//        actualValidActions = main.model.game.getValidActions(players.get(0));
//
//        expectedValidActions = new HashSet<>();
//        expectedValidActions.add(PlayerAction.CHECK);
//        expectedValidActions.add(PlayerAction.BET);
//        expectedValidActions.add(PlayerAction.FOLD);
//
//        assertEquals(expectedValidActions, actualValidActions);
//    }
//
////
////    @Test(expected = RuntimeException.class)
////    public void negativeSizeTest() {
////        // THIS TEST SHOULD FAIL BECAUSE EXCEPTION IS NOT THROWN
////        box = new Box3D(new int[]{1, 2, 3}, new int[]{-1, 2, 3});
////    }
//}
