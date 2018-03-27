public abstract class Message {

    protected static final String CRLF = "\r\n";

    protected String fileId;
    protected String version;
    protected String senderId;


    public Message(String message) {}

    public Message(String fileId, String version, String senderId){
        this.fileId = fileId;
        this.version = version;
        this.senderId = senderId;
    }

    public String getMessage() {
        return "Teste";

    }




}
