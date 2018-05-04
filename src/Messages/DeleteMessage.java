package Messages;

import Peer.Peer;
import Peer.FileManager;
import Peer.MessageCarrier;

import java.io.IOException;
import java.util.List;

public class DeleteMessage extends Message implements Runnable {

    public DeleteMessage(String header) {
            super(header);
    }

    public DeleteMessage(String fileId, String version, String senderId){

        super(fileId,version,senderId);

    }

    public byte[] getFullMessage() {
        String header = "DELETE " + version + " " + senderId + " " + fileId + " "+ CRLF +CRLF;

        byte[] headerBytes = header.getBytes();

        return headerBytes;

    }

    /**
     * Checks if it has the chunk stored if it completely deletes it from its record
     */
    public synchronized void action() throws IOException {

        String peerID = Peer.getPeerID();
        boolean isToSendDeleted=false;

        if(peerID.equals(this.senderId)){
            return;
        }

        List<String> filesStored = Peer.getStateManager().getBackedUpFiles();
        for(int i = 0; i< filesStored.size();i++){
            if(filesStored.get(i).contains(fileId)) {
                FileManager manager = new FileManager();
                String pathname = "Peer " + Peer.getPeerID() + "/" + filesStored.get(i);
                Peer.getStateManager().deleteBackedUpFile(filesStored.get(i));
                i--;
                manager.deleteFile(pathname);
                isToSendDeleted = true;
            }
            
        }
        Peer.getStateManager().removeChunks(fileId);


        if(this.version.equals("2.0") && isToSendDeleted) {
            Message deleted = new DeletedMessage(fileId, "1.0", Peer.getPeerID());
            Runnable thread = new MessageCarrier(deleted, "MC");
            Peer.getExec().execute(thread);
        }
    }

    @Override
    public void run() {
        try {
            this.action();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getMessageHeader(){
        return "DELETE " + this.version + " " + this.senderId + " " + this.fileId;
    }
}
