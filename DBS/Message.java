public class Message {

    private static final String CRLF = "\r\n";

    private String fileId;
    private String version;
    private String senderId;


    public Message(String fileId, String version, String senderId){
        this.fileId = fileId;
        this.version = version;
        this.senderId = senderId;
    }

    public String getPutChunk(Chunk info, int replicationDegree) {
        return "PUTCHUNK " + version + " " + senderId + " " + fileId + " "+ info.getChunkNo() + " " + replicationDegree +
                " " + CRLF +CRLF+info.getData();

    }




}
