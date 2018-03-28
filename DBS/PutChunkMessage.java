import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class PutChunkMessage extends Message implements Runnable {

    private ChunkData info;

    public PutChunkMessage(String header, byte[] body) {


        super(header);
        String[] headerWords = header.split(" ");

        ChunkData info = new ChunkData(Integer.parseInt(headerWords[4]));
        info.setReplicationDegree(Integer.parseInt(headerWords[5]));
        info.setData(body.length, body);
        this.info = info;

    }

    public PutChunkMessage(String fileId, String version, String senderId, ChunkData info, int replicationDegree) {
        super(fileId, version, senderId);
        this.info = info;
        info.setReplicationDegree(replicationDegree);
    }


    public synchronized void action() throws IOException {

        //SEND STORED MESSAGE

        if(this.senderId.equals(Peer.getPeerID())){
            return;
        }

        //CHECK IF REPLICATION DEGREE IS STILL LOWER THAN DESIRED


        //Send Stored message
        Message messageToSend = new StoredMessage(fileId, "1.0", Peer.getPeerID(), this.info.getChunkNo());
        Runnable thread = new MessageCarrier(messageToSend, "MDB");
        Peer.getExec().execute(thread);

        //Store chunk data
        String filename = "Peer " + Peer.getPeerID() + "/" +fileId+"."+info.getChunkNo();


        File file = new File(filename);
        if(!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }

        Path path = file.toPath();
        ByteBuffer buffer = ByteBuffer.wrap(info.getData());

        AsynchronousFileChannel channel = AsynchronousFileChannel.open(path, StandardOpenOption.WRITE);

        CompletionHandler handler = new CompletionHandler<Integer, Object>() {

            @Override
            public void completed(Integer result, Object attachment) {

                System.out.println(attachment + " completed and " + result + " bytes are written.");
                try {
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void failed(Throwable e, Object attachment) {

                System.out.println(attachment + " failed with exception:");
                try {
                    channel.close();
                } catch (IOException exc) {
                    exc.printStackTrace();
                }
                e.printStackTrace();
            }
        };

        channel.write(buffer,0, "Chunk saving", handler);


    }

    public byte[] getFullMessage() {

        String header = "PUTCHUNK " + version + " " + senderId + " " + fileId + " "+ info.getChunkNo() + " " + info.getReplicationDegree() +
                " " + CRLF +CRLF;


        byte[] headerBytes = header.getBytes();
        byte[] data = info.getData();
        byte[] finalByteArray = new byte[headerBytes.length+data.length];

        System.arraycopy( headerBytes, 0, finalByteArray, 0, headerBytes.length);
        System.arraycopy( data, 0, finalByteArray, headerBytes.length, data.length );

        return finalByteArray;
    }


    @Override
    public void run() {
        try {
            action();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
