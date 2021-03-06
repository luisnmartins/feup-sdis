package Chunk;

import Chunk.Chunk;

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

    /**
     * Check whether or not a chunk is desired to be saved
     * @return true if the chunk is desired to be saved and false otherwise
     */
    public synchronized boolean isDesired(){
        if(desiredReplicationDegree.equals(-1))
            return true;
        if(currentReplicationDegree.compareTo(desiredReplicationDegree) >= 0) {
            return false;
        }
        else return true;
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
     * Check if a chunk can be deleted keeping the desired replication degree
     * @return true if the chunk can be deleted and false otherwise
     */
    public synchronized boolean canBeDeleted(){
        if(desiredReplicationDegree.equals(-1))
            return false;
        if(currentReplicationDegree.compareTo(desiredReplicationDegree) > 0) {
            return true;
        }
        else return false;
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
