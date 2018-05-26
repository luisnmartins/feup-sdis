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
    private static Storage storage;

    private static ReceiverSocket controlReceiver;

    private static int serId;


    public Peer() {
    }

    public Peer(String trackerIP ,int port) throws IOException {
        peerID = UUID.randomUUID().toString();
        Peer.trackerIP = trackerIP;
        trackerPort = port;

        exec = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(100000);

    
        //Serializable
        LogsManager logsManager = new LogsManager();
        storage = logsManager.LoadData();


        if(!setKeyPair())
            return;

        controlReceiver = new ReceiverSocket(0);
        controlReceiver.connect(peerID);

        //TEST
        this.sendRegister();

        Runnable onlineMessagesThread = new OnlineMessagesThread();
        Peer.getExec().scheduleAtFixedRate(onlineMessagesThread, 30, 60, TimeUnit.SECONDS);

    }

    public class OnlineMessagesThread implements Runnable {
        public OnlineMessagesThread() {}
        @Override
        public void run() {
            Message message = new OnlineMessage(peerID, true);
            try {
                Peer.sendMessageToTracker(message);                
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
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

    public static void setPeerID(String peerID) {
        Peer.peerID = peerID;
    }

    public static Storage getStorage() {
        return storage;
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

    public static int getSerId() {
        return serId;
    }

    public static void setSerId(int serId) {
        Peer.serId = serId;
    }

    public static void main(String[] args) throws IOException {
        System.setProperty("java.net.preferIPv4Stack", "true");

        Peer peer;
        if (args.length == 4) {
            peer = new Peer(args[0],Integer.parseInt(args[1]));

            serId = Integer.parseInt(args[2]);

            if(args[3].equals("download")){
                peer.download("path", "path");
            }else{
                peer.seed("/home/julieta/Github/feup-sdis/src/bridge.jpeg", "/home/julieta/Github/feup-sdis/src");
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
        int port = controlReceiver.getServerSocket().getLocalPort();
        SenderSocket channelStarter = new SenderSocket(trackerPort, trackerIP);
        channelStarter.connect(peerID, "tracker",null);
        Message message = new RegisterMessage(this.peerID, address, port, key);
        channelStarter.getHandler().sendMessage(message);
    
    }

    public void download(String torrentPath, String filePath) throws IOException{
        
        Message message = new GetFileMessage(peerID, "abc");
        sendMessageToTracker(message);
        
    }

    public void seed(String filePath, String torrentPath) throws IOException{
        FileManager manager = new FileManager(filePath);
        InetAddress address = InetAddress.getByName(this.trackerIP);
        SimpleEntry<String,TorrentInfo> torrentInfo = manager.createDownloadFile(260096, this.trackerPort, address.getHostAddress(),torrentPath);
        storage.getFilesSeeded().put(torrentInfo.getKey(), torrentInfo.getValue());
        Message message = new HasFileMessage(peerID, torrentInfo.getKey());
        sendMessageToTracker(message);
    }

    public static void sendMessageToTracker(Message message) throws UnknownHostException {
        SenderSocket channelStarter = new SenderSocket(trackerPort, trackerIP);
        channelStarter.connect(peerID, "tracker",null);
        channelStarter.getHandler().sendMessage(message);
    }
 


}