import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Game implements Serializable {
    private int playerWithTurnIndex;
    private int currentPlayerIndex;
    private int nextPlayerIndex;
    private int dealerIndex;
    private int smallBlindIndex;
    private int bigBlindIndex;

    private int smallBlind;
    private int bigBlind;

    public int getMinimumCallAmount() {
        return minimumCallAmount;
    }

    private int minimumCallAmount;
    private int totalPot;

    private final int MAX_HAND_SIZE;

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
        this.deck = new Deck();
        this.tableCards = new ArrayList<>();
        this.players = players;

        this.smallBlind = smallBlind;
        this.bigBlind = smallBlind * 2;
        this.totalPot = 0;

        this.MAX_HAND_SIZE = 2;

        this.playerBettings = new HashMap<>();
        for (Player player: players) {
            playerBettings.put(player, 0);
        }
        this.minimumCallAmount = bigBlind;
        this.dealerIndex = 0;
        this.currentPlayerIndex = 0;
        this.nextPlayerIndex = 0;

        this.hasGameStarted = false;
        this.hasGameEnded = false;
        this.roundState = RoundState.PRE_FLOP;
    }

    public void startGame() {
        deck.fillDeck();
        deck.shuffle();
        this.hasGameStarted = true;
    }

    public void removeLoses() {
        for (Player player: getPlayersWithNoChips()) {
            players.remove(player);
        }
    }
    public void endRound() {
        for (Player player: playerBettings.keySet()) {
            playerBettings.put(player, 0);
        }
        tableCards.clear();
        for (Player player: players) {
            player.get_hand().clear();
        }
    }

    public void betByPlayer(Player player, int betAmount) {
        if (betAmount > player.getChips()) {
            betAmount = player.getChips();
        }
        player.takeChips(betAmount);
        totalPot += betAmount;
        playerBettings.put(player, betAmount);
        this.minimumCallAmount = betAmount;
    }

    public void callByPlayer(Player player) {
        int actualCalLAmount = minimumCallAmount;
        if (minimumCallAmount > player.getChips()) {
            actualCalLAmount = player.getChips();
        }
        player.takeChips(actualCalLAmount);
        totalPot += actualCalLAmount;
        playerBettings.put(player, actualCalLAmount);
    }
    
    public boolean isRoundStateOver() {

        if (players.size() <= 1) {
            return false;
        }

        boolean everyBetIsSame = true;

        int betAmount = -1;
        for (Player player: playerBettings.keySet()) {
            if (betAmount == -1) {
                betAmount = playerBettings.get(player);
            }
            else if (betAmount != playerBettings.get(player)) {
                everyBetIsSame = false;
            }
        }

        if (players.size() == 2) {
            return everyBetIsSame && playerBettings.get(players.get(0)) > bigBlind;
        }

        boolean everyoneHadATurn = false;
        if (lastBetter == null) {
            everyoneHadATurn = playerWithTurnIndex == (bigBlindIndex + 1) % players.size();
        }
        else {
            everyoneHadATurn = getPlayerWithTurn().equals(lastBetter);
        }

        return everyBetIsSame && everyoneHadATurn;
    }

    public void initializeRound() {
        this.roundState = RoundState.PRE_FLOP;
        assignRoles();
        assignCards();
        assignTurn();
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

    public void assignRoles() {
        if (dealerIndex >= players.size()) {
            dealerIndex = dealerIndex % players.size();
        }

        players.get(dealerIndex).setRole(PokerRole.NONE);

        dealerIndex = (dealerIndex + 1) % players.size();
        smallBlindIndex = (dealerIndex + 1) % players.size();
        bigBlindIndex = (dealerIndex + 2) % players.size();

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
            if (playerHand.length > MAX_HAND_SIZE) {
                throw new RuntimeException("Player has more than 2 cards");
            }
        }
    }

    public void assignTurn() {
        playerWithTurnIndex = (bigBlindIndex + 1) % players.size();
        players.get(playerWithTurnIndex).setTurn(true);
    }

    public Player getPlayerWithTurn() {
        return players.get(playerWithTurnIndex);
    }

    public void giveNextPlayerTurn() {
        getPlayerWithTurn().setTurn(false);
        playerWithTurnIndex = (playerWithTurnIndex + 1) % players.size();
        getPlayerWithTurn().setTurn(true);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public void foldPlayer(Player player) {
        unfoldedPlayers.remove(player);
    }

    public void dealCards() {
        for (Player player : players) {
            Card[] playerHand = deck.draw(5);
            for (Card card: playerHand) {
                player.insert(card);
            }
        }
    }

    private Player getNextPlayer() {
        if (nextPlayerIndex >= unfoldedPlayers.size()) {
            throw new RuntimeException("Index out of bounds");
        }

        Player nextPlayer = unfoldedPlayers.get(nextPlayerIndex);
        currentPlayerIndex = nextPlayerIndex;
        nextPlayerIndex = (nextPlayerIndex + 1) % unfoldedPlayers.size();

        return nextPlayer;
    }

    private Player getCurrentPlayer() {
        if (currentPlayerIndex >= unfoldedPlayers.size()) {
            throw new RuntimeException("Index out of bounds");
        }

        return unfoldedPlayers.get(currentPlayerIndex);
    }

    public void giveNextTurn() {
        getCurrentPlayer();
        getNextPlayer().giveTurn();
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

        // TODO check if player exists
        findPlayerUsingID(dealer).setRole(PokerRole.DEALER);
        findPlayerUsingID(smallBlind).setRole(PokerRole.SMALL_BLIND);
        findPlayerUsingID(bigBlind).setRole(PokerRole.BIG_BLIND);

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
            case BET, RAISE -> {
                betByPlayer(player, betAmount);
                lastBetter = player;
            }
            case CALL -> {
                callByPlayer(player);
            }
        }
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


    private Player findPlayerUsingID(Player playerToFind) {
        String playerIDToFind = playerToFind.getPlayerID();
        for (Player player: players) {
            String playerID = player.getPlayerID();
            if (playerID.equals(playerIDToFind)) {
                return player;
            }
        }
        throw new RuntimeException("Cannot find player");
    }

    public void advanceRoundState() {
        this.roundState = RoundState.getNextRoundState(roundState);
        for (Player player: playerBettings.keySet()) {
            playerBettings.put(player, 0);
        }
        updateTableCards();
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
        this.tableCards = tableCards;
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
