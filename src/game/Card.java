package game;

import java.io.Serializable;

public class  Card implements Serializable {
    private static final long serialVersionUID = 3132734764930998421L;
    public int face; // 1에서 10까지의 숫자
    public String suit; // "Club", "Spade"

    public Card(int face, String suit) {
        this.face = face;
        this.suit = suit;
    }

    public String toString() {
        return face + " " + suit;
    }
}
