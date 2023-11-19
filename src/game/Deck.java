package game;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    public ArrayList<Card> deck;
    public int cardLeft = 20;
    public static final int[] FACE
            = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    public static final String[] SUIT = {"Club", "Spade"};

    public Deck() {
        deck = new ArrayList<>();
        for(int i=0; i<10; i++) {
            for(int j=0; j<2; j++) {
                Card card = new Card(FACE[i], SUIT[j]);
                deck.add(card);
            }
        }
        Collections.shuffle(deck);
    }

    public Card drawCard() {
        Card card = deck.get(cardLeft-1);
        deck.remove(cardLeft-1);
        cardLeft -= 1;
        return card;
    }
}
