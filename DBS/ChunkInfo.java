
public class ChunkInfo {

    private int chunkNo;
    private int currentReplicationDegree;
    private Integer desiredReplicationDegree = null;

    public ChunkInfo(int chunkNo, int rdd, int rda){
        this.chunkNo = chunkNo;
        this.currentReplicationDegree=rdd;
        this.desiredReplicationDegree = rda;

    }

    public ChunkInfo(int chunkNo){
        this.chunkNo = chunkNo;
        this.currentReplicationDegree = 1;

    }


    public boolean isDesired(){
        if(currentReplicationDegree >= desiredReplicationDegree)
            return true;
        else return false;
    }

    public int getCurrentReplicationDegree() {
        return currentReplicationDegree;
    }

    public int getDesiredReplicationDegree() {
        return desiredReplicationDegree;
    }

    public int getChunkNo() {
        return chunkNo;
    }

    public void addReplicationDegree(){
        this.currentReplicationDegree++;
    }

    public void decReplicationDegree(){
        this.currentReplicationDegree--;
    }

    public void setCurrentReplicationDegree(int currentReplicationDegree) {
        this.currentReplicationDegree = currentReplicationDegree;
    }

    public void setDesiredReplicationDegree(int desiredReplicationDegree) {
        this.desiredReplicationDegree = desiredReplicationDegree;
    }
}
