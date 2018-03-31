import java.util.Random;
import java.util.concurrent.TimeUnit;

public class GetChunkMessage extends Message{

    private int chunkNo;

    public GetChunkMessage(String header) {
        super(header);
        String[] headerWords = header.split(" ");
        this.chunkNo = Integer.parseInt(headerWords[4]);
        System.out.println("GETCHUNK "+ this.chunkNo);
    }

    public GetChunkMessage(String version, String senderId, String fileId, int chunkNo) {
        super(fileId, version, senderId);
        this.chunkNo = chunkNo;
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

        Peer.getStateManager().addChunkToRestore(chunkNo);

        if(Peer.getStateManager().hasBackedUpChunk(this.fileId,this.chunkNo) != null){

            Runnable chunkMessage = new ChunkMessage(this.fileId,"1.0",Peer.getPeerID(),new ChunkData(this.chunkNo));
            Random rand = new Random();
            int randomTime = rand.nextInt(399);
            Peer.getExec().schedule(chunkMessage,randomTime, TimeUnit.MILLISECONDS);

        }

    }
}
