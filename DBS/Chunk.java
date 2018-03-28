
public abstract class Chunk{

    private int chunkNo;
    private int replicationDegree;


    public Chunk(int chunkNo) {
        this.chunkNo = chunkNo;
    }

    int getChunkNo(){
        return chunkNo;
    }

    byte[] getData() { return data; }

    int getReplicationDegree() { return this.replicationDegree;}

    void setReplicationDegree(int replicationDegree) { this.replicationDegree = replicationDegree;}

    void setData(int size, byte[] data) { this.data = new byte[size];
                                        System.arraycopy(data, 0, this.data, 0, size); }


}
