package Messages;


import Tracker.*;

public class HasFileMessage{

    private String CRLFCRLF = "\r\n\r\n";

    private String senderId;
    private String fileId;
    private String senderIp;
    

    public HasFileMessage(String header, String senderIp){

        String[] headerWords = header.split(" ");
        this.senderId = headerWords[1];
        this.fileId = headerWords[2];
        this.senderIp = senderIp;
        
    }

    public HasFileMessage(String senderId, String fileId, boolean toSend) {

        this.senderId = senderId;
        this.fileId = fileId;
    
    }


    public byte[] getFullMessage() {
        String header = "HASFILE " + this.senderId + " " + this.fileId + " " + this.CRLFCRLF;
        byte[] headerBytes = header.getBytes();
        return headerBytes;

    }

    public void action() {

        Tracker.addAvailableFile(this.senderId, this.fileId, this.senderIp);
        
    }
}