import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class PutChunkMessage extends Message {

    private Chunk info;
    private int replicationDegree;

    public PutChunkMessage(String message) {

        super(message);

        String messageHeader =  message.substring(0, message.lastIndexOf("\r\n"));
        String[] headerWords = messageHeader.split(" ");
        String version = headerWords[1];
        String senderId = headerWords[2];
        String fileId = headerWords[3];
        this.fileId = fileId;
        this.version = version;
        this.senderId = senderId;

        Chunk info = new Chunk(Integer.parseInt(headerWords[4]));
        byte[] data = message.substring(message.lastIndexOf("\r\n"), message.length()-1).getBytes();
        info.setData(data);
        this.info = info;
        this.replicationDegree = Integer.parseInt(headerWords[5]);

        try {
            action();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void action() throws IOException {

        System.out.println("action called");
        /*ByteBuffer buffer = ByteBuffer.wrap(info.getData());
        Path path = Paths.get(this.fileId+"_"+this.info.getChunkNo());
        AsynchronousFileChannel channel = AsynchronousFileChannel.open(path, StandardOpenOption.WRITE);

        CompletionHandler handler = new CompletionHandler<Integer,Object>() {

            @Override
            public void completed(Integer result, Object attachment) {

                System.out.println(attachment + " completed and " + result + " bytes are written.");
            }
            @Override
            public void failed(Throwable e, Object attachment) {

                System.out.println(attachment + " failed with exception:");
                e.printStackTrace();
            }
        };

        channel.write(buffer, 0, "Write operation ALFA", handler);

        channel.close();*/

    }

    public PutChunkMessage(String fileId, String version, String senderId, Chunk info, int replicationDegree) {
        super(fileId, version, senderId);
        this.info = info;
        this.replicationDegree = replicationDegree;
    }

    public String getMessage() {
        return "PUTCHUNK " + version + " " + senderId + " " + fileId + " "+ info.getChunkNo() + " " + replicationDegree +
                " " + CRLF +CRLF+info.getData();
    }







}
