import java.util.Random;
import java.util.concurrent.TimeUnit;

public class RemoveMessage extends Message{

    private int chunkNo;

    public RemoveMessage(String fileId,String version, String senderId,int chunkNo){
        super(fileId,version,senderId);
        this.chunkNo = chunkNo;
    }

    public RemoveMessage(String header){
        super(header);
        String[] headerWords = header.split(" ");
        this.chunkNo = Integer.parseInt(headerWords[4]);
    }

    public void action(){
        if(this.senderId.equals(Peer.getPeerID())){
            return;
        }

        String fileIdKey = fileId + "." + chunkNo;
        Peer.getStateManager().updateChunkDec(fileIdKey);

        int desiredRep = Peer.getStateManager().getChunkTable().get(fileIdKey).getDesiredReplicationDegree();

        if(Peer.getStateManager().storedChunk(fileIdKey)){

            ChunkData chunk = new ChunkData(chunkNo);
            FileManager manager = new FileManager();
            String pathname = "Peer "+Peer.getPeerID()+"/"+fileIdKey;
            byte[] data_to_send = manager.getFileData(pathname);
            chunk.setData(data_to_send.length, data_to_send);


            Peer.getStateManager().updateChunkInfoPeerRemove(fileIdKey, Peer.getPeerID());
            Runnable respond = new RemoveRespond(chunk, desiredRep);
            Random rand = new Random();
            int wait_time = rand.nextInt(400);
            Peer.getExec().schedule(respond, wait_time,TimeUnit.MILLISECONDS);
        }
    }

    public byte[] getFullMessage(){
        String header = "REMOVED " + version + " " + senderId + " " + fileId + " "+ chunkNo +
                " " + CRLF +CRLF;
        return header.getBytes();
    }

    /**
     * @return the chunkNo
     */
    public int getChunkNo() {
        return chunkNo;
    }

    /*
    Mini thread checks if the replication degree desired has been restored, if it has it means it already received a putchnk
    */
   public class RemoveRespond implements Runnable{

        ChunkData chunk;
        int desiredRep;

        RemoveRespond(ChunkData chunk,int desiredRep){
            this.chunk = chunk;
            this.desiredRep = desiredRep;
        }

       @Override
       public void run() {

        String fileIdKey = fileId +"." + chunkNo;
            if(Peer.getStateManager().checkChunkStatus(fileIdKey)){
                return;
            }else{

                Message messageToSend = new PutChunkMessage(fileId, Peer.getVersion(), Peer.getPeerID(), chunk,desiredRep);
                ((PutChunkMessage) messageToSend).setToReclaim();
                Runnable putchunkthread = new MessageCarrier(messageToSend, "MDB",chunkNo);
                Peer.getExec().execute(putchunkthread);
            }
       }
   }
}