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
    private ArrayList<Boolean> chunksDownloaded; 


    public TorrentInfo(String trackerAddress, int trackerPort, long chunkLength, long fileLength, String filePath){

        this.trackerAddress = trackerAddress;
        this.trackerPort = trackerPort;
        this.chunkLength = chunkLength;
        this.fileLength = fileLength;
        this.filePath = filePath;

        int totalChunks = (int)Math.ceil(this.fileLength/this.chunkLength);
        this.chunksDownloaded = new ArrayList<>(totalChunks);   
        for(int i = 0; i < totalChunks; i++){
            this.chunksDownloaded.add(false);
        }

    }

    /**
     * @return the chunkLength
     */
    public long getChunkLength() {
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
    public ArrayList<Boolean> getChunksDownloaded() {
        return chunksDownloaded;
    }

    /**
     * @param chunksDownloaded the chunksDownloaded to set
     */
    public void setChunksDownloaded(ArrayList<Boolean> chunksDownloaded) {
        this.chunksDownloaded = chunksDownloaded;
    }

    /**
     * @return the fileLength
     */
    public long getFileLength() {
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
    public String getFilePath() {
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
    public String getTrackerAddress() {
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
    public int getTrackerPort() {
        return trackerPort;
    }

    /**
     * @param trackerPort the trackerPort to set
     */
    public void setTrackerPort(int trackerPort) {
        this.trackerPort = trackerPort;
    }
}