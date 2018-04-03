package Chunk;

public abstract class Chunk implements java.io.Serializable{

    protected int chunkNo;
    protected Integer desiredReplicationDegree = -1;


    public Chunk(int chunkNo) {
        this.chunkNo = chunkNo;
    }

    public Chunk(int chunkNo,int desiredReplicationDegree){
        this.chunkNo = chunkNo;
        this.desiredReplicationDegree = desiredReplicationDegree;
    }

    public int getChunkNo(){
        return chunkNo;
    }

    public int getReplicationDegree() { return this.desiredReplicationDegree;}

    public void setReplicationDegree(int replicationDegree) { this.desiredReplicationDegree = replicationDegree;}




    public synchronized int getDesiredReplicationDegree() {
        return desiredReplicationDegree;
    }


    public synchronized void setDesiredReplicationDegree(int desiredReplicationDegree) {
        this.desiredReplicationDegree = desiredReplicationDegree;
    }


}
