package main;

import network.ServerManager;

public class GUI_Server {
    private static final int DEFAULT_PORT = 6777;

    public static void main(String[] args) {
        ServerManager _svrMgr = new ServerManager(DEFAULT_PORT);
        new Thread(_svrMgr).start();
    }
}
