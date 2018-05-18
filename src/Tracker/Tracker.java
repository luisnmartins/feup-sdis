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

    private static ConcurrentHashMap<String,PeerInfo> onlinePeers = new ConcurrentHashMap<>();;
    private static ConcurrentHashMap<String,ArrayList<String>> availableFiles = new ConcurrentHashMap<>();; 

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



        //--TESTS REGISTER--//

        //Test 1
        RegisterMessage registerToSend1 = new RegisterMessage("abc123", "444.55.66", 7777);
        byte[] register1 = registerToSend1.getFullMessage();
        SimpleEntry<Integer,byte[]> registerPair1 = new SimpleEntry<>(register1.length,register1);
        Tracker.getMessageInterpreter().putInQueue(registerPair1);

        //Test 2
        RegisterMessage registerToSend2 = new RegisterMessage("abc123", "444.55.66", 8888);
        byte[] register2 = registerToSend2.getFullMessage();
        SimpleEntry<Integer,byte[]> registerPair2 = new SimpleEntry<>(register2.length,register2);
        Tracker.getMessageInterpreter().putInQueue(registerPair2);

        //Test 3
        RegisterMessage registerToSend3 = new RegisterMessage("abc123", "444.55.67", 8888);
        byte[] register3 = registerToSend3.getFullMessage();
        SimpleEntry<Integer,byte[]> registerPair3 = new SimpleEntry<>(register3.length,register3);
        Tracker.getMessageInterpreter().putInQueue(registerPair3);

        //--TEST HASFILES--//

        //Test 1
        HasFileMessage hasfileToSend1 = new HasFileMessage("abc123", "file123", true);
        byte[] hasfile1 = hasfileToSend1.getFullMessage();
        SimpleEntry<Integer,byte[]> hasfilePair1 = new SimpleEntry<>(hasfile1.length,hasfile1);
        Tracker.getMessageInterpreter().putInQueue(hasfilePair1);



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


    
    public static void addOnlinePeer(String peerId, String address, int port, String senderIp){

        PeerInfo peerInfo = new PeerInfo(address, port);

        if(!address.equals(senderIp)){
            System.out.println("TRACKER ERROR - That ip address is not yours.");
            return;            
        }
        
        if(onlinePeers.get(peerId)==null){
            Tracker.onlinePeers.put(peerId, peerInfo); 
            System.out.println("TRACKER - Peer added to the system.");
        }
        else{
            PeerInfo oldInfo = onlinePeers.get(peerId);

            if(oldInfo.getAddress().equals(peerInfo.getAddress())){
                if(oldInfo.getPort() != peerInfo.getPort()){
                    oldInfo.setPort(port);
                    System.out.println("TRACKER - Peer updated with new port.");
                }
            }
            else{
                System.out.println("TRACKER ERROR - The id and ip address do not match.");
            }
        }       
        
    }

    public static void addAvailableFile(String senderId, String fileId, String senderIp){

        if(!senderIp.equals(onlinePeers.get(senderId).getAddress())){
            System.out.println("TRACKER ERROR - Your ip doesn't match with your id.");
            return;            
        }

        if(availableFiles.get(fileId) == null){
            ArrayList<String> peersIds = new ArrayList(); 
            peersIds.add(senderId);
            availableFiles.put(fileId, peersIds);

            System.out.println("TRACKER - File added.");
        }
        else{
            if(!availableFiles.get(fileId).contains(senderId)){
                availableFiles.get(fileId).add(senderId);
                System.out.println("TRACKER - You were added to the peers with this file.");
            }
            
        }

    }

    

    

}