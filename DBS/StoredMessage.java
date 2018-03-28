
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

    public void InterpretStore(){

        if(this.senderId.equals(Peer.getPeerID())){
            return;
        }
        String fileIdKey = this.fileId.trim()+"."+this.chunkNo;
        if(Peer.getStateManager().chunkExists(fileIdKey)){
            Peer.getStateManager().updateChunk(fileIdKey);

        }else{
            ChunkInfo chunkInfo = new ChunkInfo(this.chunkNo);
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
}
