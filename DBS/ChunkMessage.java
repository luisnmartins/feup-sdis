
import java.io.IOException;

public class ChunkMessage extends Message implements Runnable{

    private ChunkData info;

    public ChunkMessage(String header) {
        super(header);
    }

    public ChunkMessage(String fileId, String version, String senderId,ChunkData info) {
        super(fileId, version, senderId);
        this.info = info;
        String pathname = "Peer " + Peer.getPeerID() + "/" + fileId + "." + info.getChunkNo();
        FileManager manager = new FileManager(pathname);
        try {
            byte[] buffer = manager.readEntireFileData();
            info.setData(buffer.length, buffer);
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public byte[] getFullMessage() {

        String header = "PUTCHUNK " + version + " " + senderId + " " + fileId + " "+ info.getChunkNo() + " "  +
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

    }
}
