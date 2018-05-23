package Messages;


import Tracker.*;

public class OnlineMessage{

    private String CRLFCRLF = "\r\n\r\n";

    private String senderId;    

    public OnlineMessage(String header){

        String[] headerWords = header.split(" ");
        this.senderId = headerWords[1];        
    }

    public OnlineMessage(String senderId, boolean toSend){

        this.senderId = senderId;    
    }

    public byte[] getFullMessage() {
        String header = "ONLINE " + this.senderId + " " + this.CRLFCRLF;
        byte[] headerBytes = header.getBytes();
        return headerBytes;
    }

    public void action() {

        int res = Tracker.refreshOnlinePeer(this.senderId);
        
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