package Peer;

import Messages.*;
import Peer.Peer;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.AbstractMap.SimpleEntry;

public class MessageInterpreter implements Runnable {

    private static final byte CR = 0xD;
    private static  final byte LF = 0xA;

    private String header;
    private byte[] body;
    private BlockingQueue<SimpleEntry<Integer,byte[]>> bQueue;

    public MessageInterpreter(){bQueue = new LinkedBlockingQueue<>();}

    /**
     * Separates the message received in the necessary parts
     */
    public void separateMessage(int size,byte[] data){
        int i=0;
        for(; i<size; i++) {
            if(i <= size-5) {
                if (data[i] == CR && data[i + 1] == LF && data[i + 2] == CR && data[i + 3] == LF) {
                    break;
                }
            }
        }
        byte[] headerByte = new byte[i];
        System.arraycopy(data, 0, headerByte, 0, i-1);
        this.header = new String(headerByte);
        this.header = this.header.trim();


        if(size > i+3) {
            this.body = new byte[size - i - 4];
            System.arraycopy(data, i + 4, this.body, 0, size - i - 4);

        } else {
            this.body = null;
        }

        System.out.println("Received: "  + this.header);
    }

    /**
     * 
     * Takes message from queue and checks the message type and acts accordingly
     */
    @Override
    public void run() {
        while(true){

            try {
                SimpleEntry<Integer,byte[]> pair  = bQueue.take();
                this.separateMessage(pair.getKey(),pair.getValue());
                String messageType = this.header.substring(0,this.header.indexOf(" "));

                switch (messageType){
                    /*case "PUTCHUNK": {
                        Runnable putchunk = new PutChunkMessage(header, body);
                        Random rand = new Random();
                        int randomTime = rand.nextInt(399);
                        Peer.getExec().schedule(putchunk,randomTime,TimeUnit.MILLISECONDS);
                        break;
                    }
                    case "STORED":{
                        StoredMessage stored = new StoredMessage(header);
                        stored.action();
                        break;
                    }
                    case "GETCHUNK": {
                        GetChunkMessage getChunkMessage = new GetChunkMessage(header);
                        getChunkMessage.action();
                        break;
                    }
                    case "CHUNK":{
                        try {
                            Message chunkMessage = new ChunkMessage(header, body);
                            chunkMessage.action();
                        }catch (Throwable e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case "DELETE":{
                       DeleteMessage delete = new DeleteMessage(header);
                       Peer.getExec().execute(delete);
                       break;
                    }
                    case "DELETED":{
                        DeletedMessage deleted = new DeletedMessage(header);
                        Peer.getExec().execute(deleted);
                        break;
                    }
                    case "REMOVED": {
                        RemoveMessage removeMessage = new RemoveMessage(header);
                        removeMessage.action();
                        break;
                    }
                    case "ALIVE": {
                        AliveMessage alive = new AliveMessage(header);
                        alive.action();
                        break;
                    }*/
                    case "SUCCESS": {
                        //TODO: Success Action
                        break;
                    }
                    case "ERROR": {
                        //TODO: Error Action
                        break;
                    }
                    case "CLOSE": {
                        //TODO: Close Action
                        break;
                    }
                    //TRACKER
                    case "REGISTER": {
                        RegisterMessage register = new RegisterMessage(header);
                        register.action();
                        break;
                    }
                    case "ONLINE": {
                        OnlineMessage online = new OnlineMessage(header);
                        online.action();
                        break;
                    }
                    case "HASFILE": {                      
                        HasFileMessage hasfile = new HasFileMessage(header);
                        hasfile.action();
                        break;
                    }
                    case "NOFILE": {                      
                        NoFileMessage nofile = new NoFileMessage(header);
                        nofile.action();
                        break;
                    }
                    case "GETFILE": {                      
                        GetFileMessage getfile = new GetFileMessage(header);
                        getfile.action();
                        break;
                    }
                    //PEER
                    case "PEERINFO": {                      
                        PeerInfoMessage peerinfo = new PeerInfoMessage(header);
                        peerinfo.action();
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }




        }

    }

    public void putInQueue(SimpleEntry<Integer,byte[]> pair){
        try { 
            this.bQueue.put(pair);
        } catch (InterruptedException e) {
            e.printStackTrace();            
            return;
        }
    }
}
