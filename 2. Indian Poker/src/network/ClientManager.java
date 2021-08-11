package network;

import java.io.IOException;
import java.net.Socket;

import java.awt.*;
import java.util.concurrent.TimeUnit;

import GUI.*;
import game.*;


public class ClientManager extends NetworkManager {
    private Socket _socket = null;
    public Player player;
    public static int startChips = 30;

    public Frame f_start;
    public Frame f_rsp; // Rock Scissor Paper
    private Button btn_rsp;
    private Label lbl_rsp;

    public Frame f_main; // Indian Poker
    private MainGUIStruct m;

    public Frame f_end = new Frame("Indian Poker"); // Ending board

    // Constructor
    public ClientManager(String addr, int port, String name, Frame f_start) {
        try {
            this.f_start = f_start;
            setPlayer(name);
            rspGui();
            _socket = new Socket(addr, port);
            System.out.println("Connected to server: " + addr + ":" + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPlayer(String name) {
        this.player = new Player(name, startChips);
        this.f_start.setVisible(false);
    }

    // GUI for Rock Scissor Paper
    public void rspGui() {
        f_rsp = new Frame("Rock Scissor Paper");
        f_rsp.setBounds(120, 100, 600, 400);
        f_rsp.setLayout(null);

        Font font1 = new Font("Arial", Font.BOLD, 40);
        Font font2 = new Font("Arial", Font.BOLD, 20);

        Label l1 = new Label("Rock Scissor Paper !", Label.CENTER); l1.setFont(font1);
        Label l2 = new Label("Winner Goes First !", Label.CENTER); l2.setFont(font2);
        lbl_rsp = new Label("Waiting for Opponent...", Label.CENTER); lbl_rsp.setFont(font2);
        CheckboxGroup group_rsp = new CheckboxGroup();
        Checkbox cb1 = new Checkbox("Rock", false, group_rsp); cb1.setFont(font2);
        Checkbox cb2 = new Checkbox("Scissor", false, group_rsp); cb2.setFont(font2);
        Checkbox cb3 = new Checkbox("Paper", false, group_rsp); cb3.setFont(font2);
        btn_rsp = new Button("Enter"); btn_rsp.setFont(font2);

        l1.setBounds(0, 50, 600, 40); l1.setForeground(new Color(148, 85, 17));
        l2.setBounds(0, 100, 600, 40);
        cb1.setBounds(200, 150, 100, 40);
        cb2.setBounds(200, 190, 100, 40);
        cb3.setBounds(200, 230, 100, 40);
        lbl_rsp.setBounds(0, 300, 600, 40); lbl_rsp.setForeground(new Color(9, 72, 152, 255));
        btn_rsp.setBounds(300, 160, 100, 100);

        f_rsp.add(l1);
        f_rsp.add(l2);
        f_rsp.add(lbl_rsp);
        f_rsp.add(cb1);
        f_rsp.add(cb2);
        f_rsp.add(cb3);
        f_rsp.add(btn_rsp); btn_rsp.setVisible(false);

        f_rsp.setBackground(Color.LIGHT_GRAY);
        f_rsp.addWindowListener(new WinEvent());
        f_rsp.setVisible(true);

        btn_rsp.addActionListener(e -> {
            Checkbox checkbox = group_rsp.getSelectedCheckbox();
            if(checkbox != null) {
                String myHand = checkbox.getLabel();
                this.player.hand = myHand.equals("Rock") ? 1 : (myHand.equals("Scissor") ? 2 : 3);
                try {
                    _sendMsg(new MessageStruct(1, this.player));
                    btn_rsp.setVisible(false);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    // GUI for Indian Poker
    public void mainGUI() throws InterruptedException {
        TimeUnit.SECONDS.sleep(3); // Wait for 3 seconds
        f_rsp.setVisible(false);
        m = new MainGUIStruct();

        f_main = new Frame("Indian Poker");
        f_main.setBounds(120, 100, 1000, 600);
        f_main.setLayout(null);

        m.font1 = new Font("Arial", Font.BOLD, 40);
        m.font2 = new Font("Arial", Font.BOLD, 30);
        m.font4 = new Font("Arial", Font.BOLD, 15);
        m.font5 = new Font("Arial", Font.BOLD, 20);
        m.font3 = new Font("Arial", Font.BOLD, 60);

        m.l01 = new Label("Round 1 - " + this.player.name, Label.CENTER); m.l01.setFont(m.font1); m.l01.setForeground(Color.WHITE);
        m.l02 = new Label("Current Chips", Label.CENTER); m.l02.setFont(m.font2);
        m.l03 = new Label("Francis", Label.CENTER); m.l03.setFont(m.font1); m.l03.setForeground(Color.WHITE);
        m.l04 = new Label("Joseph", Label.CENTER); m.l04.setFont(m.font1); m.l04.setForeground(Color.WHITE);
        m.l05 = new Label("30", Label.CENTER); m.l05.setFont(m.font3); m.l05.setForeground(Color.WHITE);
        m.l06 = new Label("30", Label.CENTER); m.l06.setFont(m.font3); m.l06.setForeground(Color.WHITE);
        m.l07 = new Label("Current Betting :", Label.CENTER); m.l07.setFont(m.font5); m.l07.setForeground(Color.WHITE);
        m.l08 = new Label("Current Betting :", Label.CENTER); m.l08.setFont(m.font5); m.l08.setForeground(Color.WHITE);
        m.l09 = new Label("0", Label.CENTER); m.l09.setFont(m.font5);
        m.l10 = new Label("0", Label.CENTER); m.l10.setFont(m.font5);
        m.l11 = new Label("Enter Betting :", Label.CENTER); m.l11.setFont(m.font5);
        m.txt12 = new TextField(20); m.txt12.setFont(m.font1);
        m.btn13 = new Button("BET !"); m.btn13.setFont(m.font5); m.btn13.setForeground(Color.WHITE);
        m.l14 = new Label("");
        m.l15 = new Label("Opponent's Card", Label.CENTER); m.l15.setFont(m.font4);
        m.l16 = new Label("", Label.CENTER); m.l16.setFont(m.font2);
        m.l17 = new Label("My Card", Label.CENTER); m.l17.setFont(m.font4);
        m.l18 = new Label("?", Label.CENTER); m.l18.setFont(m.font2); m.l18.setForeground(Color.WHITE);

        m.l01.setBounds(40, 60, 740, 50); m.l01.setBackground(Color.BLACK);
        m.l02.setBounds(40, 120, 740, 40);
        m.l03.setBounds(40, 200, 350, 60); m.l03.setBackground(Color.RED);
        m.l04.setBounds(430, 200, 350, 60); m.l04.setBackground(Color.BLUE);
        m.l05.setBounds(40, 280, 350, 100); m.l05.setBackground(Color.RED);
        m.l06.setBounds(430, 280, 350, 100); m.l06.setBackground(Color.BLUE);
        m.l07.setBounds(40, 400, 200, 50); m.l07.setBackground(Color.RED);
        m.l08.setBounds(430, 400, 200, 50); m.l08.setBackground(Color.BLUE);
        m.l09.setBounds(260, 400, 130, 50); m.l09.setBackground(Color.WHITE);
        m.l10.setBounds(650, 400, 130, 50); m.l10.setBackground(Color.WHITE);
        m.l11.setBounds(125, 500, 200, 50);
        m.txt12.setBounds(345, 500, 130, 50); m.txt12.setBackground(Color.WHITE);
        m.btn13.setBounds(495, 500, 200, 50); m.btn13.setBackground(Color.BLACK); m.btn13.setVisible(false);
        m.l14.setBounds(405, 180, 10, 300); m.l14.setBackground(Color.BLACK);
        m.l15.setBounds(810, 60, 150, 30); m.l15.setBackground(Color.ORANGE);
        m.l16.setBounds(810, 100, 150, 200); m.l16.setBackground(Color.WHITE);
        m.l17.setBounds(810, 320, 150, 30); m.l17.setBackground(Color.ORANGE);
        m.l18.setBounds(810, 360, 150, 200); m.l18.setBackground(Color.BLACK);

        f_main.add(m.l01);
        f_main.add(m.l02);
        f_main.add(m.l03);
        f_main.add(m.l04);
        f_main.add(m.l05);
        f_main.add(m.l06);
        f_main.add(m.l07);
        f_main.add(m.l08);
        f_main.add(m.l09);
        f_main.add(m.l10);
        f_main.add(m.l11);
        f_main.add(m.txt12);
        f_main.add(m.btn13);
        f_main.add(m.l14);
        f_main.add(m.l15);
        f_main.add(m.l16);
        f_main.add(m.l17);
        f_main.add(m.l18);

        f_main.setBackground(Color.LIGHT_GRAY);
        f_main.addWindowListener(new WinEvent());
        f_main.setVisible(true);

        m.btn13.addActionListener(e -> {
            String roundBet = m.txt12.getText();
            if(roundBet != null) {
                try {
                    int _roundBet = Integer.parseInt(roundBet);
                    _sendMsg(new MessageStruct(4, _roundBet));
                    m.txt12.setText("");
                    m.btn13.setVisible(false);
                } catch (NumberFormatException | IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    // Update game status
    public void updateGame(Game game) {
         if(this.player.name.equals(game.player1.name)) {
            this.player = game.player1;
        } else {
            this.player = game.player2;
        }
    }

    // Update game board
    public void updateMainGUI(Game game) {
        updateGame(game);

        m.l01.setText("Round " + game.round + " - " + this.player.name);
        m.l03.setText(game.player1.name);
        m.l04.setText(game.player2.name);
        m.l05.setText("" + game.player1.totalChips);
        m.l06.setText("" + game.player2.totalChips);
        m.l09.setText("" + game.player1.bettingChips);
        m.l10.setText("" + game.player2.bettingChips);
        m.l16.setText(this.player.opponentCard.toString());
        m.btn13.setVisible(this.player.myTurn);
        m.l18.setText("?"); m.l18.setForeground(Color.WHITE);
    }

    // Reveal Opponent's card
    public void revealCard() {
        m.l18.setText(this.player.myCard.toString());
    }

    // GUI for ending
    public void endGame(Game game) {
        f_end.setVisible(false);
        f_main.setVisible(false);

        f_end = new Frame("Indian Poker");
        f_end.setBounds(100, 100, 600, 400);
        f_end.setLayout(null);

        String status = null;
        Color color = null;
        if(this.player.finalWin == 1) {
            status = "You Win !";
        } else if(this.player.finalWin == 0) {
            status = "You Lose !";
        } else {
            status = "Tie !";
        }
        if(this.player.name.equals(game.player1.name)) {
            color = Color.RED;
        } else {
            color = Color.BLUE;
        }

        Font font = new Font("Arial", Font.BOLD, 60);
        Label l1 = new Label(status, Label.CENTER); l1.setForeground(color);
        l1.setBounds(0, 150, 600, 100); l1.setFont(font);

        f_end.add(l1);

        f_end.setBackground(Color.LIGHT_GRAY);
        f_end.addWindowListener(new WinEvent());
        f_end.setVisible(true);
    }

    // Send message to server
    public void _sendMsg(MessageStruct msg) throws IOException {
        sendMsg(_socket, msg);
    }

    // Handle messages
    @Override
    public void msgHandler(MessageStruct msg, Socket _src) {
        // Code for handling messages
        switch(msg._code) {
            case 0:
                // Receive message from server that you are accepted
                // Return ClientManager Instance
                try {
                    this.player.ready = true;
                    _sendMsg(new MessageStruct(1, this.player));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 1:
            case 2:
                break;
            case 3:
                // Rock Scissor Paper
                // Make Button in rspGUI
                lbl_rsp.setText((String) msg._content);
                btn_rsp.setVisible(!((String) msg._content).contains("You"));
                break;
            case 4:
                // Game start
                // Open main frame
                try {
                    mainGUI();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case 5:
                // Receive updated game instance
                updateMainGUI((Game) msg._content);
                break;
            case 6:
                // updateGame((Game) msg._content);
                revealCard();
                break;
            case 7:
                // Final winner and loser
                updateGame((Game) msg._content);
                endGame((Game) msg._content);
                break;
        }
    }

    // Run a loop to receive messages from server
    @Override
    public void run() {
        while(true) {
            try {
                receiveMsg(_socket);
            } catch (ClassNotFoundException | IOException e) {
                if(_socket.isClosed())
                {
                    System.out.println("Bye.");
                    System.exit(0);
                }

                System.out.println("Connection to server is broken. Please restart client.");
                close(_socket);
                System.exit(-1);
            }
        }
    }
}
