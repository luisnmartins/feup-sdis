public class MessageCarrier implements  Runnable{

    private String message;
    private String channelToSend;

    MessageCarrier(String message,String type){
        this.message = message;
        this.channelToSend = type;
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
                break;
            }
            case "MDR":{
                Peer.getMDR().sendMessage(this.message);
                break;
            }

        }
    }
}
