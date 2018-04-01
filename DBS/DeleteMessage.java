import java.io.File;
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

    public void action() throws IOException {

        String peerID = Peer.getPeerID();

        if(peerID.equals(this.senderId)){
            return;
        }

        List<String> filesStored = Peer.getStateManager().getBackedUpFiles();
        for(int i = 0; i< filesStored.size();i++){
            String pathname =  "Peer " + Peer.getPeerID() + "/" +filesStored.get(i);
            FileManager manager = new FileManager();
            Peer.getStateManager().deleteBackedUpFile(filesStored.get(i));
            manager.deleteFile(pathname);
            i--;
            Peer.getStateManager().removeChunks(fileId);
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
}
