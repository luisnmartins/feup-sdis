package Messages;


import Tracker.*;

public class RegisterMessage{

    private String CRLFCRLF = "\r\n\r\n";

    private String senderId;
    private String address;
    private int port;
    private String senderIp;
    

    public RegisterMessage(String header, String senderIp){

        String[] headerWords = header.split(" ");
        this.senderId = headerWords[1];
        this.address = headerWords[2];
        this.port = Integer.parseInt(headerWords[3]);
        this.senderIp = senderIp;
        
    }

    public RegisterMessage(String senderId, String address, int port){

        this.senderId = senderId;
        this.address = address;
        this.port = port;
    
    }

    public byte[] getFullMessage() {
        String header = "REGISTER " + this.senderId + " " + this.address + " " + this.port + " " + this.CRLFCRLF;
        byte[] headerBytes = header.getBytes();
        return headerBytes;

    }

    public void action() {


        Tracker.addOnlinePeer(this.senderId, this.address, this.port, this.senderIp);

        
        
    }
}