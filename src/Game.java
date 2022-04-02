import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Game implements Serializable {
    private final int MAX_HAND_SIZE;

    private int playerWithTurnIndex;
    private int dealerIndex;
    private int smallBlindIndex;
    private int bigBlindIndex;

    // counts how many players had a turn for the current round phase
    private int turnCounter;

    private int smallBlind;
    private int bigBlind;

    private int minimumCallAmount;
    private int minimumBetAmount;
    private int totalPot;

    private Deck deck;
    private ArrayList<Player> players;
    private ArrayList<Player> unfoldedPlayers; // players who haven't folded

    private ArrayList<Card> tableCards;
    private HashMap<Player, Integer> playerBettings;
    private Player lastBetter;

    private boolean hasGameStarted;
    private boolean hasGameEnded;
    private RoundState roundState;

    public Game(ArrayList<Player> players, int smallBlind) {
        this.MAX_HAND_SIZE = 2;

        //Sets up cards and players
        this.deck = new Deck();
        this.tableCards = new ArrayList<>();
        this.players = new ArrayList<>(players);
        this.playerBettings = new HashMap<>();
        for (Player player: players) {
            playerBettings.put(player, 0);
        }

        //Sets up chip values
        this.smallBlind = smallBlind;
        this.bigBlind = smallBlind * 2;
        this.totalPot = 0;

        this.dealerIndex = 0;

        this.hasGameStarted = false;
        this.hasGameEnded = false;
    }

    public boolean isGameOver() {
        return players.size() <= 1;
    }

    public void startGame() {
        deck.fillDeck();
        deck.shuffle();
        this.hasGameStarted = true;
    }

    public void initializeRound() {
        this.turnCounter = 0;
        this.totalPot = 0;
        this.minimumCallAmount = bigBlind;
        this.minimumBetAmount = bigBlind * 2;
        this.roundState = RoundState.PRE_FLOP;

        assignRoles();
        assignCards();
        assignFirstTurn();
        takeChipsFromBlinds();
    }

    public void takeChipsFromBlinds() {
        Player smallBlindPlayer = players.get(smallBlindIndex);
        Player bigBlindPlayer = players.get(bigBlindIndex);

        performBetByPlayer(smallBlindPlayer, smallBlind);
        performBetByPlayer(bigBlindPlayer, bigBlind);
    }

    public void assignRoles() {
        if (dealerIndex >= players.size()) {
            dealerIndex = dealerIndex % players.size();
        }

        players.get(dealerIndex).setRole(PokerRole.NONE);

        dealerIndex = (dealerIndex + 1) % players.size();
        smallBlindIndex = (dealerIndex + 1) % players.size();
        bigBlindIndex = (smallBlindIndex + 1) % players.size();

        players.get(dealerIndex).setRole(PokerRole.DEALER);
        players.get(smallBlindIndex).setRole(PokerRole.SMALL_BLIND);
        players.get(bigBlindIndex).setRole(PokerRole.BIG_BLIND);
    }

    public void assignCards() {
        for (Player player : players) {
            Card[] playerHand = deck.draw(2);
            for (Card card: playerHand) {
                player.addToHand(card);
            }
            if (player.getHand().size() > MAX_HAND_SIZE) {
                throw new RuntimeException("Player has more than " + MAX_HAND_SIZE + " cards.");
            }
        }
    }

    public void assignTurn() {
        playerWithTurnIndex = (bigBlindIndex + 1) % players.size();
        players.get(playerWithTurnIndex).setTurn(true);
    }

    // accounts for bets and raises
    public void performBetByPlayer(Player player, int raiseToAmount) {
        // Existing pool of chips player already put down (includes calls, raises, bets)
        int existingBet = playerBettings.get(player);
        int betAmount = raiseToAmount - existingBet;

        if (betAmount > player.getChips()) {
            betAmount = player.getChips();
            raiseToAmount = betAmount + existingBet;
        }

        if (raiseToAmount > minimumCallAmount) {
            minimumCallAmount = raiseToAmount;
            minimumBetAmount = raiseToAmount * 2;
        }

        player.takeChips(betAmount);
        totalPot += betAmount;
        playerBettings.put(player, raiseToAmount);
        lastBetter = player;
    }

    // accounts for checks and calls
    public void performCallByPlayer(Player player) {
        int actualCallAmount = minimumCallAmount - playerBettings.get(player);
        if (actualCallAmount > player.getChips()) {
            actualCallAmount = player.getChips();
        }
        player.takeChips(actualCallAmount);
        totalPot += actualCallAmount;
        int totalCallAmount = playerBettings.get(player) + actualCallAmount;
        playerBettings.put(player, totalCallAmount);
    }

    public void removeLosers() {
        for (Player player: getPlayersWithNoChips()) {
            players.remove(player);
        }
    }

    public void endRound() {
        playerBettings.replaceAll((player, bet) -> 0); // replaces all player bet amounts with 0
        tableCards.clear(); // empties table cards
        deck.reset();
        totalPot = 0;
        for (Player player: players) {
            player.getHand().clear();
        }
    }

    // Determines if everyone put in the same amount of money
    // Exception for players who went all-in below the call amount (due to insufficient funds).
    public boolean isEveryBetTheSame() {
        if (players.size() <= 1) {
            return true;
        }

        Set<Player> players = playerBettings.keySet();
        Player prevPlayer = null;

        for (Player currPlayer: players) {
            if (prevPlayer == null) {
                prevPlayer = currPlayer;
                continue;
            }

            int prevPlayerBet = playerBettings.get(prevPlayer);
            int currPlayerBet = playerBettings.get(currPlayer);

            // If the player who put in less chips is bankrupt, then they are all in and therefore the method should
            // not return false. Since they cannot afford to match.
            if (prevPlayerBet != currPlayerBet) {
                if (prevPlayerBet < currPlayerBet) {
                    if (!prevPlayer.isBankrupt()) {
                        return false;
                    }
                }
                else if (!currPlayer.isBankrupt()) {
                    return false;
                }
            }
            prevPlayer = currPlayer;
            }
        return true;
    }

    // Determines if everyone has had a turn during the current round phase
    public boolean hasEveryoneHadATurn() {
        return turnCounter >= players.size();
    }

    public boolean isRoundStateOver() {
        if (players.size() <= 1) {
            return true;
        }
        return isEveryBetTheSame() && hasEveryoneHadATurn();
    }

    private void addToTableCards(int numOfCards) {
        for (Card card: deck.draw(numOfCards)) {
            tableCards.add(card);
        }
    }

    public void updateTableCards() {
        if (roundState == null) { throw new NullPointerException(); }
        switch (roundState) {
            case PRE_FLOP, SHOWDOWN -> {}
            case FLOP -> addToTableCards(3);
            case TURN, RIVER -> addToTableCards(1);
        }
    }


    public Player getPlayerWithTurn() {
        return players.get(playerWithTurnIndex);
    }

    public void giveNextPlayerTurn() {
        getPlayerWithTurn().setTurn(false);
        playerWithTurnIndex = (playerWithTurnIndex + 1) % players.size();
        getPlayerWithTurn().setTurn(true);
    }

    public boolean isPlayerWithTurnBankrupt() {
        return getPlayerWithTurn().isBankrupt();
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    // TODO
    public void foldPlayer(Player player) {
        unfoldedPlayers.remove(player);
    }

    public HashMap<PokerRole, Player> getPlayersWithRoles() {
        HashMap<PokerRole, Player> hashMap = new HashMap<>();
        hashMap.put(PokerRole.DEALER, players.get(dealerIndex));
        hashMap.put(PokerRole.SMALL_BLIND, players.get(smallBlindIndex));
        hashMap.put(PokerRole.BIG_BLIND, players.get(bigBlindIndex));
        return hashMap;
    }

    public void setPlayerRoles(HashMap<PokerRole, Player> hashMap) {
        Player dealer = hashMap.get(PokerRole.DEALER);
        Player smallBlind = hashMap.get(PokerRole.SMALL_BLIND);
        Player bigBlind = hashMap.get(PokerRole.BIG_BLIND);

        getPlayer(dealer).setRole(PokerRole.DEALER);
        getPlayer(smallBlind).setRole(PokerRole.SMALL_BLIND);
        getPlayer(bigBlind).setRole(PokerRole.BIG_BLIND);

        dealerIndex = players.indexOf(getPlayer(dealer));
        smallBlindIndex = players.indexOf(getPlayer(smallBlind));
        bigBlindIndex = players.indexOf(getPlayer(bigBlind));
    }

    public ArrayList<Card> getPlayerHand(Player player) {
        return getPlayer(player).get_hand();
    }

    public void setPlayerHand(Player player, ArrayList<Card> playerHand) {
        getPlayer(player).setHand(playerHand);
    }

    public void setPlayerWithTurn(Player playerWithTurn) {
        String playerIDToFind = playerWithTurn.getPlayerID();
        for (Player player: players) {
            String playerID = player.getPlayerID();
            if (playerID.equals(playerIDToFind)) {
                player.setTurn(true);
            }
            else {
                player.setTurn(false);
            }
        }
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public Player getPlayer(Player player) {
        for (Player currPlayer: players) {
            if (currPlayer.equals(player)) {
                return currPlayer;
            }
        }
        throw new RuntimeException("Cannot find player");
    }

    public void applyPlayerAction(Player actingPlayer, PlayerAction playerAction, int betAmount) {
        Player player = getPlayer(actingPlayer);
        switch (playerAction) {
            case FOLD -> {
                //TODO
            }
            case BET, RAISE -> performBetByPlayer(player, betAmount);
            case CALL -> performCallByPlayer(player);
            case CHECK, WAIT -> {}
        }
        turnCounter++;
    }

    public void advanceRoundState() {
        endRoundState();
        this.roundState = RoundState.getNextRoundState(roundState);
        updateTableCards();
    }

    public void endRoundState() {
        for (Player player: playerBettings.keySet()) {
            playerBettings.put(player, 0);
        }
        lastBetter = null;
        turnCounter = 0;
        minimumCallAmount = 0;
        minimumBetAmount = smallBlind;
    }

    public void giveChipsToWinners() {
        ArrayList<Player> winners = getWinningPlayers();
        int winningShare = this.totalPot / winners.size();
        for (Player winner: winners) {
            winner.set_chips(winner.getChips() + winningShare);
        }
    }

    public ArrayList<Card> getTableCards() {
        return tableCards;
    }
    public void setTableCards(ArrayList<Card> tableCards) {
        this.tableCards = new ArrayList<>(tableCards);
    }

    public ArrayList<Player> getPlayersWithNoChips() {
        ArrayList<Player> playersWithNoChips = new ArrayList<>();
        for (Player player: players) {
            if (player.getChips() <= 0) {
                playersWithNoChips.add(player);
            }
        }
        return playersWithNoChips;
    }

    public int[] getScore(Player player) {
        HandEval evaluator = new HandEval();
        ArrayList<Card> totalHand = new ArrayList<>(tableCards);
        totalHand.addAll(player.get_hand());
        return evaluator.evaluate(totalHand);
    }

    public int[] getHighestScore() {
        int[] highestScore = {0, 0};
        for (Player player: players) {
            int[] currentScore = getScore(player);
            if (highestScore[0] < currentScore [0]) {
                highestScore = currentScore;
            }
            else if (highestScore[0] == currentScore[0]) {
                if (highestScore[1] < currentScore[1]) {
                    highestScore = currentScore;
                }
            }
        }
        return highestScore;
    }

    public ArrayList<Player> getWinningPlayers() {
        ArrayList<Player> winners = new ArrayList<>();
        int[] highestScore = getHighestScore();
        for (Player player: players) {
            int[] currScore = getScore(player);
            if (highestScore[0] == currScore[0] &&
                    highestScore[1] == currScore[1]) {
                winners.add(player);
            }
        }
        return winners;
    }

    private boolean canCheck(Player player) {
        if (playerBettings.get(player) == minimumCallAmount) {
            return true;
        }

        for (Player currPlayer : playerBettings.keySet()) {
            if (playerBettings.get(currPlayer) > 0) {
                return false;
            }
        }
        return true;
    }

    private boolean canBet() {
        return lastBetter == null;
    }

    public HashSet<PlayerAction> getValidActions(Player player) {
        Player localPlayer = getPlayer(player);
        HashSet<PlayerAction> validActions = new HashSet<>();

        int playerBetPower = localPlayer.getChips() + playerBettings.get(localPlayer);
        if (playerBetPower > minimumCallAmount) {
            if (canBet()) {
                validActions.add(PlayerAction.BET);
            }
            else {
                validActions.add(PlayerAction.RAISE);
            }
        }
        if (canCheck(localPlayer)) {
            validActions.add(PlayerAction.CHECK);
        }
        else {
            validActions.add(PlayerAction.CALL);
        }
        validActions.add(PlayerAction.FOLD);

        return validActions;
    }

    public int getPlayerSidePot(Player player) {
        if (playerBettings.containsKey(player)) {
            return playerBettings.get(player);
        }
        return playerBettings.get(getPlayer(player));
    }

    public int getPlayerBettingPower(Player player) {
        Player localPlayer = getPlayer(player);
        return playerBettings.get(localPlayer) + localPlayer.getChips();
    }

    public int getMinimumCallAmount() {
        return minimumCallAmount;
    }

    public int getMinimumBetAmount() {
        return this.minimumBetAmount;
    }

    public RoundState getRoundState() {
        return roundState;
    }

    public void setRoundState(RoundState roundState) {
        this.roundState = roundState;
    }

    public boolean hasGameStarted() {
        return hasGameStarted;
    }

    public void setHasGameStarted(boolean hasGameStarted) {
        this.hasGameStarted = hasGameStarted;
    }

    public boolean hasGameEnded() {
        return hasGameEnded;
    }

    public void setHasGameEnded(boolean hasGameEnded) {
        this.hasGameEnded = hasGameEnded;
    }

    public int getTotalPot() {
        return totalPot;
    }

}
