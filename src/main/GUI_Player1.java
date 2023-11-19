package main;

import GUI.WinEvent;
import network.ClientManager;

import java.awt.*;

public class GUI_Player1 {
    public static void main(String[] args) {
        Frame f_start = new Frame("Indian Poker");
        f_start.setBounds(120, 100, 600, 400);
        f_start.setLayout(null);

        Font font1 = new Font("Arial", Font.BOLD, 40);
        Font font2 = new Font("Arial", Font.ITALIC, 20);
        Font font3 = new Font("Arial", Font.BOLD, 20);
        Label l1 = new Label("Welcome to Indian Poker !", Label.CENTER); l1.setFont(font1);
        Label l2 = new Label("made by Francis Yoon", Label.CENTER); l2.setFont(font2);
        Label l3 = new Label("Enter Server_IP :", Label.RIGHT); l3.setFont(font3);
        Label l4 = new Label("Enter Port_Num :", Label.RIGHT); l4.setFont(font3);
        Label l5 = new Label("Enter Nickname :", Label.RIGHT); l5.setFont(font3);
        TextField t1 = new TextField(20); t1.setFont(font3);
        TextField t2 = new TextField(20); t2.setFont(font3);
        TextField t3 = new TextField(20); t3.setFont(font3);
        Button btn = new Button("Enter"); btn.setFont(font3);

        l1.setBounds(0, 50, 600, 40); l1.setForeground(new Color(148, 85, 17));
        l2.setBounds(0, 100, 600, 40);
        l3.setBounds(20, 180, 200, 40);
        l4.setBounds(20, 230, 200, 40);
        l5.setBounds(20, 280, 200, 40);
        t1.setBounds(230, 185, 170, 25);
        t2.setBounds(230, 235, 170, 25);
        t3.setBounds(230, 285, 170, 25);
        btn.setBounds(420, 185, 130, 130);

        f_start.add(l1);
        f_start.add(l2);
        f_start.add(l3);
        f_start.add(l4);
        f_start.add(l5);
        f_start.add(t1);
        f_start.add(t2);
        f_start.add(t3);
        f_start.add(btn);

        f_start.setBackground(Color.LIGHT_GRAY); // Set Color to Light Gray
        f_start.addWindowListener(new WinEvent());
        f_start.setVisible(true);

        btn.addActionListener(e -> {
            String serverIP = t1.getText();
            int port = Integer.parseInt(t2.getText());
            String name = t3.getText();

            ClientManager _cltMgr = new ClientManager(serverIP, port, name, f_start);
            new Thread(_cltMgr).start();
        });
    }
}
