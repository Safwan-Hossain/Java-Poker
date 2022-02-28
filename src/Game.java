import java.util.ArrayList;

public class Game {
    private Deck deck;
    private ArrayList<Player> players;
    // players who haven't folded
    private ArrayList<Player> unfoldedPlayers;
    private ArrayList<Card> tableCards;
    private int currentPlayerIndex;
    private int nextPlayerIndex;
    private int minimumCallAmount;

    public Game(ArrayList<Player> players) {
        this.deck = new Deck();
        this.players = players;
        this.tableCards = new ArrayList<Card>();
        this.currentPlayerIndex = 0;
        this.nextPlayerIndex = 0;
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

        return  nextPlayer;
    }

    private Player getCurrentPlayer() {
        if (currentPlayerIndex >= unfoldedPlayers.size()) {
            throw new RuntimeException("Index out of bounds");
        }

        return unfoldedPlayers.get(currentPlayerIndex);
    }

    public void giveNextTurn() {
        getCurrentPlayer().takeTurn();
        getNextPlayer().giveTurn();
    }






}
