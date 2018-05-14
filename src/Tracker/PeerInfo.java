
package Tracker;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PeerInfo{
    
    private String address;
    private int receiverPort;

    public PeerInfo(String address, int receiverPort){
        this.address=address;
        this.receiverPort = receiverPort;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address=address;
    }

    public int getReceiverPort() {
        return this.receiverPort;
    }

    public void setRceiverPort(int receiverPort) {
        this.receiverPort=receiverPort;
    }

}