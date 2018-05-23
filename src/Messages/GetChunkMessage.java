package Messages;

import Chunk.ChunkData;
import Peer.Peer;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class GetChunkMessage {

    private String fileId;
    private int chunkNo;

    public GetChunkMessage(String header) {
  
        String[] headerWords = header.split(" ");
        this.fileId = headerWords[1];        
        this.chunkNo = Integer.parseInt(headerWords[2]);
        
    }

    public byte[] getFullMessage(){
        String header = "GETCHUNK " + version + " " + senderId + " " + fileId + " "+ chunkNo +
                " " + CRLF +CRLF;
        return header.getBytes();
    }


    public void action(){

        if(this.senderId.equals(Peer.getPeerID())){
            return;
        }
        String fileIdKey = fileId +"." + chunkNo;
        Peer.getStateManager().addChunkToRestore(fileIdKey);

        if(Peer.getStateManager().hasBackedUpChunk(this.fileId,this.chunkNo) != null){

            Runnable chunkMessage = new ChunkMessage(this.fileId,this.version, Peer.getPeerID(),new ChunkData(this.chunkNo));
            Random rand = new Random();
            int randomTime = rand.nextInt(400);
            Peer.getExec().schedule(chunkMessage,randomTime, TimeUnit.MILLISECONDS);

        }

    }

    public String getMessageHeader(){
        return "GETCHUNK " + this.version + " " + this.senderId + " " + this.fileId + " " + chunkNo;
    }
}
