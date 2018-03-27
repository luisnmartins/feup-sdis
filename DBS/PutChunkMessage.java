import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class PutChunkMessage extends Message {

    private Chunk info;
    private int replicationDegree;

    public PutChunkMessage(String header, byte[] body) {

        super(header);
        String[] headerWords = header.split(" ");

        Chunk info = new Chunk(Integer.parseInt(headerWords[4]));
        info.setData(body.length, body);
        this.info = info;
        this.replicationDegree = Integer.parseInt(headerWords[5]);

        try {
            action();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void action() throws IOException {

        String filename = "CHUNKS/"+fileId+"."+info.getChunkNo();

        try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(info.getData());
        }

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

    public PutChunkMessage(String fileId, String version, String senderId, Chunk info, int replicationDegree) {
        super(fileId, version, senderId);
        this.info = info;
        this.replicationDegree = replicationDegree;
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
        System.out.println(finalByteArray.length);
        return finalByteArray;
    }




}
