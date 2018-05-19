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
        //this.senderIp = senderIp;

        //TODO: Delete
        if(this.senderId.equals("abc123")){
            this.senderIp = "444.55.66";
        }else if(this.senderId.equals("abc456")){
            this.senderIp = "777.88.99";
        }
        
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

        int res = Tracker.addAvailableFile(this.senderId, this.fileId, this.senderIp);

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