package Chunk;

import Chunk.Chunk;

public class ChunkData extends Chunk {

    private byte[] data;


    public ChunkData(int chunkNo) {
        super(chunkNo);
    }

    public byte[] getData() { return data; }

    public void setData(int size, byte[] data) {
        this.data = new byte[size];
        System.arraycopy(data, 0, this.data, 0, size);
    }



}
