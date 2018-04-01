
import java.io.*;

public class PutChunkMessage extends Message implements Runnable{

    private ChunkData info;
    private boolean isFromReclaim = false;

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

        String fileIdKey = fileId.trim()+"."+info.getChunkNo();

        if(Peer.getStateManager().hasFileById(fileId) && isFromReclaim){
            Peer.getStateManager().updateChunkRep(fileIdKey,info.getReplicationDegree()); //update desired replication degree
            Peer.getStateManager().updateChunkSize(fileIdKey, info.getData().length);
            return;
        }
            

        //check if the current chunk already exists in the peer table
        if(Peer.getStateManager().chunkExists(fileIdKey)) {

            Peer.getStateManager().updateChunkRep(fileIdKey,info.getReplicationDegree()); //update desired replication degree
            Peer.getStateManager().updateChunkSize(fileIdKey, info.getData().length);
            if(this.senderId.equals(Peer.getPeerID()))
                return;

            //check if peer has a copy of the current chunk data
            if(Peer.getStateManager().storedChunk(fileIdKey)) {
                //Send Stored message
                Message messageToSend = new StoredMessage(fileId, "1.0", Peer.getPeerID(), this.info.getChunkNo());
                Runnable thread = new MessageCarrier(messageToSend, "MC");
                Peer.getExec().execute(thread);
                return;

            }  //check if current replication degree >= desired replication degree
            else if(Peer.getStateManager().checkChunkStatus(fileIdKey) || !Peer.getStateManager().canStore(info.getData().length)) {
                return;

            } else{ //if there's not data stores and sends message

                //Send Stored message
                Message messageToSend = new StoredMessage(fileId, "1.0", Peer.getPeerID(), this.info.getChunkNo());
                Runnable thread = new MessageCarrier(messageToSend, "MC");
                Peer.getExec().execute(thread);

                //Store chunk data
                String pathname = "Peer " + Peer.getPeerID() + "/" +fileId+"."+info.getChunkNo();
                // System.out.println("NEW SAVE CHUNK: "+info.getChunkNo());

                FileManager file = new FileManager(pathname);
                try {
                    file.saveChunk(info);
                }catch(IOException e){
                    e.printStackTrace();
                }

                Peer.getStateManager().addBackedUpFile(fileIdKey);

            }


        } else {

            if(!Peer.getStateManager().canStore(info.getData().length)){
                System.out.println("CANT STORE" + info.getData().length);
                return ;
            }
            ChunkInfo chunkInfo = new ChunkInfo(info.getChunkNo(),0,info.getReplicationDegree(),info.getData().length);
            Peer.getStateManager().addChunk(fileIdKey,chunkInfo);

            if(this.senderId.equals(Peer.getPeerID()))
                return;

            //Send Stored message
            Message messageToSend = new StoredMessage(fileId, "1.0", Peer.getPeerID(), this.info.getChunkNo());
            Runnable thread = new MessageCarrier(messageToSend, "MC");
            Peer.getExec().execute(thread);

            //Store chunk data
            String pathname = "Peer " + Peer.getPeerID() + "/" +fileId+"."+info.getChunkNo();
            // System.out.println("NEW SAVE CHUNK: "+info.getChunkNo());

            FileManager file = new FileManager(pathname);
            try {
                file.saveChunk(info);
            }catch(IOException e){
                e.printStackTrace();
            }

            Peer.getStateManager().addBackedUpFile(fileIdKey);


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

    public ChunkData getInfo() {
        return info;
    }

    public void setToReclaim(){
        isFromReclaim = true;
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
