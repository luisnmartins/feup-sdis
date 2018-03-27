package ChunkInfo;

public class ChunkInfo {

    private String fileID;
    private int currentReplicationDegree;
    private int desiredReplicationDegree;

    ChunkInfo(String fileID,int rdd, int rda){
        this.fileID = fileID;
        this.currentReplicationDegree=rdd;
        this.desiredReplicationDegree = rda;

    }
}
