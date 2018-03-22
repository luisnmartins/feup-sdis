import java.io.File;

public class Chunk extends File{

    protected String fileID;
    protected int chunkNo;
    protected int replicationDegree;

    public Chunk(String pathname){
        super(pathname);
    }

    public Chunk(File parent, String child){
        super(parent,child);
    }

    public Chunk(String parent, String child){
        super(parent,child);
    }

    String getFileID(){
        return fileID;
    }

    int getChunkNo(){
        return chunkNo;
    }

    int getReplicationDegree(){
        return this.replicationDegree;
    }

    void setFileID(String id){
        this.fileID = id;
    }

    void setChunkNo(int chunkNo){
        this.chunkNo = chunkNo;
    }

    void setReplicationDegree(int replicationDegree){
        this.replicationDegree = replicationDegree;
    }
}
