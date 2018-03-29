import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class StoreHandler implements Runnable {

    private Message messageToSend;
    private int chunkNo;
    private int tryCounter;
    private int time;

    StoreHandler(Message messageToSend,int chunkNo){
        this.messageToSend = messageToSend;
        this.chunkNo = chunkNo;
        this.tryCounter = 0;
        this.time = 1;
    }
    @Override
    public void run(){

        String fileIdKey = messageToSend.fileId+"."+chunkNo;
        if(tryCounter >4)
            return;
        if(!Peer.getStateManager().checkChunkStatus(fileIdKey)){
            Peer.getMDB().sendMessage(messageToSend);
            tryCounter++;
            time = time*2;
            Peer.getExec().schedule(this,time, TimeUnit.SECONDS);
        }
    }
}
