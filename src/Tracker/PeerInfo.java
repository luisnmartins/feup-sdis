
package Tracker;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PeerInfo{
    
    private String address;
    private int port;
    private long lastTimeOnline; //in milliseconds
    private byte[] publicKey;

    public PeerInfo(String address, int port, long lastTimeOnline,byte[] key){
        this.address=address;
        this.port = port;
        this.lastTimeOnline = lastTimeOnline;
        this.publicKey = key;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address=address;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port=port;
    }

    public long getLastTimeOnline() {
        return this.lastTimeOnline;
    }

    public void setLastTimeOnline(long lastTimeOnline) {
        this.lastTimeOnline=lastTimeOnline;
    }


}