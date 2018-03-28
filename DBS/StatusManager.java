import ChunkInfo.ChunkInfo;
import javafx.util.Pair;

import java.util.Hashtable;
import java.util.Set;

public class StatusManager {

    private static Hashtable<String,String> filesTables;
    private static Hashtable<Integer, ChunkInfo> chunkTable;
    private static Hashtable<String,Pair<Integer,Integer>> chunkStorages;

    StatusManager(){
        this.filesTables = new Hashtable<>();
        this.chunkTable = new Hashtable<>();
    }

    public  synchronized void addFile(String pathname,String fileID){
        filesTables.put(pathname,fileID);

    }
    public synchronized void addChunk(int chunkNo,ChunkInfo chunkInfo){
        chunkTable.put(chunkNo,chunkInfo);
    }

    public synchronized void updateChunk(int chunkId){
        ChunkInfo info = chunkTable.get(chunkId);
        info.addReplicationDegree();
    }

    public synchronized void updateChunkRep(int chunkId,int rep){
        ChunkInfo info = chunkTable.get(chunkId);
        info.setDesiredReplicationDegree(rep);
    }

    public synchronized void deleteFile(String pathname){
        String fileId = new String(filesTables.get(pathname));
        filesTables.remove(pathname);
        Set<Integer> keys = chunkTable.keySet();
        for(Integer key:keys){
            if(chunkTable.get(key).getFileID().equals(fileId)){
                chunkTable.remove(key);
            }
        }

    }

    public synchronized boolean chunkExists(int chunkId){
        if(chunkTable.get(chunkId) == null){
            return false;
        }else return true;


    }

    public synchronized boolean fileExists(String pathname){
        if(filesTables.get(pathname) == null)
            return false;
        else return true;
    }

    public synchronized boolean checkChunkStatus(int chunkId){
        return chunkTable.get(chunkId).isDesired();
    }

    public static Hashtable<Integer, ChunkInfo> getChunkTable() {
        return chunkTable;
    }

    public static Hashtable<String, String> getFilesTables() {
        return filesTables;
    }
}
