import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChunkInfo {

    private int chunkNo;
    private int currentReplicationDegree;
    private Integer desiredReplicationDegree = null;
    private int size;
    private Set<String> peers;

    public ChunkInfo(int chunkNo, int rdd, int rda,int size){
        this.chunkNo = chunkNo;
        this.currentReplicationDegree=rdd;
        this.desiredReplicationDegree = rda;
        this.size = size;
        peers = new HashSet<>();

    }

    public ChunkInfo(int chunkNo){
        this.chunkNo = chunkNo;
        this.currentReplicationDegree = 1;
        this.size = -1;
        peers = new HashSet<>();

    }
    public synchronized void addStorePeer(String peerID){

        this.peers.add(peerID);

    }

    public synchronized boolean isStored(String peerID){
        return peers.contains(peerID);
    }




    public boolean isDesired(){
        if(desiredReplicationDegree == null)
            return false;
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

    public int getSize() {
        return size;
    }
}
