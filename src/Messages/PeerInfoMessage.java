package Messages;

import Peer.*;
import Sockets.SenderSocket;
import Tracker.*;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;

public class PeerInfoMessage extends MessageTemp{

    private String CRLFCRLF = "\r\n\r\n";

    private String address;
    private int port; 
    private byte[] key;

    public PeerInfoMessage(String header, byte[] body){

        String[] headerWords = header.split(" ");
        this.address = headerWords[1];
        this.port = Integer.parseInt(headerWords[2]);
        this.key = body; 
               
    }

    public PeerInfoMessage(String address, int port, byte[] key){

        this.address = address;
        this.port = port;
        this.key = key;
    
    }

    public byte[] getFullMessage() {
        String header = "PEERINFO " + this.address + " " + this.port + " " +this.CRLFCRLF;
        byte[] headerBytes = header.getBytes();
        byte[] finalByteArray = new byte[headerBytes.length+this.key.length];
        System.arraycopy( headerBytes, 0, finalByteArray, 0, headerBytes.length);
        System.arraycopy( key, 0, finalByteArray, headerBytes.length, key.length );        
        return finalByteArray;

    }

    public int action() {
    
        //clearbyte[] key = readPublicKey();
        try{
            InetAddress inet = InetAddress.getLocalHost();
            String address = inet.getHostAddress();
            int port = Peer.getControlReceiver().getServerSocket().getLocalPort();
            MessageTemp message = new HasFileMessage(Peer.getPeerID(), "abc");
    
            SenderSocket channelStarter = new SenderSocket(Peer.getTrackerPort(), Peer.getTrackerIP());
    
            channelStarter.connect(Peer.getPeerID(),"tracker",true,key);
    
            channelStarter.getHandler().getWriter().write(new String("Ola").getBytes());
        }catch(IOException e){

        }
      
        return 0;


    }
}