
import java.util.HashSet;
import java.util.Set;

public class ChunkInfo extends Chunk implements java.io.Serializable {

    private Integer currentReplicationDegree;
    private int size;
    private Set<String> peers;

    public ChunkInfo(int chunkNo, int rdd, int rda,int size){
        super(chunkNo, rda);
        this.currentReplicationDegree=rdd;
        this.size = size;
        peers = new HashSet<>();

    }

    public ChunkInfo(int chunkNo){
        super(chunkNo);
        this.currentReplicationDegree = 1;
        this.size = -1;
        peers = new HashSet<>();

    }
    public synchronized void addStorePeer(String peerID){

        this.peers.add(peerID);

    }

    public synchronized boolean isDesired(){
        if(desiredReplicationDegree.equals(-1))
            return false;
        if(currentReplicationDegree.compareTo(desiredReplicationDegree) >= 0 && desiredReplicationDegree.equals(-1) == false) {
            return true;
        }
        else return false;
    }

    public synchronized void removeStorePeer(String peerId){
        this.peers.remove(peerId);
    }

    public synchronized boolean isStored(String peerID){
        return peers.contains(peerID);
    }

    public synchronized int getCurrentReplicationDegree() {
        return currentReplicationDegree;
    }


    public synchronized void addReplicationDegree(){
        this.currentReplicationDegree++;
    }

    public synchronized void decReplicationDegree(){
        this.currentReplicationDegree--;
    }

    public synchronized void setCurrentReplicationDegree(int currentReplicationDegree) {
        this.currentReplicationDegree = currentReplicationDegree;
    }

    /**
     * @param size the size to set
     */
    public synchronized void setSize(int size) {
        this.size = size;
    }

    public synchronized Set<String> getPeers() {
        return peers;
    }

    public synchronized int getSize() {
        return size;
    }
}
