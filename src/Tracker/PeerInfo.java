
package Tracker;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PeerInfo{
    
    private String address;
    private int port;

    public PeerInfo(String address, int port){
        this.address=address;
        this.port = port;
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

}