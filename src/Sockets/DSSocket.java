package Sockets;

import javax.net.ssl.*;
import java.io.*;
import java.net.InetAddress;
import java.security.GeneralSecurityException;
import java.net.UnknownHostException;
import java.nio.charset.*;
import java.lang.ProcessBuilder;
import Peer.Peer;
/**
 * DRSocket
 */
public class DSSocket extends SecureSocket{

    protected String host;
    protected InetAddress address;

    public DSSocket(int port,String host)throws UnknownHostException{
        super();
        this.port = port;
        this.host = host;
        this.address = InetAddress.getByName(this.host);
        /*try {
            SSLSocketFactory sslFact = (SSLSocketFactory)SSLSocketFactory.getDefault();
            this.socket = (SSLSocket) sslFact.createSocket(host,port);  




            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            // Send messages to the server through
            // the OutputStream
            // Receive messages from the server
            // through the InputStream
        } catch (Exception e) {
            //TODO: handle exception 
        }*/

      

    }
     public void startSSLSocket() throws GeneralSecurityException, IOException{

        //SSLContext sslContext = SSLContext.getInstance("SSL");
        try{
            SSLSocketFactory sslFact = (SSLSocketFactory) SSLSocketFactory.getDefault();
        this.socket = (SSLSocket) sslFact.createSocket(this.address,this.port);
        System.out.println("Created socket");
        //this.socket.getSSLParameters().setEndpointIdentificationAlgorithm("HTTPS");

        OutputStream out = socket.getOutputStream();
        InputStream in = socket.getInputStream();

        out.write(new String("Ola Peer1").getBytes(Charset.forName("UTF-8")));


        }catch(Exception e){
            e.printStackTrace();
        }
        

    }

    public void connect(String connectFrom,String connectTo){
        try{
            setupSocketKeyStore(connectFrom);
            setupPublicKeyStore(connectTo);
            setupSSLContext();

            SSLSocketFactory sf = sslContext.getSocketFactory();
            this.socket = (SSLSocket) sf.createSocket(this.host,this.port);

            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            din = new DataInputStream(in);
            dout = new DataOutputStream(out);

        }catch(GeneralSecurityException gse){
            gse.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void run(){
        while(true){
            
        }
    }

      

}