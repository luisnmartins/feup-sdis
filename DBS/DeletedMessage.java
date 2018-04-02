

public class DeletedMessage extends Message implements Runnable{

    /**
     * Create a deletedMessage structure splitting the string message
     * @param header string message
     */
    public DeletedMessage(String header) {
        super(header);
    }

    /**
     * Create a deletedMessage structure with the given arguments
     * @param fileId id of the file which message refers to
     * @param version version of the message
     * @param senderId id of the peer that sent or will send the message
     */
    public DeletedMessage(String fileId, String version, String senderId) {

        super(fileId, version, senderId);

    }

    /**
     * Create a string message to send
     *
     * @return message to send in bytes
     */
    public byte[] getFullMessage() {
        String header = "DELETED " + version + " " + senderId + " " + fileId + " " + CRLF + CRLF;

        byte[] headerBytes = header.getBytes();

        return headerBytes;

    }


    /**
     * Delete peer id from deleted files table if peer has this file in his list
     */
    public void action() {

        String peerID = Peer.getPeerID();

        if (peerID.equals(this.senderId)) {
            return;
        }

        Peer.getStateManager().removeDeletedFilePeer(fileId, senderId);

    }

    @Override
    public void run() {
            this.action();
    }

}
