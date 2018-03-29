
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Hashtable;
import java.util.Set;

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


    public void action() throws IOException {

        //SEND STORED MESSAGE

        if(this.senderId.equals(Peer.getPeerID())){
            return;
        }

        String fileIdKey = fileId.trim()+"."+info.getChunkNo();

        if(Peer.getStateManager().chunkExists(fileIdKey) ){

            Peer.getStateManager().updateChunkRep(fileIdKey,info.getReplicationDegree());
            if(Peer.getStateManager().checkChunkStatus(fileIdKey) || Peer.getStateManager().storedChunk(fileIdKey))
                return;
        }

        //Send Stored message
        Message messageToSend = new StoredMessage(fileId, "1.0", Peer.getPeerID(), this.info.getChunkNo());
        Runnable thread = new MessageCarrier(messageToSend, "MDB");
        Peer.getExec().execute(thread);

        //Store chunk data
        String pathname = "Peer " + Peer.getPeerID() + "/" +fileId+"."+info.getChunkNo();

        FileManager file = new FileManager(pathname);
        try {
            file.saveChunk(info);
        }catch(IOException e){
            e.printStackTrace();
        }

        //updates the hashtable incrementing the chunk replicationdegree
        if(Peer.getStateManager().chunkExists(fileIdKey)){
            Peer.getStateManager().updateChunk(fileIdKey);
        }else{
            ChunkInfo chunkInfo = new ChunkInfo(info.getChunkNo(),1,info.getReplicationDegree());
            Peer.getStateManager().addChunk(fileIdKey,chunkInfo);

        }
        Peer.getStateManager().addBackupedUpFile(this.fileId, this.info.getChunkNo());

        Hashtable<String,ChunkInfo> HASH = Peer.getStateManager().getChunkTable();
        Set<String> keys = HASH.keySet();
        for(String key:keys){
            System.out.println("Chunk table key: " + key + " value "+ HASH.get(key).getChunkNo());
        }


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
