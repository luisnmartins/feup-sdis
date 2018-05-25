package Messages;

import Messages.*;
import Tracker.*;

public class RegisterMessage extends MessageTemp{

    private String CRLFCRLF = "\r\n\r\n";

    private String senderId;
    private String address;
    private int port;
    private byte[] key;
    

    public RegisterMessage(String header,byte[] body){
        super();
        String[] headerWords = header.split(" ");
        this.senderId = headerWords[1];
        this.address = headerWords[2];
        this.port = Integer.parseInt(headerWords[3]);
        this.key = body;       
    }

    public RegisterMessage(String senderId, String address, int port,byte[] body){
        super();
        this.senderId = senderId;
        this.address = address;
        this.port = port;
        this.key = body;
    
    }

    public byte[] getFullMessage() {
        String header = "REGISTER " + this.senderId + " " + this.address + " " + this.port + " " + this.CRLFCRLF;
        
        byte[] headerBytes = header.getBytes();
        byte[] finalArray = new byte[headerBytes.length+key.length];

        System.arraycopy(headerBytes, 0, finalArray, 0, headerBytes.length);
        System.arraycopy(key, 0, finalArray, headerBytes.length,key.length);
        return finalArray;

    }

    public void action() {

        int res = Tracker.addOnlinePeer(this.senderId, this.address, this.port,this.key);
        
        if(res == -1){
            String header = "ERROR" + " " + this.CRLFCRLF;
            byte[] headerBytes = header.getBytes();
            //TODO: send error
        
        }else if(res == 0){
            String header = "SUCCESS" + " " + this.CRLFCRLF;
            byte[] headerBytes = header.getBytes();
            //TODO: send success
        }
        
    }
}