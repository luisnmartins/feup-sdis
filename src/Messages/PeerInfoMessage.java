package Messages;


import Tracker.*;

public class PeerInfoMessage{

    private String CRLFCRLF = "\r\n\r\n";

    private String address;
    private int port; 

    public PeerInfoMessage(String header){

        String[] headerWords = header.split(" ");
        this.address = headerWords[1];
        this.port = Integer.parseInt(headerWords[2]); 
               
    }

    public PeerInfoMessage(String address, int port){

        this.address = address;
        this.port = port;
    
    }

    public byte[] getFullMessage() {
        String header = "PEERINFO " + this.address + " " + this.port + " " +this.CRLFCRLF;
        byte[] headerBytes = header.getBytes();
        return headerBytes;

    }

    public void action() {

        
    }
}