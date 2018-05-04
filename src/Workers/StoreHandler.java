package Workers;

import Messages.Message;
import Peer.Peer;

import java.util.concurrent.TimeUnit;

public class StoreHandler implements Runnable {

    private Message messageToSend;
    private int chunkNo;
    private int tryCounter;
    private int time;

    public StoreHandler(Message messageToSend,int chunkNo){
        this.messageToSend = messageToSend;
        this.chunkNo = chunkNo;
        this.tryCounter = 2;
        this.time = 1;
    }
    @Override
    public void run(){
        String fileIdKey = messageToSend.getFileId().trim()+"."+chunkNo;
        if(tryCounter > 5) {
            return;
        }

        if(Peer.getStateManager().getChunkTable().get(fileIdKey) == null || Peer.getStateManager().checkChunkStatus(fileIdKey) == false) {
            Peer.getMDB().sendMessage(this.messageToSend);
            time = time * 2;
            Peer.getExec().schedule(this, time, TimeUnit.SECONDS);
        }
        tryCounter++;

    }

}
