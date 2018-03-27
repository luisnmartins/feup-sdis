public class MessageCarrier implements  Runnable{

    private Message message;
    private String channelToSend;

    MessageCarrier(Message message,String type){
        this.message = message;
        this.channelToSend = type;
    }

    @Override
    public void run() {
        switch (channelToSend){
            case "MC":{
                Peer.getMC().sendMessage(this.message.getFullMessage());
                break;
            }
            case "MDB":{
                Peer.getMDB().sendMessage(this.message.getFullMessage());
                break;
            }
            case "MDR":{
                Peer.getMDR().sendMessage(this.message.getFullMessage());
                break;
            }

        }
    }
}
