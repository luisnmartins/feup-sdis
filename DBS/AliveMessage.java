
public class AliveMessage extends Message{

    public AliveMessage(String header) {
        super(header);
    }

    public AliveMessage(String fileId, String version, String senderId) {

        super(fileId, version, senderId);

    }

    public byte[] getFullMessage() {
        String header = "ALIVE " + version + " " + senderId + " " + fileId + " " + CRLF + CRLF;

        byte[] headerBytes = header.getBytes();

        return headerBytes;

    }


    public void action() {

        String peerID = Peer.getPeerID();

        if (peerID.equals(this.senderId)) {
            return;
        }

        if (Peer.getStateManager().isADeletedFile(fileId)) {
            Message messageToDelete = new DeleteMessage(fileId, version, peerID);
            Runnable thread = new MessageCarrier(messageToDelete, "MC");
            Peer.getExec().execute(thread);
        }
    }

}