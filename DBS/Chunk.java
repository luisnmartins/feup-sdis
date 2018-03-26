import java.io.File;

public class Chunk{

    private int chunkNo;
    private byte[] data;

    int getChunkNo(){
        return chunkNo;
    }

    byte[] getData() { return data; }

    void setChunkNo(int chunkNo){
        this.chunkNo = chunkNo;
    }

    void setData(byte[] data) { this.data = data; }


}
