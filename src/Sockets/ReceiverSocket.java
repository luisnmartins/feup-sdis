package Sockets;

import javax.net.ssl.*;
import java.io.*;
import java.security.GeneralSecurityException;
import java.nio.charset.*;
import java.lang.ProcessBuilder;
import java.util.Arrays;
import java.util.AbstractMap.SimpleEntry;
import Peer.Peer;
import Peer.MessageHandler;
import Peer.MessageHandler.Transition;
import Tracker.Tracker;


public class ReceiverSocket extends SecureSocket {

    protected SSLServerSocket serverSocket;

    public ReceiverSocket(int port) {
        super();
        this.port = port;
    }

    /**
     * @return the serverSocket
     */
    public SSLServerSocket getServerSocket() {
        return serverSocket;
    }

    public void connect(String connectFrom) {
        try {
            setupSocketKeyStore(connectFrom);
            setupSSLContext();

            SSLServerSocketFactory sf = sslContext.getServerSocketFactory();
            this.serverSocket = (SSLServerSocket) sf.createServerSocket(this.port);

            // Require client auth
            this.serverSocket.setNeedClientAuth(false);
            System.out.println("Listening on " + serverSocket.getLocalPort());
            Runnable accepter;
            if(connectFrom.equals("tracker")){
                accepter = new connectionAccepter(true);
                Tracker.getExec().execute(accepter);
            }else{
                accepter =  new connectionAccepter(false);
                Peer.getExec().execute(accepter);
            }
        } catch (GeneralSecurityException ge) {
            ge.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class connectionAccepter implements Runnable {

        boolean isTracker;
        public connectionAccepter(boolean isTracker) {this.isTracker = isTracker;}

        @Override
        public void run() {
            while (true) {
                try {
                    SSLSocket socketConnected = (SSLSocket) serverSocket.accept();
                    System.out.println("Got connection from " + socketConnected);
                    MessageHandler handler = new MessageHandler(socketConnected);
                    handler.updateState(Transition.RECEIVER);
                    if(isTracker)
                        Tracker.getExec().execute(handler);
                    else
                        Peer.getExec().execute(handler);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}