package Peer;

import Chunk.ChunkData;
import Messages.*;
import java.util.AbstractMap.SimpleEntry;
import Sockets.*;
import java.util.*;
import java.net.UnknownHostException;
import java.io.*;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.concurrent.*;



/**
 * peer
 */
public class Peer{

    private static String peerID;
    private static String trackerIP;
    private static int trackerPort;

    private static ScheduledThreadPoolExecutor exec;
    private static StatusManager stateManager;

    private static ReceiverSocket controlReceiver;


    public Peer() {
    }

    public Peer(String trackerIP ,int port) throws IOException {
        this.peerID = UUID.randomUUID().toString();
        this.trackerIP = trackerIP;
        this.trackerPort = port;

        this.exec = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(100000);

        if(!setKeyPair())
            return;

        this.controlReceiver = new ReceiverSocket(0);
        this.controlReceiver.connect(this.peerID);

        //Serializable
        /*LogsManager statusData = new LogsManager();
        this.stateManager = statusData.LoadData();
        stateManager.updateData();*/

        //TEST
        this.sendRegister();

    }

    public boolean setKeyPair(){
        File priFile = new File("./Peer/" + peerID + ".private");
        File pubFile = new File("./Peer/" + peerID + ".public");
        if((!priFile.exists() || priFile.isDirectory()) || (!pubFile.exists() || pubFile.isDirectory())){
            if(!this.generateKeyPair()){
                System.out.println("There was a problem generating the keys");
                return false;
            }
        }
        return true;
    }


    public static ScheduledExecutorService getExec() {
        return exec;
    }

    public static String getPeerID() {
        return peerID;
    }

    public static StatusManager getStateManager() {
        return stateManager;
    }

    public static ReceiverSocket getControlReceiver() {
        return controlReceiver;
    }

    public static String getTrackerIP() {
        return trackerIP;
    }
   
    public static int getTrackerPort() {
        return trackerPort;
    }

    public static void main(String[] args) throws IOException {
        System.setProperty("java.net.preferIPv4Stack", "true");

        Peer peer;
        if (args.length == 3) {
            peer = new Peer(args[0],Integer.parseInt(args[1]));

            if(args[2].equals("download")){
                peer.download("path", "path");
            }else{
                peer.seed("path", "path");
            }

        } else {
            System.err.println("Error retrieving function arguments");
            return;
        }

    }

    public boolean generateKeyPair(){
        
        System.out.println("Generating Peer keys at " + System.getProperty("user.dir"));
        
        String peerName = this.peerID;
        String commandtoCreate = "keytool -genkey -alias " + peerName + "private -keystore " + peerName + ".private -storetype JKS -keyalg rsa -dname 'CN=Your Name, OU=Your Organizational Unit, O=Your Organization, L=Your City, S=Your State, C=Your Country' -storepass " + peerName + "pw -keypass "+ peerName + "pw";
        String commandtoExportPublic = "keytool -export -alias " + peerName + "private -keystore " + peerName + ".private -file temp.key -storepass " + peerName + "pw";
        String commandtoImportPublic = "keytool -import -noprompt -alias " +peerName + "public -keystore " + peerName + ".public -file temp.key -storepass public";

        try{
            String[] args = {"/bin/bash","-c","cd Peer;" + commandtoCreate + ";" + commandtoExportPublic + ";" + commandtoImportPublic + ";rm -f temp.key"};
            Process proc = new ProcessBuilder(args).start();
            proc.waitFor();
            System.out.println("Keys generated succesfully");
            return true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
      
    }

    public byte[] readPublicKey() throws IOException{
        String pathname = "./Peer/"  + peerID + ".public";
        FileManager manager = new FileManager(pathname);
        return manager.readEntireFileData();
    }

    public void sendRegister() throws IOException{

        byte[] key = readPublicKey();
 
        String address = InetAddress.getLocalHost().getHostAddress();
        int port = this.controlReceiver.getServerSocket().getLocalPort();
        SenderSocket channelStarter = new SenderSocket(this.trackerPort, this.trackerIP);
        channelStarter.connect(peerID, "tracker",null);
        Message message = new RegisterMessage(this.peerID, address, port, key);
        channelStarter.getHandler().sendMessage(message);
    
    }

    public void download(String torrentPath, String filePath) throws IOException{
        
        Message message = new GetFileMessage(peerID, "abc");
        sendMessageToTracker(message);
        
    }

    public void seed(String filePath, String torrentPath) throws IOException{
    
        Message message = new HasFileMessage(peerID, "abc");
        sendMessageToTracker(message);
    }

    public void sendMessageToTracker(Message message) throws UnknownHostException {
        SenderSocket channelStarter = new SenderSocket(this.trackerPort, this.trackerIP);
        channelStarter.connect(peerID, "tracker",null);
        channelStarter.getHandler().sendMessage(message);
    }

   


}