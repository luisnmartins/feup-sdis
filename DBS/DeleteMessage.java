import java.io.IOException;

public class DeleteMessage extends Message {



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

        if(peerID == this.senderId){
            return;
        }

        String pathname = "Peer "+peerID+"/"+this.fileId+"."+this.
    }
}
