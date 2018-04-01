
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class StatusManager implements java.io.Serializable{

    private static final int DEFAULT_MAX_SIZE = 1000000000;
    private volatile ConcurrentHashMap<String,String> filesTables; //pathname fileId; files that the current peer sent to be backedUp
    private volatile ConcurrentHashMap<String,ChunkInfo> chunkTable;   //fileid.chunkno chunkinfo
    private volatile List<String> backedUpFiles;  //files stored by the current peer
    transient private volatile Set<Integer> chunksToRestore;
    private volatile int sizeUsed;
    private volatile int maxSizeUse;

    StatusManager(){
        this.filesTables = new ConcurrentHashMap<>();
        this.chunkTable = new ConcurrentHashMap<>();
        this.backedUpFiles = Collections.synchronizedList(new ArrayList<>());
        this.chunksToRestore = Collections.synchronizedSet(new HashSet<>());
        this.sizeUsed = 0;
        this.maxSizeUse = DEFAULT_MAX_SIZE;
        
    }

    public synchronized void addBackedUpFile(String fileIdKey) {
        backedUpFiles.add(fileIdKey);
        
        sizeUsed += chunkTable.get(fileIdKey).getSize();

    }

    public synchronized void deleteBackedUpFile(String fileIdKey){
        backedUpFiles.remove(fileIdKey);

        sizeUsed -= chunkTable.get(fileIdKey).getSize();
    }

    public synchronized boolean isOutOfMemory(){
        return sizeUsed > maxSizeUse;
    }

    public synchronized boolean isMaxedOut(){
        return sizeUsed == maxSizeUse;
    }

    public synchronized boolean canStore(int size){
        if((size + this.sizeUsed) > this.maxSizeUse){
            return false;
        }else return true;
    }

    public synchronized String hasBackedUpChunk(String fileId, int chunkNo) {
        String file = fileId+"."+String.valueOf(chunkNo);

        if(backedUpFiles.contains(file)) {
            return file;
        }
        else return null;
    }


    public synchronized void addFile(String pathname,String fileId){
        filesTables.put(pathname,fileId);

    }
    public synchronized void addChunk(String fileIdKey,ChunkInfo chunkInfo){
        chunkTable.put(fileIdKey,chunkInfo);
    }

    public synchronized void updateChunk(String fileIdKey){
        ChunkInfo info = chunkTable.get(fileIdKey);
        info.addReplicationDegree();
    }

    public synchronized void updateChunkDec(String fileIdKey){
        ChunkInfo info = chunkTable.get(fileIdKey);
        info.decReplicationDegree();
    }

    public synchronized void updateChunkInfoPeer(String fileIdKey, String peerID) {
        chunkTable.get(fileIdKey).addStorePeer(peerID);
    }

    public synchronized void updateChunkInfoPeerRemove(String fileIdKey, String peerId){
        chunkTable.get(fileIdKey).removeStorePeer(peerId);
    }

    public synchronized void updateChunkRep(String fileIdKey,int rep){
        ChunkInfo info = chunkTable.get(fileIdKey);
        info.setDesiredReplicationDegree(rep);
    }

    public synchronized void updateChunkSize(String fileIdKey,int size){
        ChunkInfo info = chunkTable.get(fileIdKey);
        info.setSize(size);
    }

    public synchronized String isBackedUp(String pathname) {
        String fileId = filesTables.get(pathname);
        if(fileId == null)
            return null;

        return fileId;
    }


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

    public synchronized boolean storedChunk(String fileIdKey){
        if(backedUpFiles.indexOf(fileIdKey) != -1){
            return true;
        }else return false;
    }

    public synchronized int getChunkNumber(String fileIdKey){
       return chunkTable.get(fileIdKey).getChunkNo();
    }

    public synchronized boolean chunkExists(String fileIdKey){
        if(chunkTable.get(fileIdKey) == null){
            return false;
        }else return true;


    }

    public synchronized void removeChunks(String fileId){
        Set<String> set = chunkTable.keySet();
        for(String key: set){
            if(key.contains(fileId)){
                chunkTable.remove(key);
            }
        }
    }

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

    public synchronized void addChunkToRestore(Integer chunkNo) {
        chunksToRestore.add(chunkNo);
    }


    public synchronized boolean isChunkToRestore(Integer chunkNo) {
        return chunksToRestore.contains(chunkNo);

    }

    public synchronized boolean chunkReallyToRestore(Integer chunkNo) {
        if(isChunkToRestore(chunkNo)) {
            chunksToRestore.remove(chunkNo);
            return true;
        } else {
            return false;
        }
    }

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
