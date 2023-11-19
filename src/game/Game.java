package game;

import java.io.Serializable;

public class Game implements Serializable {
    private static final long serialVersionUID = 3232734764930998421L;
    public int round;

    public Player player1;
    public Player player2;

    public int bet1, bet2;

    public Player p_first; // First player of the round
    public Player p_second; // Second player of the round
}
