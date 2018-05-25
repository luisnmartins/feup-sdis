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

    private static volatile ConcurrentHashMap<String,PeerInfo> onlinePeers = new ConcurrentHashMap<>();
    private static volatile ConcurrentHashMap<String,ArrayList<String>> availableFiles = new ConcurrentHashMap<>();; 

    private static ScheduledThreadPoolExecutor exec;
    private static MessageInterpreter messageInterpreter;

    private static ReceiverSocket sslServerSocket;

    public Tracker() throws IOException{
   
        initiateSocketThreads();
    }

    public void initiateSocketThreads()throws IOException {

        this.exec = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(100000);


        this.sslServerSocket = new ReceiverSocket(5555);
        this.sslServerSocket.connect("tracker");
        
        messageInterpreter = new MessageInterpreter();
        Runnable interpreterThread = messageInterpreter;
        exec.execute(interpreterThread);

    }

    public static void main(String[] args) throws IOException {
        
        System.setProperty("java.net.preferIPv4Stack", "true");

        Tracker tracker = new Tracker();

        //tests(tracker);

    }

    public static void tests(Tracker tracker){
        //--TESTS REGISTER--//

        //Test 1
        RegisterMessage registerToSend1 = new RegisterMessage("abc123", "444.55.66", 7777,null);
        byte[] register1 = registerToSend1.getFullMessage();
        SimpleEntry<Integer,byte[]> registerPair1 = new SimpleEntry<>(register1.length,register1);
        Tracker.getMessageInterpreter().putInQueue(registerPair1);

        //Test 2
        RegisterMessage registerToSend2 = new RegisterMessage("abc123", "444.55.66", 8888,null);
        byte[] register2 = registerToSend2.getFullMessage();
        SimpleEntry<Integer,byte[]> registerPair2 = new SimpleEntry<>(register2.length,register2);
        Tracker.getMessageInterpreter().putInQueue(registerPair2);

        //Test 3
        RegisterMessage registerToSend3 = new RegisterMessage("abc123", "444.55.67", 8888,null);
        byte[] register3 = registerToSend3.getFullMessage();
        SimpleEntry<Integer,byte[]> registerPair3 = new SimpleEntry<>(register3.length,register3);
        Tracker.getMessageInterpreter().putInQueue(registerPair3);

        //--TEST HASFILES--//

        //Test 1
        HasFileMessage hasfileToSend1 = new HasFileMessage("abc123", "file123");
        byte[] hasfile1 = hasfileToSend1.getFullMessage();
        SimpleEntry<Integer,byte[]> hasfilePair1 = new SimpleEntry<>(hasfile1.length,hasfile1);
        Tracker.getMessageInterpreter().putInQueue(hasfilePair1);

        //Test 2
        HasFileMessage hasfileToSend2 = new HasFileMessage("abc456", "file123");
        byte[] hasfile2 = hasfileToSend2.getFullMessage();
        SimpleEntry<Integer,byte[]> hasfilePair2 = new SimpleEntry<>(hasfile2.length,hasfile2);
        Tracker.getMessageInterpreter().putInQueue(hasfilePair2);


        //--TEST GETFILES--//
        //Test 1
        RegisterMessage registerToSend4 = new RegisterMessage("abc456", "777.88.99", 8888,null);
        byte[] register4 = registerToSend4.getFullMessage();
        SimpleEntry<Integer,byte[]> registerPair4 = new SimpleEntry<>(register4.length,register4);
        Tracker.getMessageInterpreter().putInQueue(registerPair4);

        //Test 2
        GetFileMessage getfileToSend1 = new GetFileMessage("abc456", "file123");
        byte[] getfile1 = getfileToSend1.getFullMessage();
        SimpleEntry<Integer,byte[]> getfilePair1 = new SimpleEntry<>(getfile1.length,getfile1);
        Tracker.getMessageInterpreter().putInQueue(getfilePair1);

        //--TEST NOFILES--//
        //Test 1
        NoFileMessage nofileToSend1 = new NoFileMessage("abc123", "file123");
        byte[] nofile1 = nofileToSend1.getFullMessage();
        SimpleEntry<Integer,byte[]> nofilePair1 = new SimpleEntry<>(nofile1.length,nofile1);
        Tracker.getMessageInterpreter().putInQueue(nofilePair1);

        Tracker.getMessageInterpreter().putInQueue(getfilePair1);
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


    
    public static int addOnlinePeer(String peerId, String address, int port,byte[] key){

        long time = System.currentTimeMillis();
        System.out.println(time);
        PeerInfo peerInfo = new PeerInfo(address, port, time,key);
        
        if(onlinePeers.get(peerId)==null){
            Tracker.onlinePeers.put(peerId, peerInfo); 
            System.out.println("TRACKER - Peer added to the system.");
            return 0;
        }
        else{
            PeerInfo oldInfo = onlinePeers.get(peerId);

            if(oldInfo.getAddress().equals(peerInfo.getAddress())){
                if(oldInfo.getPort() != peerInfo.getPort()){
                    oldInfo.setPort(port);
                    System.out.println("TRACKER - Peer updated with new port.");
                    return 0;
                }
            }
            else{
                System.out.println("TRACKER ERROR - The id and ip address do not match.");
                return -1;
            }
        }
        
        return -1;
        
    }

    public static int refreshOnlinePeer(String senderId){
        if(onlinePeers.get(senderId)==null){
            System.out.println("TRACKER ERROR - You are not registered in the system.");
            return - 1;  
        }

        PeerInfo peerInfo = onlinePeers.get(senderId);
        long time = System.currentTimeMillis();
        peerInfo.setLastTimeOnline(time);

        return 0;
    }

    public static int addPeerToFile(String senderId, String fileId){

        if(onlinePeers.get(senderId)==null){
            System.out.println("TRACKER ERROR - You are not registered in the system.");
            return - 1;  
        }

        if(availableFiles.get(fileId) == null){
            ArrayList<String> peersIds = new ArrayList(); 
            peersIds.add(senderId);
            availableFiles.put(fileId, peersIds);

            System.out.println("TRACKER - File added.");
            return 0;
        }
        else{
            if(!availableFiles.get(fileId).contains(senderId)){
                availableFiles.get(fileId).add(senderId);
                System.out.println("TRACKER - You were added to the peers with this file.");
                return 0;
            }
        }

        return -1;

    }

    public static int removePeerOfFile(String senderId, String fileId){

        if(onlinePeers.get(senderId)==null){
            System.out.println("TRACKER ERROR - You are not registered in the system.");
            return - 1;  
        }

        if(availableFiles.get(fileId) != null){

            availableFiles.get(fileId).remove(senderId);

            if(availableFiles.get(fileId).isEmpty()){
                availableFiles.remove(fileId);
            }

            System.out.println("TRACKER - File removed.");
            return 0;
        }

        return -1;

    }

    public static ArrayList<PeerInfo> getAvailableFile(String senderId, String fileId){


        if(onlinePeers.get(senderId)==null){
            System.out.println("TRACKER ERROR - You are not registered in the system.");
            return null;  
        }

        ArrayList<PeerInfo> filePeers = new ArrayList<>();

        ArrayList<String> peersIds = availableFiles.get(fileId);
        if(peersIds!=null){
            for(int i = 0; i < peersIds.size(); i++){
                filePeers.add(onlinePeers.get(peersIds.get(i)));
            }
        }
        else{
            System.out.println("TRACKER ERROR - That file is not available");
            return null;
        }
        

        return filePeers;
    }
    

    

    

}