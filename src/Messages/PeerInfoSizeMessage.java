package Messages;


import Tracker.*;

public class PeerInfoSizeMessage{

    private String CRLFCRLF = "\r\n\r\n";

    private int size;
    

    public PeerInfoSizeMessage(String header){

        String[] headerWords = header.split(" ");
        this.size = Integer.parseInt(headerWords[1]);       
    }

    public PeerInfoSizeMessage(int size){

        this.size = size;
    
    }

    public byte[] getFullMessage() {
        String header = "PEERINFOSIZE " + this.size + " " + this.CRLFCRLF;
        byte[] headerBytes = header.getBytes();
        return headerBytes;

    }

    public void action() {

        
    }
}