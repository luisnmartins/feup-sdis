package Messages;


import Tracker.*;

public class HasFileMessage{

    private String CRLFCRLF = "\r\n\r\n";

    private String senderId;
    private String fileId;  

    public HasFileMessage(String header){

        String[] headerWords = header.split(" ");
        this.senderId = headerWords[1];
        this.fileId = headerWords[2];
        
    }

    public HasFileMessage(String senderId, String fileId) {

        this.senderId = senderId;
        this.fileId = fileId;
    
    }


    public byte[] getFullMessage() {
        String header = "HASFILE " + this.senderId + " " + this.fileId + " " + this.CRLFCRLF;
        byte[] headerBytes = header.getBytes();
        return headerBytes;

    }

    public void action() {

        int res = Tracker.addPeerToFile(this.senderId, this.fileId);

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