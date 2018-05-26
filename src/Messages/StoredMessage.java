/*package Messages;

import Chunk.ChunkInfo;
import Peer.Peer;

public class StoredMessage extends Message {

    private int chunkNo;

    public StoredMessage(String fileId, String version, String senderId,int chunkNo) {
        super(fileId,version,senderId);
        this.chunkNo = chunkNo;
    }

    public StoredMessage(String header){
        super(header);
        String[] headerWords = header.split(" ");
        this.chunkNo = Integer.parseInt(headerWords[4]);


    }

    public void action(){

        String fileIdKey = this.fileId.trim()+"."+this.chunkNo;


        if(Peer.getStateManager().chunkExists(fileIdKey)){

            if(!Peer.getStateManager().getChunkInfo(fileIdKey).isStored(this.senderId)) {
                Peer.getStateManager().updateChunk(fileIdKey);
                Peer.getStateManager().updateChunkInfoPeer(fileIdKey, this.senderId);
            }

        }else{
            ChunkInfo chunkInfo = new ChunkInfo(this.chunkNo);
            chunkInfo.addStorePeer(this.senderId);
            Peer.getStateManager().addChunk(fileIdKey,chunkInfo);
        }
    }

    public byte[] getFullMessage(){
        String header = "STORED " + version + " " + senderId + " " + fileId + " "+ chunkNo +
                " " + CRLF +CRLF;
        return header.getBytes();
    }

    public int getChunkNo() {
        return chunkNo;
    }

    public String getMessageHeader(){
        return "STORED " + this.version + " " + this.senderId + " " + this.fileId + " " + this.chunkNo;
    }
}*/
