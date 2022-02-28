import java.util.ArrayList;

public class Game {
    private Deck deck;
    private ArrayList<Player> players;
    private ArrayList<Card> tableCards;


    public void newGame(ArrayList<Player> players) {
        this.deck = new Deck();
        this.players = players;
        this.tableCards = new ArrayList<Card>();
    }


}
