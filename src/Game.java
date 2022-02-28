import java.util.ArrayList;

public class Game {
    private Deck deck;
    private ArrayList<Player> players;
    private ArrayList<Card> tableCards;
    private int currentPlayerIndex;
    private int nextPlayerIndex;

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

    public void dealCards() {
        for (Player player : players) {
            Card[] playerHand = deck.draw(5);
            for (Card card: playerHand) {
                player.insert(card);
            }
        }
    }

    public void giveNextPlayerTurn() {
        players.get(nextPlayerIndex).giveTurn();
        currentPlayerIndex = nextPlayerIndex;
        nextPlayerIndex++;
    }

    public void takeCurrentPlayerTurn() {
        players.get(currentPlayerIndex).takeTurn();
    }





}
