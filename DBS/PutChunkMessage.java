import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class PutChunkMessage extends Message implements Runnable {

    private Chunk info;
    private int replicationDegree;

    public PutChunkMessage(String header, byte[] body) {


        super(header);
        String[] headerWords = header.split(" ");

        Chunk info = new Chunk(Integer.parseInt(headerWords[4]));
        info.setData(body.length, body);
        this.info = info;
        this.replicationDegree = Integer.parseInt(headerWords[5]);

    }

    public PutChunkMessage(String fileId, String version, String senderId, Chunk info, int replicationDegree) {
        super(fileId, version, senderId);
        this.info = info;
        this.replicationDegree = replicationDegree;
    }


    public synchronized void action() throws IOException {

        //SLEEP
        //SEND STORED MESSAGE

        if(this.senderId.equals(Peer.getPeerID())){
            return;
        }

        //CHECK IF REPLICATION DEGREE IS STILL LOWER THAN DESIRED

        //STORE FILE
        String filename = "Peer " + Peer.getPeerID() + "/" +fileId+"."+info.getChunkNo();


        File file = new File(filename);
        file.getParentFile().mkdirs();

        try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(info.getData());
        }

        Message messageToSend = new StoredMessage(fileId, "1.0", Peer.getPeerID(), this.info.getChunkNo());
        Runnable thread = new MessageCarrier(messageToSend, "MDB");
        Peer.getExec().execute(thread);
        /*Path path = Paths.get(filename);
        AsynchronousFileChannel channel = AsynchronousFileChannel.open(path, StandardOpenOption.WRITE);

        CompletionHandler handler = new CompletionHandler<Integer,ByteBuffer>() {

            @Override
            public void completed(Integer result, ByteBuffer attachment) {

                System.out.println(attachment + " completed and " + result + " bytes are written.");
            }
            @Override
            public void failed(Throwable e, ByteBuffer attachment) {

                System.out.println(attachment + " failed with exception:");
                e.printStackTrace();
            }
        };

        channel.write(buffer,0,buffer,handler);

        channel.close();*/

    }

    public byte[] getFullMessage() {
        String header = "PUTCHUNK " + version + " " + senderId + " " + fileId + " "+ info.getChunkNo() + " " + replicationDegree +
                " " + CRLF +CRLF;

        ByteArrayOutputStream finalOutputStream = new ByteArrayOutputStream();
        byte[] headerBytes = header.getBytes();
        byte[] data = info.getData();

        try {
            finalOutputStream.write(headerBytes);
            finalOutputStream.write(data);
        } catch (IOException e) {
            System.err.println("Error create PutChunk Message");
            e.printStackTrace();
        }

        byte[] finalByteArray = finalOutputStream.toByteArray();
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
