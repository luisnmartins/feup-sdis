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
        /*try {
            SSLServerSocketFactory sslSrvFact = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            serverSocket = (SSLServerSocket) sslSrvFact.createServerSocket(port);
            this.socket = (SSLSocket) serverSocket.accept();

            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            // Send messages to the client through
            // the OutputStream
            // Receive messages from the client
            // through the InputStream



        } catch (Exception e) {
            //TODO: handle exception
        }*/
    }

     public void startSSLServerSocket() throws GeneralSecurityException, IOException{
        //SSLContext sslContext = SSLContext.getInstance("SSL");
        try{
            SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        
        this.serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(this.port);
        this.socket = (SSLSocket) serverSocket.accept();
        System.out.println("Created socket");

        OutputStream out = socket.getOutputStream();
        InputStream in = socket.getInputStream();

        byte[] response = new byte[9];
        in.read(response);

        System.out.println(new String(response,StandardCharsets.UTF_8));

        }catch(Exception e){
            e.printStackTrace();
        }
        
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
            while(true){
                socket = (SSLSocket) ss.accept();
                System.out.println( "Got connection from "+socket );
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