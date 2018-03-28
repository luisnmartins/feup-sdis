
public abstract class Chunk{

    private int chunkNo;
    private int replicationDegree;


    public Chunk(int chunkNo) {
        this.chunkNo = chunkNo;
    }

    int getChunkNo(){
        return chunkNo;
    }

    int getReplicationDegree() { return this.replicationDegree;}

    void setReplicationDegree(int replicationDegree) { this.replicationDegree = replicationDegree;}



}
