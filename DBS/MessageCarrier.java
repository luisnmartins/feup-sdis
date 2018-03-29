import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class MessageCarrier implements  Runnable{

    private Message message;
    private String channelToSend;
    private int chunkNo;

    MessageCarrier(Message message,String type){
        this.message = message;
        this.channelToSend = type;
    }

    MessageCarrier(Message message,String type,int chunkNo){
        this.message = message;
        this.channelToSend = type;
        this.chunkNo = chunkNo;
    }

    @Override
    public void run() {
        switch (channelToSend){
            case "MC":{
                Peer.getMC().sendMessage(this.message);
                break;
            }
            case "MDB":{
                Peer.getMDB().sendMessage(this.message);
                Callable call = new StoreHandler(this.message,chunkNo);
                Future<Boolean> future = Peer.getExec().schedule(call,1,TimeUnit.SECONDS);
                try {
                    if(!future.get()){
                        Peer.getMDB().sendMessage(this.message);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                break;
            }
            case "MDR":{
                Peer.getMDR().sendMessage(this.message);
                break;
            }

        }
    }
}
