import java.util.concurrent.Callable;

public class StoreHandler implements Callable {

    private Message messageToSend;
    private int chunkNo;

    StoreHandler(Message messageToSend,int chunkNo){
        this.messageToSend = messageToSend;
        this.chunkNo = chunkNo;
    }
    @Override
    public Object call() throws Exception {

        String fileIdKey = messageToSend.fileId+"."+chunkNo;
        return Peer.getStateManager().checkChunkStatus(fileIdKey);
    }
}
