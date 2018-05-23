package Messages;


import Tracker.*;
import java.util.*;

public class GetFileMessage{

    private String CRLFCRLF = "\r\n\r\n";

    private String senderId;
    private String fileId;    

    public GetFileMessage(String header){

        String[] headerWords = header.split(" ");
        this.senderId = headerWords[1];
        this.fileId = headerWords[2];        
    }

    public GetFileMessage(String senderId, String fileId) {

        this.senderId = senderId;
        this.fileId = fileId;
    
    }


    public byte[] getFullMessage() {
        String header = "GETFILE " + this.senderId + " " + this.fileId + " " + this.CRLFCRLF;
        byte[] headerBytes = header.getBytes();
        return headerBytes;

    }

    public void action() {

        ArrayList<PeerInfo> filePeers = Tracker.getAvailableFile(this.senderId, this.fileId);

        if(filePeers == null){
            String header = "ERROR" + " " + this.CRLFCRLF;
            byte[] headerBytes = header.getBytes();
            //TODO: send error
        }
        else{
            for(int i = 0; i < filePeers.size(); i++){
                System.out.println("TRACKER - Peer: " + filePeers.get(i).getAddress() + " " +  filePeers.get(i).getPort());
                PeerInfoMessage peerinfo = new PeerInfoMessage(filePeers.get(i).getAddress(), filePeers.get(i).getPort());
                //TODO: send peerinfo
                
            }

            String header = "CLOSE" + " " + this.CRLFCRLF;
            byte[] headerBytes = header.getBytes();
            //TODO: send close
        }
       
        
    }
}