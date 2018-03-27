import java.io.IOException;

public abstract class Message {

    protected static final String CRLF = "\r\n";
    protected static final String CRLFCRLF = "\r\n\r\n";

    protected String fileId;
    protected String version;
    protected String senderId;


    public Message(String header) {
        String[] headerWords = header.split(" ");
        this.version = headerWords[1];
        this.senderId = headerWords[2];
        this.fileId = headerWords[3];
    }

    public Message(String fileId, String version, String senderId){
        this.fileId = fileId;
        this.version = version;
        this.senderId = senderId;
    }

    public byte[] getFullMessage() {
        return new byte[5];

    }

    public void action() throws IOException {}




}
