public class StoredMessage extends Message{

    private int chunkId;

    public StoredMessage(String fileId, String version, String senderId,int chunkNo) {
        super(fileId,version,senderId);
        this.chunkId = chunkNo;
    }

    public byte[] getFullMessage(){
        String header = "STORED " + version + " " + senderId + " " + fileId + " "+ chunkId +
                " " + CRLF +CRLF;
        return header.getBytes();
    }


}
