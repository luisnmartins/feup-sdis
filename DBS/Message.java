public class Message {

    private Header header;
    private byte[] body;

    public Message(Header header,byte[] body){
        this.header = header;
        this.body = body;
    }


}
