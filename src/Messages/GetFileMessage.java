package Messages;


import Tracker.*;
import java.util.*;

public class GetFileMessage{

    private String CRLFCRLF = "\r\n\r\n";

    private String senderId;
    private String fileId;
    private String senderIp;
    

    public GetFileMessage(String header, String senderIp){

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

    public GetFileMessage(String senderId, String fileId, boolean toSend) {

        this.senderId = senderId;
        this.fileId = fileId;
    
    }


    public byte[] getFullMessage() {
        String header = "GETFILE " + this.senderId + " " + this.fileId + " " + this.CRLFCRLF;
        byte[] headerBytes = header.getBytes();
        return headerBytes;

    }

    public void action() {

        ArrayList<PeerInfo> filePeers = Tracker.getAvailableFile(this.senderId, this.fileId, this.senderIp);

        if(filePeers == null){
            String header = "ERROR" + " " + this.CRLFCRLF;
            byte[] headerBytes = header.getBytes();
            //TODO: send error
        }
        else{
            System.out.println("TRACKER - Num: " + filePeers.size());
            PeerInfoSizeMessage peerinfosize = new PeerInfoSizeMessage(filePeers.size());
            //TODO: send peerinfosize

            for(int i = 0; i < filePeers.size(); i++){
                System.out.println("TRACKER - Peer: " + filePeers.get(i).getAddress() + " " +  filePeers.get(i).getPort());
                PeerInfoMessage peerinfo = new PeerInfoMessage(filePeers.get(i).getAddress(), filePeers.get(i).getPort());
                //TODO: send peerinfo
                
            }
        }
       
        
    }
}