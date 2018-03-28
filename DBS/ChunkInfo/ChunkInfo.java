package ChunkInfo;

public class ChunkInfo {

    private String fileID;
    private int currentReplicationDegree;
    private final int desiredReplicationDegree;

    public ChunkInfo(String fileID, int rdd, int rda){
        this.fileID = fileID;
        this.currentReplicationDegree=rdd;
        this.desiredReplicationDegree = rda;

    }


    public boolean isDesired(){
        if(currentReplicationDegree == desiredReplicationDegree)
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
}
