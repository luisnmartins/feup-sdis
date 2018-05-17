package Peer;

import Messages.Message;
import Peer.Peer;
import Workers.StoreHandler;
import Sockets.SenderSocket;
import Sockets.SenderSocket;

import java.util.concurrent.TimeUnit;
import java.net.UnknownHostException;

public class MessageCarrier implements  Runnable{

    private Message message;
    private String channelToSend;
    private int chunkNo;
    private int toPort;
    private String toHost;
    private String toID;

    public MessageCarrier(Message message,String type){
        this.message = message;
        this.channelToSend = type;
    }

    public MessageCarrier(Message message,String type,int chunkNo){
        this.message = message;
        this.channelToSend = type;
        this.chunkNo = chunkNo;
    }

    public MessageCarrier(Message message,int port,String host,String toPeerName){
        this.message = message;
        this.toPort = port;
        this.toHost = host;
        this.toID = toPeerName;
        this.channelToSend = "TCP";
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

            case "TCP":{
                try{
                    SenderSocket socket = new SenderSocket(toPort,toHost);
                    socket.connect(Peer.getPeerID(),this.toID);
                    Runnable senderSocket = socket;
                    Peer.getExec().execute(socket);
                }catch(UnknownHostException e){
                    e.printStackTrace();
                }
            
            }

        }
    }
}
