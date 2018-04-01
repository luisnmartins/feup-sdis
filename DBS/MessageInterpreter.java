import javafx.util.Pair;

import java.net.Inet4Address;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class MessageInterpreter implements Runnable {

    private static final byte CR = 0xD;
    private static  final byte LF = 0xA;

    private String header;
    private byte[] body;
    private BlockingQueue<Pair<Integer,byte[]>> bQueue;

    public MessageInterpreter(){bQueue = new LinkedBlockingQueue<>();}

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
    }

    @Override
    public void run() {
        while(true){

            try {
                Pair<Integer,byte[]> pair  = bQueue.take();
                this.separateMessage(pair.getKey(),pair.getValue());
                String messageType = this.header.substring(0,this.header.indexOf(" "));

                switch (messageType){
                    case "PUTCHUNK": {
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
                            System.out.println(header);
                            System.out.println(body.length);
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
                    case "REMOVED": {
                        RemoveMessage removeMessage = new RemoveMessage(header);
                        removeMessage.action();
                        break;
                    }
                }
            } catch (InterruptedException e) {
                continue;
            }



        }

    }

    public void putInQueue(Pair<Integer,byte[]> pair){
        try {
            this.bQueue.put(pair);
        } catch (InterruptedException e) {
            return;
        }
    }
}
