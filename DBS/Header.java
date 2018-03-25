public class Header {

    private String messageType;
    private String version;
    private String senderId;
    private String fileId;
    private String chunkNr;
    private String replicationDegree;

    private static final String CRLF = "\r\n";

    public Header(String type,String vs, String sender,String fileId,String chunk,String replicationDegree){
        this.messageType = type;
        this.version = vs;
        this.senderId = sender;
        this.fileId = fileId;
        this.chunkNr = chunk;
        this.replicationDegree = replicationDegree;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getChunkNr() {
        return chunkNr;
    }

    public String getVersion() {
        return version;
    }

    public String getFileId() {
        return fileId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReplicationDegree() {
        return replicationDegree;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getFullHeader(){
        String fullHeader = "";
        fullHeader += this.getMessageType() + " " + this.getVersion() + " " + this.getSenderId() + " "
                    + this.getFileId() +" "+ this.getChunkNr() + " " + this.getReplicationDegree() + " "
                    + this.CRLF +this.CRLF;
        return  fullHeader;
    }
}
