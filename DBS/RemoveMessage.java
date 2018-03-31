public class RemoveMessage extends Message{

    private int chunkNo;

    public RemoveMessage(String fileId,String version, String senderId,int chunkNo){
        super(fileId,version,senderId);
        this.chunkNo = chunkNo;
    }

    public RemoveMessage(String header){
        super(header);
        String[] headerWords = header.split(" ");
        this.chunkNo = Integer.parseInt(headerWords[4]);
    }

    public void action(){

    }

    public byte[] getFullMessage(){
        String header = "REMOVED " + version + " " + senderId + " " + fileId + " "+ chunkNo +
                " " + CRLF +CRLF;
        return header.getBytes();
    }

    /**
     * @return the chunkNo
     */
    public int getChunkNo() {
        return chunkNo;
    }
}