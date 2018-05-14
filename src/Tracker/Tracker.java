package Tracker;

import Chunk.ChunkData;
import Messages.*;
import Peer.*;
import Workers.RestoreChecker;
import java.util.AbstractMap.SimpleEntry;
import Sockets.*;
import java.util.*;
import java.io.*;
import java.util.concurrent.*;

/**
 * Tracker
 */
public class Tracker {

    private static ConcurrentHashMap<String,PeerInfo> onlinePeers;
    private static ConcurrentHashMap<String,ArrayList<String>> availableFiles; 

    private static ScheduledThreadPoolExecutor exec;
    private static MessageInterpreter messageInterpreter;

    public Tracker() throws IOException{
        initiateSocketThreads();
    }

    public void initiateSocketThreads()throws IOException {

        this.exec = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(100000);
        
        messageInterpreter = new MessageInterpreter();
        Runnable interpreterThread = messageInterpreter;
        exec.execute(interpreterThread);

    }

    public static void main(String[] args) throws IOException {
        
        System.setProperty("java.net.preferIPv4Stack", "true");

        Tracker tracker = new Tracker();

        //Test
        String testMessage = "REGISTER 123-abc 123.1.2.3 1234 \r\n\r\n";
        SimpleEntry<Integer,byte[]> pair = new SimpleEntry<>(testMessage.getBytes().length,testMessage.getBytes());
        Tracker.getMessageInterpreter().putInQueue(pair);

    }

    public static ScheduledExecutorService getExec() {
        return exec;
    }

    public static MessageInterpreter getMessageInterpreter() {
        return messageInterpreter;
    }

    public static ConcurrentHashMap<String,PeerInfo> getOnlinePeers(){
        return Tracker.onlinePeers;
    }

    public static ConcurrentHashMap<String,ArrayList<String>> getAvailableFiles(){
        return Tracker.availableFiles;
    }
    
    public static void addOnlinePeer(String peerID, String address, String receiverPort){
        PeerInfo peerInfo = new PeerInfo(address, Integer.parseInt(receiverPort));
        System.out.println(peerID + " " + address + " " + receiverPort);
        Tracker.onlinePeers.put(peerID, peerInfo);
    }

    

}