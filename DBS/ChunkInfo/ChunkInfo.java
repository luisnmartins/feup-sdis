package ChunkInfo;

public class ChunkInfo {

    private String fileID;
    private int currentReplicationDegree;
    private Integer desiredReplicationDegree = null;

    public ChunkInfo(String fileID, int rdd, int rda){
        this.fileID = fileID;
        this.currentReplicationDegree=rdd;
        this.desiredReplicationDegree = rda;

    }

    public ChunkInfo(String fileID){
        this.fileID = fileID;
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

    public String getFileID() {
        return fileID;
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
