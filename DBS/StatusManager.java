
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class StatusManager implements java.io.Serializable{

    private static final int DEFAULT_MAX_SIZE = 1000000000;
    private volatile ConcurrentHashMap<String,String> filesTables; //pathname fileId; files that the current peer sent to be backedUp
    private volatile ConcurrentHashMap<String,ChunkInfo> chunkTable;   //fileid.chunkno chunkinfo
    private volatile List<String> backedUpFiles;  //chunks stored by the current peer
    transient private volatile Set<String> chunksToRestore;
    private volatile int sizeUsed;
    private volatile int maxSizeUse;

    /**
     * Default construtor of the database
     */
    StatusManager(){
        this.filesTables = new ConcurrentHashMap<>();
        this.chunkTable = new ConcurrentHashMap<>();
        this.backedUpFiles = Collections.synchronizedList(new ArrayList<>());
        this.chunksToRestore = Collections.synchronizedSet(new HashSet<>());
        this.sizeUsed = 0;
        this.maxSizeUse = DEFAULT_MAX_SIZE;
        
    }

    /**
     * Adds a file(chunk) to the list of stored chunks and updates the occupied space by the peer 
     */
    public synchronized void addBackedUpFile(String fileIdKey) {
        backedUpFiles.add(fileIdKey);
        
        sizeUsed += chunkTable.get(fileIdKey).getSize();

    }
    /**
     * Deletes a file(chunk) to the list of stored chunks and updates the occupied space by the peer 
     */
    public synchronized void deleteBackedUpFile(String fileIdKey){
        sizeUsed -= chunkTable.get(fileIdKey).getSize();
        backedUpFiles.remove(fileIdKey);


    }


    /**
     * Decrements the size used by the system with the size of the chunkid given
     */
    public synchronized void decSizeUsed(String fileIdKey){
            sizeUsed -= chunkTable.get(fileIdKey).getSize();
    }


    /**
     * Checks if peer exceeded max capacity
     */
    public synchronized boolean isOutOfMemory(){
        return sizeUsed > maxSizeUse;
    }

    /**
     * Checks if peer is at max capacity
     */
    public synchronized boolean isMaxedOut(){
        return sizeUsed == maxSizeUse;
    }

    /**
     * Given a size of potential chunk to store, checks if it can be stored
     */
    public synchronized boolean canStore(int size){
        if((size + this.sizeUsed) > this.maxSizeUse){
            return false;
        }else return true;
    }

    /**
     * Checks if specified chunk is already stored
     */
    public synchronized String hasBackedUpChunk(String fileId, int chunkNo) {
        String file = fileId+"."+String.valueOf(chunkNo);

        if(backedUpFiles.contains(file)) {
            return file;
        }
        else return null;
    }

    /**
     * Adds a file to the table of backed up files
     */
    public synchronized void addFile(String pathname,String fileId){
        filesTables.put(pathname,fileId);

    }

    /**
     * Adds a chunk to the table of chunks in the system
     */
    public synchronized void addChunk(String fileIdKey,ChunkInfo chunkInfo){
        chunkTable.put(fileIdKey,chunkInfo);
    }

    /**
     * Updates by incrementing current replication degree of the chunk specified
     */
    public synchronized void updateChunk(String fileIdKey){
        ChunkInfo info = chunkTable.get(fileIdKey);
        info.addReplicationDegree();
    }

    /**
     * Updates by decrementing current replication degree of the chunk specified
     */
    public synchronized void updateChunkDec(String fileIdKey){
        ChunkInfo info = chunkTable.get(fileIdKey);
        info.decReplicationDegree();
    }

    /**
     * Stores to the chunk the peer that owns it
     */
    public synchronized void updateChunkInfoPeer(String fileIdKey, String peerID) {
        chunkTable.get(fileIdKey).addStorePeer(peerID);
    }

    /**
     * Removes from the chunk the peer that will stop owning it
     */
    public synchronized void updateChunkInfoPeerRemove(String fileIdKey, String peerId){
        chunkTable.get(fileIdKey).removeStorePeer(peerId);
    }

    /**
     * Sets the desired rep degree of the chunk
     */
    public synchronized void updateChunkRep(String fileIdKey,int rep){
        ChunkInfo info = chunkTable.get(fileIdKey);
        info.setDesiredReplicationDegree(rep);
    }

    /**
     * Sets the size the chunks occupies
     */
    public synchronized void updateChunkSize(String fileIdKey,int size){
        ChunkInfo info = chunkTable.get(fileIdKey);
        info.setSize(size);
    }

    /**
     * Checks if file specified has been backed up previously
     */
    public synchronized String isBackedUp(String pathname) {
        String fileId = filesTables.get(pathname);
        if(fileId == null)
            return null;

        return fileId;
    }


    /**
     * Deletes a file completely and all chunks associated (from tables)
     */
    public synchronized String deleteFile(String pathname){

        String fileId;
        if((fileId = isBackedUp(pathname)) == null)
            return null;
        fileId = new String(fileId);
        filesTables.remove(pathname);
        Set<String> keys = chunkTable.keySet();
        for(String key:keys){
            if(key.contains(fileId)){
                chunkTable.remove(key);
            }
        }
        return fileId;



    }

    /**
     * Checks if peer stored the chunk specified
     */
    public synchronized boolean storedChunk(String fileIdKey){
        if(backedUpFiles.indexOf(fileIdKey) != -1){
            return true;
        }else return false;
    }

    /**
     * Gets chunk number from specified chunk
     */
    public synchronized int getChunkNumber(String fileIdKey){
       return chunkTable.get(fileIdKey).getChunkNo();
    }

    /**
     * Checks if specified chunk is already stored in the table
     */
    public synchronized boolean chunkExists(String fileIdKey){
        if(chunkTable.get(fileIdKey) == null){
            return false;
        }else return true;


    }

    /**
     * Cleans all chunks from the table
     */
    public synchronized void removeChunks(String fileId){
        Set<String> set = chunkTable.keySet();
        for(String key: set){
            if(key.contains(fileId)){
                chunkTable.remove(key);
            }
        }
    }

    /**
     * Check if chunk achieved the desired replication degree
     */
    public synchronized boolean checkChunkStatus(String fileIdKey){

        if(chunkTable.get(fileIdKey).isDesired() == false) {
            return false;
        }
        else {
            return true;
        }

    }


    public synchronized ConcurrentHashMap<String, ChunkInfo> getChunkTable() {
        return chunkTable;
    }

    public synchronized ConcurrentHashMap<String, String> getFilesTables() {
        return filesTables;
    }

    public synchronized List<String> getBackedUpFiles() {
        return backedUpFiles;
    }

    public synchronized ChunkInfo getChunkInfo(String fileIdKey){
        return chunkTable.get(fileIdKey);
    }

    public synchronized void addChunkToRestore(String fileIdKey) {
        chunksToRestore.add(fileIdKey);
    }


    public synchronized boolean isChunkToRestore(String fileIdKey) {
        return chunksToRestore.contains(fileIdKey);

    }

    /**
     * Checks if it needs to restore specified chunk
     */
    public synchronized boolean chunkReallyToRestore(String fileIdKey) {
        if(isChunkToRestore(fileIdKey)) {
            chunksToRestore.remove(fileIdKey);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if there are files to restore
     */
    public synchronized boolean isChunkToRestoreEmpty(){
        return  chunksToRestore.isEmpty();
    }

    /**
     * @return the sizeUsed
     */
    public synchronized int getSizeUsed() {
        return sizeUsed;
    }

    /**
     * @return the maxSizeUse
     */
    public synchronized int getMaxSizeUse() {
        return maxSizeUse;
    }

    /**
     * @param maxSizeUse the maxSizeUse to set
     */
    public synchronized void setMaxSizeUse(int maxSizeUse) { this.maxSizeUse = maxSizeUse; }

    public synchronized boolean hasFileById(String fileId){
        return filesTables.contains(fileId);
    }


    /**
     * Write status variables to serializable
     * @param stream
     * @throws IOException
     */
    private void writeObject(java.io.ObjectOutputStream stream) throws IOException {

        stream.writeObject(filesTables);
        stream.writeObject(chunkTable);
        stream.writeObject(backedUpFiles);
        stream.writeInt(sizeUsed);
        stream.writeInt(maxSizeUse);
    }

    /**
     * Read status variables to serializable
     * @param stream
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {

        chunksToRestore = Collections.synchronizedSet(new HashSet<>());
        filesTables = (ConcurrentHashMap<String, String>) stream.readObject();
        chunkTable = (ConcurrentHashMap<String, ChunkInfo>)stream.readObject();
        backedUpFiles = (List<String>) stream.readObject();
        sizeUsed = stream.readInt();
        maxSizeUse = stream.readInt();

    }


}
