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
                Runnable handler = new StoreHandler(this.message,chunkNo);
                Peer.getExec().schedule(handler,1,TimeUnit.SECONDS);

                break;
            }
            case "MDR":{
                Peer.getMDR().sendMessage(this.message);
                break;
            }

        }
    }
}
