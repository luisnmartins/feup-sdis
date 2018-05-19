package Sockets;

import javax.net.ssl.*;
import java.io.*;
import java.security.GeneralSecurityException;
import java.nio.charset.*;
import java.lang.ProcessBuilder;
import java.util.AbstractMap.SimpleEntry;
import Peer.Peer;


public class ReceiverSocket extends SecureSocket {

    protected SSLServerSocket serverSocket;

    public ReceiverSocket(int port) {
        super();
        this.port = port;
    }

    public void connect(String connectFrom) {
        try {
            setupSocketKeyStore(connectFrom);
            setupSSLContext();

            SSLServerSocketFactory sf = sslContext.getServerSocketFactory();
            this.serverSocket = (SSLServerSocket) sf.createServerSocket(this.port);

            // Require client auth
            this.serverSocket.setNeedClientAuth(false);
            System.out.println("Listening on " + this.port);
            Runnable accepter = new connectionAccepter();
            Peer.getExec().execute(accepter);
        } catch (GeneralSecurityException ge) {
            ge.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class connectionAccepter implements Runnable {

        public connectionAccepter() {
        }

        @Override
        public void run() {
            while (true) {
                try {
                    SSLSocket socketConnected = (SSLSocket) serverSocket.accept();
                    System.out.println("Got connection from " + socketConnected);
                    Runnable handler = new connectionHandler(socketConnected);
                    Peer.getExec().execute(handler);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public class connectionHandler implements Runnable {
        SSLSocket socketConnected;

        public connectionHandler(SSLSocket socket) {
            this.socketConnected = socket;
        }

        @Override
        public void run() {
            while (true) {

                try {
                    InputStream in = socketConnected.getInputStream();
                    OutputStream out = socketConnected.getOutputStream();

                    DataInputStream dataIn = new DataInputStream(in);
                    DataOutputStream dataOut = new DataOutputStream(out);

                    byte[] buf = new byte[PACKET_SIZE];
                    int read =  din.read(buf);

                    byte[] response = Arrays.copyOfRange(buf, 0, read);
                    SimpleEntry<Integer, byte[]> pair = new SimpleEntry<>(buf.length, buf);
                    Peer.getMessageInterpreter().putInQueue(pair);

                }catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

}