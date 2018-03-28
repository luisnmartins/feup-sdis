public class ChunkData extends Chunk{

    private byte[] data;


    public ChunkData(int chunkNo) {
        super(chunkNo);
    }

    byte[] getData() { return data; }

    void setData(int size, byte[] data) { this.data = new byte[size];
        System.arraycopy(data, 0, this.data, 0, size); }

}
