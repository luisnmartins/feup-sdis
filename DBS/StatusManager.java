import ChunkInfo.ChunkInfo;

import java.util.Hashtable;

public class StatusManager {

    private static Hashtable<String,String> filesTables;
    private static Hashtable<String, ChunkInfo> chunkTable;

    StatusManager(){
        this.filesTables = new Hashtable<>();
        this.chunkTable = new Hashtable<>();
    }

    public  synchronized void addFile(String pathname,){

    }
    public synchronized void addChunk(){

    }

    public synchronized void updateChunk(){

    }

    public synchronized void deleteFile(){

    }


}
