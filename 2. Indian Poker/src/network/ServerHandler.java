package network;

import java.io.IOException;
import java.net.Socket;

public class ServerHandler extends Thread {
    private Socket _socket = null;
    public ServerManager _svrMgr = null;

    public ServerHandler(ServerManager svrMgr, Socket socket) {
        _socket = socket;
        _svrMgr = svrMgr;
    }

    /*
     * Receive messages from client
     * Removes thread of disconnected client
     */
    @Override
    public void run() {
        while(true) {
            try {
                _svrMgr.receiveMsg(_socket);
            } catch (IOException | ClassNotFoundException e) {
                _svrMgr.clientDisconnected(_socket);
                break;
            }
        }
    }
}
