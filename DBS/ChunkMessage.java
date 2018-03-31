
import java.io.IOException;
import java.io.PipedWriter;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

public class ChunkMessage extends Message implements Runnable{

    private ChunkData info;

    public ChunkMessage(String header, byte[] body) {
        super(header);

        String[] headerWords = header.split(" ");
        ChunkData info = new ChunkData(Integer.parseInt(headerWords[4]));
        info.setData(body.length, body);
        this.info = info;
    }

    public ChunkMessage(String fileId, String version, String senderId,ChunkData info) {
        super(fileId, version, senderId);
        this.info = info;
        String pathname = "Peer " + Peer.getPeerID() + "/" +fileId+"."+info.getChunkNo();

        FileManager manager = new FileManager(pathname);

        try {
            byte[] buffer = manager.readEntireFileData();
            info.setData(buffer.length, buffer);
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public byte[] getFullMessage() {

        String header = "CHUNK " + version + " " + senderId + " " + fileId + " "+ info.getChunkNo() + " "  +
                " " + CRLF +CRLF;


        byte[] headerBytes = header.getBytes();
        byte[] data = info.getData();
        byte[] finalByteArray = new byte[headerBytes.length+data.length];

        System.arraycopy( headerBytes, 0, finalByteArray, 0, headerBytes.length);
        System.arraycopy( data, 0, finalByteArray, headerBytes.length, data.length );

        return finalByteArray;
    }

    @Override
    public void action() {

        if(this.senderId.equals(Peer.getPeerID()))
            return;


        ConcurrentHashMap hashed = Peer.getStateManager().getFilesTables();
        Set<String> set = hashed.keySet();
        for(String key : set){
            if(hashed.get(key).equals(fileId)){
                if(Peer.getStateManager().isChunkToRestore(info.getChunkNo())) {
                    System.out.println("Restoring chunk  "+ info.getChunkNo());
                    String pathname = "Peer " + Peer.getPeerID() + "/" + fileId + "." + info.getChunkNo();
                    FileManager manager = new FileManager(pathname);
                    try {
                        manager.saveChunk(info);
                        Peer.getStateManager().chunkReallyToRestore(info.getChunkNo());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(Peer.getStateManager().isChunkToRestoreEmpty()){
                    FileManager manager = new FileManager(key);
                    try {
                        manager.mergeChunks(fileId);
                        //TODO delete temporary files
                        //DeleteMessage tempFiles = new DeleteMessage(fileId, "1.0",Peer.getPeerID());
                        //tempFiles.action();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;

                }
                break;

            }
        }

    }

    @Override
    public void run() {

        if(Peer.getStateManager().chunkReallyToRestore(info.getChunkNo())) {
            Runnable messageToSend = new MessageCarrier(this,"MDR");
            Peer.getExec().execute(messageToSend);
        }

    }


}
