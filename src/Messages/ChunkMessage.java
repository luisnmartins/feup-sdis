/*package Messages;

import Chunk.ChunkData;
import Peer.FileManager;
import Peer.MessageCarrier;
import Peer.Peer;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
        String fileIdKey = fileId+"."+info.getChunkNo();
        for(String key : set){
            if(hashed.get(key).equals(fileId)){
                Peer.decWindow();
                if(Peer.getStateManager().isChunkToRestore(fileIdKey)) {
                    System.out.println("Restoring chunk  "+ info.getChunkNo());
                    String pathname = "Peer " + Peer.getPeerID() + "/SaveData/" + fileId + "." + info.getChunkNo();
                    FileManager manager = new FileManager(pathname);
                    try {
                        manager.saveChunk(info);
                        Peer.getStateManager().chunkReallyToRestore(fileIdKey);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return;

            }
        }
        Peer.getStateManager().chunkReallyToRestore(fileIdKey);

    }

    @Override
    public void run() {

        String fileIdKey = fileId + "." + info.getChunkNo();
        if(Peer.getStateManager().isChunkToRestore(fileIdKey)) {
            Runnable messageToSend = new MessageCarrier(this,"MDR");
            Peer.getExec().execute(messageToSend);
        }

    }

    public String getMessageHeader(){
        return "CHUNK " + this.version + " " + this.senderId + " " + this.fileId + " " + info.getChunkNo();
    }


}*/
