package game;

import java.io.Serializable;

public class Player implements Serializable {
    private static final long serialVersionUID = 3432734764930998421L;
    public boolean ready; // Opponent is matched : true
    public int hand; // Opponent chose from rock scissor paper
    public boolean myTurn; // My turn to bet : true

    public String name;
    public Card myCard;
    public Card opponentCard;

    public int totalChips;
    public int bettingChips;
    public boolean lastWin;
    public boolean moreBet;
    public int finalWin;


    public Player(String name, int totalChips) {
        this.name = name;
        this.totalChips = totalChips;
        this.moreBet = true;
    }

    public void betChips(int myBet) {
        bettingChips += myBet;
        totalChips -= myBet;
    }

    public void win(int opponentBet) {
        totalChips += bettingChips + opponentBet;
        bettingChips = 0;
        lastWin = true;
        moreBet = true;
    }

    public void lose() {
        bettingChips = 0;
        lastWin = false;
        moreBet = true;
    }

    public String toString() {
        return "Ready : " + ready + ", Hand : " + hand + ", My Turn : " + myTurn + ", Name : " + name
                + ", My Card : " + myCard.toString() + ", Opponent Card : " + opponentCard.toString()
                + ", Total Chips : " + totalChips + ", Betting Chips : " + bettingChips
                + ", Last Win : " + lastWin + ", More Bet : " + moreBet;
    }
}
