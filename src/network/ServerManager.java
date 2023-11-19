package network;

import game.Deck;
import game.Game;
import game.Player;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class ServerManager extends NetworkManager {
    private ServerSocket _svrSocket = null;
    private int playerReady = 0;
    private Game game;
    private Socket socket1; // Socket for player1
    private Socket socket2; // Socket for player2
    private Socket s_first; // Socket for first
    private Socket s_second; // Socket for second
    private final int startChips = ClientManager.startChips;

    public ServerManager(int svrPort) {
        try {
            game = new Game();
            _svrSocket = new ServerSocket(svrPort);

            System.out.println("Waiting for clients...");
            System.out.println("Please connect to " + InetAddress.getLocalHost() + ":" + svrPort + ".");
        } catch(IOException e) {
            System.out.println("ERROR: failed to listen on port " + svrPort);
            e.printStackTrace();
        }
    }

    /*
     * Run a loop to accept incoming clients
     * Once a connection is established, new thread is made by ServerHandler
     */
    @Override
    public void run() {
        try {
            // Accept player1
            socket1 = _svrSocket.accept();
            // Create ServerHandler
            new ServerHandler(this, socket1).start();
            System.out.println("Player1 connected!");
            sendMsg(socket1, new MessageStruct(0, 0)); // Returns player1

            // Accept player2
            socket2 = _svrSocket.accept();
            new ServerHandler(this, socket2).start();
            System.out.println("Player2 connected");
            sendMsg(socket2, new MessageStruct(0, 0)); // Returns player2

            // Wait for both players to return message
            while(true) {
                if(playerReady == 2) {
                    System.out.println("Both Players Accepted");
                    break;
                } else {
                    System.out.print(""); // Waiting : Standby
                }
            }

            // Decide order by Rock Scissor Paper
            decideFirst();
            System.out.println("Player1 : " + this.game.player1.hand + " " + this.game.player1.lastWin);
            System.out.println("Player2 : " + this.game.player2.hand + " " + this.game.player2.lastWin);
            System.out.println();

            // Start Indian Poker
            startGame();

            System.out.println("Game End");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Decide who goes first by Rock Scissor Paper
    public void decideFirst() {
        try {
            boolean rspEnd = false;

            sendMsg(socket1, new MessageStruct(3, "Waiting for Opponent..."));
            sendMsg(socket2, new MessageStruct(3, "Waiting for Opponent..."));
            while(!rspEnd) {
                if(this.game.player1.hand == 0 || this.game.player2.hand == 0) {
                    System.out.print(""); // Waiting : Rock Scissor Paper
                } else {
                    // Rock : 1
                    // Scissor : 2
                    // Paper : 3
                    if(this.game.player1.hand == this.game.player2.hand) {
                        this.game.player1.hand = 0;
                        this.game.player2.hand = 0;
                        sendMsg(socket1, new MessageStruct(3, "Tie"));
                        sendMsg(socket2, new MessageStruct(3, "Tie"));
                    } else {
                        if((this.game.player1.hand + 1 == this.game.player2.hand) || (this.game.player1.hand == 3 && this.game.player2.hand == 1)) {
                            this.game.player1.lastWin = true;
                            this.game.player2.lastWin = false;
                            sendMsg(socket1, new MessageStruct(3, "You won. You go first. !"));
                            sendMsg(socket2, new MessageStruct(3, "You lost. You go second. !"));
                        } else {
                            this.game.player1.lastWin = false;
                            this.game.player2.lastWin = true;
                            sendMsg(socket2, new MessageStruct(3, "You won. You go first !"));
                            sendMsg(socket1, new MessageStruct(3, "You lost. You go second. !"));
                        }
                        rspEnd = true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Start Indian Poker
    public void startGame() {
        try {
            // Open the main GUI window
            sendMsg(socket1, new MessageStruct(4, "game start"));
            sendMsg(socket2, new MessageStruct(4, "game start"));

            while(this.game.player1.totalChips != 0 && this.game.player2.totalChips != 0) {
                // 10 rounds begin
                int round = 1;
                Deck deck = new Deck(); // Create a deck for the game
                do {
                    this.game.round = round;
                    this.game.bet1 = 0;
                    this.game.bet2 = 0;

                    System.out.println("Round: " + round + "\n");

                    // Game end condition
                    if (this.game.player1.totalChips == 2 * startChips || this.game.player2.totalChips == 2 * startChips) {
                        break;
                    }

                    // Set first player and second player of the round
                    this.game.p_first = this.game.player1.lastWin ? this.game.player1 : this.game.player2;
                    this.game.p_second = !this.game.player1.lastWin ? this.game.player1 : this.game.player2;
                    s_first = this.game.player1.lastWin ? socket1 : socket2;
                    s_second = !this.game.player1.lastWin ? socket1 : socket2;

                    this.game.p_first.myTurn = false;
                    this.game.p_second.myTurn = false;

                    // Draw card
                    this.game.p_first.myCard = deck.drawCard();
                    this.game.p_second.myCard = deck.drawCard();
                    this.game.p_first.opponentCard = this.game.p_second.myCard;
                    this.game.p_second.opponentCard = this.game.p_first.myCard;

                    System.out.println("Player " + this.game.p_first.name + "'s card is " + this.game.p_first.myCard.toString());
                    System.out.println("Player " + this.game.p_second.name + "'s card is " + this.game.p_second.myCard.toString());

                    boolean running = true;
                    // When someone all ins, but draw -> reveal the card without right away without additional betting
                    if (this.game.p_first.totalChips == 0 || this.game.p_second.totalChips == 0) {
                        running = false;
                    } else {
                        // Basic betting
                        this.game.p_first.betChips(1);
                        this.game.p_second.betChips(1);

                        // Send Info to client -> update client' game board
                        sendMsg(socket1, new MessageStruct(5, this.game));
                        sendMsg(socket2, new MessageStruct(5, this.game));

                        // When either player's total chip becomes 0 by the basic betting
                        if (this.game.p_first.totalChips == 0 || this.game.p_second.totalChips == 0) {
                            running = false;
                        }
                    }

                    while (running) {
                        // Print betting of each player of this round
                        System.out.println("Player " + this.game.p_first.name + "'s total betting: " + this.game.p_first.bettingChips);
                        System.out.println("Player " + this.game.p_second.name + "'s total betting: " + this.game.p_second.bettingChips);
                        System.out.println();

                        // Bet until it satisfies the rule
                        Outer:
                        do {
                            this.game.p_first.myTurn = true;

                            // Send Info to client -> update client' game board
                            sendMsg(s_first, new MessageStruct(5, this.game));
                            sendMsg(s_second, new MessageStruct(5, this.game));

                            // *************** Add another betting rule ***************
                            while (true) {
                                // When my turn ended(finished betting)
                                if (!this.game.p_first.myTurn) {
                                    // When betting condition is satisfied
                                    if ((this.game.bet1 <= this.game.p_first.totalChips) && (this.game.p_first.bettingChips + this.game.bet1 >= this.game.p_second.bettingChips) && (this.game.p_first.bettingChips + this.game.bet1 <= this.game.p_second.bettingChips + this.game.p_second.totalChips)) {
                                        break Outer;
                                    }
                                    // When the player give up
                                    else if (this.game.bet1 == 0) {
                                        break Outer;
                                    } else {
                                        break;
                                    }
                                } else {
                                    System.out.print("");
                                }
                            }
                        } while (true);
                        System.out.println("Player " + this.game.p_first.name + " bets " + this.game.bet1 + " chips.\n");
                        this.game.p_first.betChips(this.game.bet1); // Betting successively finished

                        // First player gives up
                        if (this.game.bet1 == 0) {
                            this.game.p_first.moreBet = false;
                            break;
                        }
                        // Total betting is same
                        else if (this.game.p_first.bettingChips == this.game.p_second.bettingChips) {
                            break;
                        }

                        // Print betting of each player of this round
                        System.out.println("Player " + this.game.p_first.name + "'s total betting: " + this.game.p_first.bettingChips);
                        System.out.println("Player " + this.game.p_second.name + "'s total betting: " + this.game.p_second.bettingChips);
                        System.out.println();

                        // Send Info to client -> update client' game board
                        sendMsg(s_first, new MessageStruct(5, this.game));
                        sendMsg(s_second, new MessageStruct(5, this.game));

                        // Bet until it satisfies the rule
                        Outer:
                        do {
                            this.game.p_second.myTurn = true;

                            // Send Info to client -> update client' game board
                            sendMsg(s_first, new MessageStruct(5, this.game));
                            sendMsg(s_second, new MessageStruct(5, this.game));

                            // *************** Add another betting rule ***************
                            while (true) {
                                if (!this.game.p_second.myTurn) {
                                    if ((this.game.bet2 <= this.game.p_second.totalChips) && (this.game.p_second.bettingChips + this.game.bet2 >= this.game.p_first.bettingChips) && (this.game.p_second.bettingChips + this.game.bet2 <= this.game.p_first.bettingChips + this.game.p_first.totalChips)) {
                                        break Outer;
                                    }
                                    // When the player give up
                                    else if (this.game.bet2 == 0) {
                                        break Outer;
                                    } else {
                                        break;
                                    }
                                } else {
                                    System.out.print("");
                                }
                            }
                        } while (true);
                        System.out.println("Player " + this.game.p_second.name + " bets " + this.game.bet2 + " chips.\n");
                        this.game.p_second.betChips(this.game.bet2);

                        // Second player gives up
                        if (this.game.bet2 == 0) {
                            this.game.p_second.moreBet = false;
                            break;
                        }
                        // Total betting is same
                        else if (this.game.p_first.bettingChips == this.game.p_second.bettingChips) {
                            break;
                        }
                    }

                    // Send Info to client -> update client' game board
                    sendMsg(s_first, new MessageStruct(5, this.game));
                    sendMsg(s_second, new MessageStruct(5, this.game));

                    // Result of the round
                    System.out.println("Player " + this.game.p_first.name + "'s card was " + this.game.p_first.myCard.toString());
                    System.out.println("Player " + this.game.p_second.name + "'s card was " + this.game.p_second.myCard.toString());

                    Player winner = null, loser = null;
                    boolean tie = false;

                    // First player gives up
                    if (!this.game.p_first.moreBet) {
                        // Penalty for giving up while holding number 10
                        if (this.game.p_first.myCard.face == 10) {
                            if (this.game.p_first.totalChips < 10) {
                                this.game.p_second.totalChips += this.game.p_first.totalChips;
                                this.game.p_first.totalChips = 0;
                            } else {
                                this.game.p_first.totalChips -= 10;
                                this.game.p_second.totalChips += 10;
                            }
                        }
                        winner = this.game.p_second;
                        loser = this.game.p_first;
                    }
                    // Second player gives up
                    else if (!this.game.p_second.moreBet) {
                        // Penalty for giving up while holding number 10
                        if (this.game.p_second.myCard.face == 10) {
                            if (this.game.p_second.totalChips < 10) {
                                this.game.p_first.totalChips += this.game.p_second.totalChips;
                                this.game.p_second.totalChips = 0;
                            } else {
                                this.game.p_second.totalChips -= 10;
                                this.game.p_first.totalChips += 10;
                            }
                        }
                        winner = this.game.p_first;
                        loser = this.game.p_second;
                    }
                    // Total betting is same
                    else {
                        // First player has higher value
                        if (this.game.p_first.myCard.face > this.game.p_second.myCard.face) {
                            winner = this.game.p_first;
                            loser = this.game.p_second;
                        }
                        // Second player has higher value
                        else if (this.game.p_second.myCard.face > this.game.p_first.myCard.face) {
                            winner = this.game.p_second;
                            loser = this.game.p_first;
                        }
                        // Tie
                        else {
                            tie = true;
                        }
                    }

                    // No tie
                    if (!tie) {
                        winner.win(loser.bettingChips);
                        loser.lose();
                    }
                    // Tie
                    else {
                        this.game.p_first.moreBet = true;
                        this.game.p_second.moreBet = true;
                    }

                    // Result of one round
                    System.out.println("Player " + this.game.player1.name + " : " + this.game.player1.totalChips);
                    System.out.println("Player " + this.game.player2.name + " : " + this.game.player2.totalChips);
                    System.out.println("\n");

                    sendMsg(socket1, new MessageStruct(5, this.game));
                    sendMsg(socket2, new MessageStruct(5, this.game));
                    sendMsg(socket1, new MessageStruct(6, this.game));
                    sendMsg(socket2, new MessageStruct(6, this.game));
                    TimeUnit.SECONDS.sleep(3); // Wait for 3 seconds
                    round++;
                } while (round <= 10);
            }

            if(this.game.player1.totalChips > this.game.player2.totalChips) {
                this.game.player1.finalWin = 1;
                this.game.player2.finalWin = 0;
            } else if(this.game.player1.totalChips < this.game.player2.totalChips) {
                this.game.player1.finalWin = 0;
                this.game.player2.finalWin = 1;
            } else {
                this.game.player1.finalWin = 2;
                this.game.player2.finalWin = 2;
            }

            // 10 Rounds End
            sendMsg(socket1, new MessageStruct(7, this.game));
            sendMsg(socket2, new MessageStruct(7, this.game));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void clientDisconnected(Socket client) {
        if(client == socket1) {
            System.out.println("Player1 has disconnected.");
            this.game.player1 = null;
            socket1 = null;
        } else {
            System.out.println("Player2 has disconnected.");
            this.game.player2 = null;
            socket2 = null;
        }
    }

    // Message handlers begin
    @Override
    public void msgHandler(MessageStruct msg, Socket src) {
        switch(msg._code) {
            case 1:
                // Player sends its own info after being accept;
                if(src == socket1) {
                    this.game.player1 = (Player) msg._content;
                } else {
                    this.game.player2 = (Player) msg._content;
                }
                playerReady += 1;
                break;
            case 4:
                // Player sends his/her bet
                if(src == s_first) {
                    this.game.bet1 = (int) msg._content;
                    this.game.p_first.myTurn = false;
                } else if(src == s_second) {
                    this.game.bet2 = (int) msg._content;
                    this.game.p_second.myTurn = false;
                }
                break;
        }
    }
}
