import ChunkInfo.ChunkInfo;

public class StoredMessage extends Message{

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

    public void interpretStore(){
        if(Peer.getStateManager().chunkExists(this.chunkNo)){
            Peer.getStateManager().updateChunk(this.chunkNo);
        }else{
            ChunkInfo chunkInfo = new ChunkInfo(this.fileId);
            Peer.getStateManager().addChunk(this.chunkNo,chunkInfo);
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
}
