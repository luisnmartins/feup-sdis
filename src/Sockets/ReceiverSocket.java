package Sockets;

import javax.net.ssl.*;
import java.io.*;
import java.security.GeneralSecurityException;
import java.nio.charset.*;
import java.lang.ProcessBuilder;
import java.util.AbstractMap.SimpleEntry;
import Peer.Peer;


/**
 * DSSocket
 */
public class ReceiverSocket extends SecureSocket {

    protected SSLServerSocket serverSocket;


    public ReceiverSocket(int port){
        super();
        this.port = port;
    }


    public void connect(String connectFrom,String connectTo) {
        try{
            setupSocketKeyStore(connectFrom);
            //setupPublicKeyStore(connectTo);
            setupSSLContext();

            SSLServerSocketFactory sf = sslContext.getServerSocketFactory();
            SSLServerSocket ss = (SSLServerSocket) sf.createServerSocket(this.port);
            
            //Require client auth
            ss.setNeedClientAuth(false);
            System.out.println("Listening on " + this.port);
            socket = (SSLSocket) ss.accept();
            System.out.println( "Got connection from "+socket );
            while(true){
              
                InputStream in = socket.getInputStream();
                OutputStream out = socket.getOutputStream();

                din = new DataInputStream(in);
                dout = new DataOutputStream(out);

                byte[] buf = new byte[PACKET_SIZE];

                din.readFully(buf);

                SimpleEntry<Integer,byte[]> pair = new SimpleEntry<>(buf.length,buf);
                Peer.getMessageInterpreter().putInQueue(pair);

                

            }

        }catch(GeneralSecurityException ge){
            ge.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }


     public void run(){
        connect(Peer.getPeerID(),null);
    }




    
}