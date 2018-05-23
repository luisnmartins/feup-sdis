package Messages;


import Tracker.*;

public class RegisterMessage{

    private String CRLFCRLF = "\r\n\r\n";

    private String senderId;
    private String address;
    private int port;
    

    public RegisterMessage(String header){

        String[] headerWords = header.split(" ");
        this.senderId = headerWords[1];
        this.address = headerWords[2];
        this.port = Integer.parseInt(headerWords[3]);       
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

        int res = Tracker.addOnlinePeer(this.senderId, this.address, this.port);
        
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