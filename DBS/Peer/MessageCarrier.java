package Peer;

import Messages.Message;
import Peer.Peer;
import Workers.StoreHandler;

import java.util.concurrent.TimeUnit;

public class MessageCarrier implements  Runnable{

    private Message message;
    private String channelToSend;
    private int chunkNo;

    public MessageCarrier(Message message,String type){
        this.message = message;
        this.channelToSend = type;
    }

    public MessageCarrier(Message message,String type,int chunkNo){
        this.message = message;
        this.channelToSend = type;
        this.chunkNo = chunkNo;
    }

    /**
     * Sends message to the specified socket
     */
    @Override
    public void run() {
        System.out.println("Sending: " + this.message.getMessageHeader());
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
