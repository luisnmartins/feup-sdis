package Peer;

import java.util.*;
import java.io.*;
import java.util.concurrent.*;

public class TorrentInfo implements java.io.Serializable{
    
    private String trackerAddress;
    private int trackerPort;
    private long chunkLength;
    private long fileLength;
    private String filePath;
    private volatile List<Boolean> chunksDownloaded; 


    public TorrentInfo(String trackerAddress, int trackerPort, long chunkLength, long fileLength, String filePath){

        this.trackerAddress = trackerAddress;
        this.trackerPort = trackerPort;
        this.chunkLength = chunkLength;
        this.fileLength = fileLength;
        this.filePath = filePath;

        long totalChunks = (fileLength + chunkLength - 1)/chunkLength;
        
        this.chunksDownloaded = Collections.synchronizedList(new ArrayList<>((int)totalChunks)) ;   
        for(int i = 0; i < totalChunks; i++){
            this.chunksDownloaded.add(false);
        }

    }

    /**
     * @return the chunkLength
     */
    public synchronized long getChunkLength() {
        return chunkLength;
    }

    /**
     * @param chunkLength the chunkLength to set
     */
    public void setChunkLength(long chunkLength) {
        this.chunkLength = chunkLength;
    }

    /**
     * @return the chunksDownloaded
     */
    public synchronized List<Boolean> getChunksDownloaded() {
        return chunksDownloaded;
    }

    /**
     * @param chunksDownloaded the chunksDownloaded to set
     */
    public synchronized void setChunksDownloaded(List<Boolean>chunksDownloaded) {
        this.chunksDownloaded = chunksDownloaded;
    }

    public synchronized void updateChunkDownloaded(int index, boolean bool){
        chunksDownloaded.remove(index);                        
        chunksDownloaded.add(index, bool);

        System.out.println("INDEX: " + index + " " + bool);
    }

    public synchronized int getNextFalse(){
        return chunksDownloaded.indexOf(false);
    }



    /**
     * @return the fileLength
     */
    public synchronized long getFileLength() {
        return fileLength;
    }

    /**
     * @param fileLength the fileLength to set
     */
    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }

    /**
     * @return the path
     */
    public synchronized String getFilePath() {
        return filePath;
    }

    /**
     * @param path the path to set
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * @return the trackerAddress
     */
    public synchronized String getTrackerAddress() {
        return trackerAddress;
    }
    /**
     * @param trackerAddress the trackerAddress to set
     */
    public void setTrackerAddress(String trackerAddress) {
        this.trackerAddress = trackerAddress;
    }

    /**
     * @return the trackerPort
     */
    public synchronized int getTrackerPort() {
        return trackerPort;
    }

    /**
     * @param trackerPort the trackerPort to set
     */
    public void setTrackerPort(int trackerPort) {
        this.trackerPort = trackerPort;
    }
}