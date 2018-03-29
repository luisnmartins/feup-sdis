
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

public class StatusManager {

    private static Hashtable<String,String> filesTables; //pathname fileId; files that the current peer sent to be backuped
    private static Hashtable<String,ChunkInfo> chunkTable;   //fileid.chunkno chunkinfo
    private static List<String> backupedUpFiles;  //files stored by the current peer
    private static List<ChunkData> chunksToRestore;

    StatusManager(){
        this.filesTables = new Hashtable<>();
        this.chunkTable = new Hashtable<>();
        this.backupedUpFiles = new ArrayList<>();
    }

    public synchronized void addBackupedUpFile(String fileId, int chunkNo) {
        String file = fileId+"."+String.valueOf(chunkNo);
        backupedUpFiles.add(file);
        System.out.println("BACKEDUP: " +backupedUpFiles);
    }

    public synchronized String hasBackupUp(String fileId, int chunkNo) {
        String file = fileId+"."+String.valueOf(chunkNo);
        if(backupedUpFiles.contains(file))
            return file;
        else return null;
    }


    public  synchronized void addFile(String pathname,String fileId){
        filesTables.put(pathname,fileId);

    }
    public synchronized void addChunk(String fileIdKey,ChunkInfo chunkInfo){
        chunkTable.put(fileIdKey,chunkInfo);
    }

    public synchronized void updateChunk(String fileIdKey){
        ChunkInfo info = chunkTable.get(fileIdKey);
        info.addReplicationDegree();
    }

    public synchronized void updateChunkRep(String fileIdKey,int rep){
        ChunkInfo info = chunkTable.get(fileIdKey);
        info.setDesiredReplicationDegree(rep);
    }


    public synchronized String deleteFile(String pathname){

        String fileId = filesTables.get(pathname);
        if(fileId == null)
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
        if(backupedUpFiles.indexOf(fileIdKey) != -1){
            return true;
        }else return false;
    }

    public synchronized int getChunkNumber(String fileIdKey){
       return chunkTable.get(fileIdKey).getChunkNo();
    }

    public synchronized boolean checkHasAllChunks(){
        boolean checkFlag = false;
        for(ChunkData chunk : chunksToRestore){
            if(chunk.getData() == null  || chunk.getData().length < 64000 ){
                checkFlag = true;
            }
        }

        if(checkFlag == true){
            int i = 0;
            for(ChunkData chunkData : chunksToRestore){

            }
        }else return false;
       return false;
    }


    public synchronized boolean chunkExists(String fileIdKey){
        if(chunkTable.get(fileIdKey) == null){
            return false;
        }else return true;


    }

    public synchronized boolean checkChunkStatus(String fileIdKey){
        return chunkTable.get(fileIdKey).isDesired();
    }

    public  Hashtable<String, ChunkInfo> getChunkTable() {
        return chunkTable;
    }

    public  Hashtable<String, String> getFilesTables() {
        return filesTables;
    }

    public  List<String> getBackupedUpFiles() {
        return backupedUpFiles;
    }

    public synchronized ChunkInfo getChunkInfo(String fileIdKey){
        return chunkTable.get(fileIdKey);
    }
}
