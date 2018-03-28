
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MessageInterpreter implements Runnable {

    private static final byte CR = 0xD;
    private static  final byte LF = 0xA;

    private String header;
    private byte[] body;

    public MessageInterpreter(int size, byte[] data){

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

        System.out.println(this.header);

        if(size > i+3) {
            this.body = new byte[size - i - 4];
            System.arraycopy(data, i + 4, this.body, 0, size - i - 4);

        } else {
            this.body = null;
        }


    }

    @Override
    public void run() {
        String messageType = this.header.substring(0,this.header.indexOf(" "));
        Message receivedMessage;
        switch (messageType){
            case "PUTCHUNK": {
                Runnable putchunk = new PutChunkMessage(header, body);
                Random rand = new Random();
                int randTime = rand.nextInt(399);
                Peer.getExec().schedule(putchunk, randTime, TimeUnit.MILLISECONDS);
                break;
            }
        }

    }


}
