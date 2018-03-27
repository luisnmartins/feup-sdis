
public class Chunk{

    private int chunkNo;
    private byte[] data;

    public Chunk(int chunkNo) {
        this.chunkNo = chunkNo;
    }

    int getChunkNo(){
        return chunkNo;
    }

    byte[] getData() { return data; }

    void setChunkNo(int chunkNo){
        this.chunkNo = chunkNo;
    }

    void setData(int size, byte[] data) { this.data = new byte[size];
                                        System.arraycopy(data, 0, this.data, 0, size); }


}
