package Peer;

import Chunk.ChunkData;
import Messages.*;
import RMI.RMIHandler;
import RMI.remoteInterface;
import Workers.RestoreChecker;
import java.util.AbstractMap.SimpleEntry;
import Sockets.*;
import java.util.*;
import java.io.*;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.concurrent.*;



/**
 * peer
 */
public class Peer{

    private static String peerID;
    private static String version;
    private static String accessPoint;
    private static MCSocket MC;
    private static MDBSocket MDB;
    private static MDRSocket MDR;
    private static MessageInterpreter messageInterpreter;

    private static ScheduledThreadPoolExecutor exec;
    private static StatusManager stateManager;

    private static String trackerIP;
    private static int trackerPort;

    private volatile static Integer window;
    private volatile static boolean flag = true;

    //SecureSockets

    //Server always running
    private static ReceiverSocket controlReceiver;


    public Peer() {
    }

    /**
     * Simplified constructor of a Peer.Peer only use for testing
     */
    public Peer(String trackerIP ,int port) throws IOException {
        version = "1.0";
        peerID = UUID.randomUUID().toString();
        this.trackerIP = trackerIP;
        this.trackerPort = port;
        //this.initiateSocketThreads();
        File priFile = new File("./Peer/" + peerID + ".private");
        File pubFile = new File("./Peer/" + peerID + ".public");

        if((!priFile.exists() || priFile.isDirectory()) || (!pubFile.exists() || pubFile.isDirectory())){
            if(!this.generateKeyPair()){
                System.out.println("There was a problem generating the keys");
                return;
            }
        }
        this.exec = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(100000);
        initiateServerSockets();
        /*LogsManager statusData = new LogsManager();
        this.stateManager = statusData.LoadData();
        stateManager.updateData();*/

        this.sendRegister();

    }

    /**
     * Complete construtor of a peer, the version cant be "2.0" because it is used to defined the enhancements
     */
    public Peer(String version, String id, String accessPoint,  SimpleEntry<Integer, String> MC, SimpleEntry<Integer, String> MDB,
            SimpleEntry<Integer, String> MDR) throws IOException {
        this.version = version;
        if (this.version.equals("2.0")) {
            this.version = "1.9";
        }
        peerID = id;
        this.accessPoint = accessPoint;
        this.initiateSocketThreads(MC, MDB, MDR);
        LogsManager statusData = new LogsManager();
        this.stateManager = statusData.LoadData();
        stateManager.updateData();

    }

    public static void main(String[] args) throws IOException {
        System.setProperty("java.net.preferIPv4Stack", "true");

        Peer peer;
        if (args.length == 3) {
            peer = new Peer(args[0],Integer.parseInt(args[1]));

            if(args[2].equals("download")){
                peer.download();
            }else{
                peer.seed();
            }

        } else {
            System.err.println("Error retrieving function arguments");
            return;
        }

    }

    public void download() throws IOException{
        byte[] key = readPublicKey();
        InetAddress inet = InetAddress.getLocalHost();
        String address = inet.getHostAddress();
        int port = this.controlReceiver.getServerSocket().getLocalPort();
        MessageTemp message = new GetFileMessage(peerID, "abc");

        SenderSocket channelStarter = new SenderSocket(trackerPort, trackerIP);

        channelStarter.connect(peerID, "tracker",false,null);

        channelStarter.getHandler().sendMessage(message);

       
    }

    public void seed() throws IOException{
        byte[] key = readPublicKey();
        InetAddress inet = InetAddress.getLocalHost();
        String address = inet.getHostAddress();
        int port = this.controlReceiver.getServerSocket().getLocalPort();
        MessageTemp message = new HasFileMessage(peerID, "abc");

        SenderSocket channelStarter = new SenderSocket(trackerPort, trackerIP);

        channelStarter.connect(peerID, "tracker",false,null);

        channelStarter.getHandler().sendMessage(message);
    }

    public void backup(String pathname, int replicationDegree, boolean enhanced) throws RemoteException {

        String versionToUse = new String(version);
        if (enhanced)
            versionToUse = "2.0";

        FileManager chunks = new FileManager(pathname); //create a Peer.FileManager to get file in chunks

        String fileId = chunks.generateFileID(); //get fileId according to sha256 encryption

        if(fileId == null) {
            System.err.println("Cannot access file");
            return;
        }

        String oldFileId;
        if ((oldFileId = this.stateManager.isBackedUp(pathname)) != null) {

            if (oldFileId.equals(fileId)) {
                System.out.println("You have already backedUp this file");
                return;
            }
            System.out.print("You have updated this file. Old ");
            this.delete(pathname, false);
        }
        this.stateManager.addFile(pathname, fileId);
        try {

            List<ChunkData> chunksArray = chunks.splitFile(); //get an array with all the chunks

            for (int i = 0; i < chunksArray.size(); i++) {

                Message messageToSend = new PutChunkMessage(fileId, versionToUse, peerID, chunksArray.get(i),
                        replicationDegree);
                Runnable thread = new MessageCarrier(messageToSend, "MDB", chunksArray.get(i).getChunkNo());
                exec.execute(thread);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void restore(String pathname, boolean enhanced) {
        String fileId;
        String versionToUse = new String(version);
        if (enhanced)
            versionToUse = "2.0";
        int currentChunkNo;
        if ((fileId = stateManager.isBackedUp(pathname)) == null) {
            System.err.println("This file doesn't exist or your not the owner of it");
            return;
        }
        stateManager.resetChunkToRestore();
        Set<String> set = stateManager.getChunkTable().keySet();
        String[] array = set.toArray(new String[set.size()]);
        this.window = 0;
        RestoreChecker checker = new RestoreChecker();
        exec.execute(checker);
        for (int i = 0; i< array.length;i++) {
            if (array[i].contains(fileId)) {

                if(this.flag){
                    
                    currentChunkNo = Integer.parseInt(array[i].substring(array[i].indexOf(".") + 1, array[i].length()));
                    stateManager.addChunkToRestore(array[i]);
                    window++;
                    Message getChunkMessage = new GetChunkMessage(versionToUse, peerID, fileId, currentChunkNo);
                    Runnable thread = new MessageCarrier(getChunkMessage, "MC");
                    exec.execute(thread);
                }else{
                    while(!this.flag){

                    }
                    i--;
                }
                
                }
                
            }

            Runnable check = new checkRestore(pathname,fileId);
            exec.execute(check);
            
        }


    public void state() throws RemoteException {

        System.out.println("PEER FILES:   ");
        ConcurrentHashMap<String, String> peerFiles = stateManager.getFilesTables();
        Set<String> s = peerFiles.keySet();
        Set<String> chunksKeys = stateManager.getChunkTable().keySet();
        for (String pathname : s) {
            String fileId = peerFiles.get(pathname);
            System.out.println("FILE: ");
            System.out.println("   Pathname: " + pathname);
            System.out.println("   File Id: " + fileId);
            System.out.println("   Desired Replication Degree: "
                    + stateManager.getChunkTable().get(fileId + ".0").getDesiredReplicationDegree());
            for (String fileIdKey : chunksKeys) {
                if (fileIdKey.contains(fileId)) {
                    System.out.println("   Chunk.Chunk: " + stateManager.getChunkTable().get(fileIdKey).getChunkNo()
                            + " ReplicationDegree: "
                            + stateManager.getChunkTable().get(fileIdKey).getCurrentReplicationDegree());

                }
            }

        }

        List<String> backedUpFiles = stateManager.getBackedUpFiles();
        System.out.println("CHUNKS STORED: ");
        for (int i = 0; i < backedUpFiles.size(); i++) {
            String string_aux = backedUpFiles.get(i);
            System.out.println("Chunk.Chunk:  " + backedUpFiles.get(i));
            System.out.println("   CurrentReplicationDegree: "
                    + stateManager.getChunkTable().get(string_aux).getCurrentReplicationDegree());
            double sizeKB = stateManager.getChunkTable().get(string_aux).getSize() / 1000;
            System.out.println("   Chunk.Chunk size: " + sizeKB + " KBytes");
        }

        System.out.println("Memory used: " + stateManager.getSizeUsed() / 1000.0 + " KBytes");
        System.out.println("Maximum capacity allowed: " + stateManager.getMaxSizeUse() / 1000.0 + " KBytes");

    }

    public void reclaim(Integer memory) throws RemoteException {
        stateManager.setMaxSizeUse(memory);
        List<String> string_aux = stateManager.getBackedUpFiles();

        while (stateManager.isOutOfMemory() && !string_aux.isEmpty()) {

            String toRemoveFileIdKey = new String(string_aux.get(0));
            byte[] data_removed = removeFile(toRemoveFileIdKey);
            int desiredRep = stateManager.getChunkTable().get(toRemoveFileIdKey).getDesiredReplicationDegree();

            if (desiredRep == 1) {

                int chunkId = Integer.parseInt(toRemoveFileIdKey.substring(65, toRemoveFileIdKey.length()));
                String fileId = toRemoveFileIdKey.substring(0, 64);
                ChunkData chunk = new ChunkData(chunkId);
                chunk.setData(data_removed.length, data_removed);

                Message messageToSend = new PutChunkMessage(fileId, "2.1", peerID, chunk, desiredRep);
                ((PutChunkMessage) messageToSend).setToReclaim();
                Runnable putchunkthread = new MessageCarrier(messageToSend, "MDB", chunkId);
                Peer.getExec().schedule(putchunkthread, 1, TimeUnit.SECONDS);
            }

        }
    }

    /**
     * @return the controlReceiver
     */
    public static ReceiverSocket getControlReceiver() {
        return controlReceiver;
    }

    /**
     * @return the trackerIP
     */
    public static String getTrackerIP() {
        return trackerIP;
    }
    /**
     * @return the trackerPort
     */
    public static int getTrackerPort() {
        return trackerPort;
    }

    public static byte[] removeFile(String toRemoveFileIdKey) {

        stateManager.deleteBackedUpFile(toRemoveFileIdKey);
        FileManager manager = new FileManager();
        String pathname = "Peer " + peerID + "/" + toRemoveFileIdKey;
        byte[] data_removed = manager.deleteFile(pathname);
        stateManager.updateChunkDec(toRemoveFileIdKey);
        stateManager.updateChunkInfoPeerRemove(toRemoveFileIdKey,getPeerID());

        int chunkId = Integer.parseInt(toRemoveFileIdKey.substring(65, toRemoveFileIdKey.length()));
        String fileId = toRemoveFileIdKey.substring(0, 64);
        Message message = new RemoveMessage(fileId, version, peerID, chunkId);
        Runnable thread = new MessageCarrier(message, "MC", chunkId);
        exec.execute(thread);

        return data_removed;
    }

    public void delete(String pathname, boolean enhanced) throws RemoteException {

        String versionToUse = new String(version);
        if (enhanced)
            versionToUse = "2.0";
        String fileId;
        if ((fileId = stateManager.deleteFile(pathname, versionToUse)) == null) {
            System.err.println("You didn't backup up this file so you can't delete it");
            return;

        } else {
            System.out.println("Files will be deleted");
            //stateManager.getFilesTables().remove(pathname);
            Message deleteMessage = new DeleteMessage(fileId, versionToUse, peerID);
            Runnable thread = new MessageCarrier(deleteMessage, "MC");
            Peer.getExec().execute(thread);
        }

    }

    //Iniate the 3 multicasts sockets and the threads that will read the messages in a thread pool
    public void initiateSocketThreads() throws IOException {

        this.exec = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(100000);
        //Thread para o canal principal MC;
        MC = new MCSocket();
        Runnable mcThread = MC;
        this.exec.execute(mcThread);

        //Thread para o canal MDB
        MDB = new MDBSocket();
        Runnable mdbThread = MDB;
        this.exec.execute(mdbThread);

        //Thread para o canal MDR
        MDR = new MDRSocket();
        Runnable mdrThread = MDR;
        this.exec.execute(mdrThread);

        messageInterpreter = new MessageInterpreter();
        Runnable interpreterThread = messageInterpreter;
        this.exec.execute(interpreterThread);

    }

    //Iniate the 3 multicasts sockets and the threads that will read the messages in a thread pool
    public void initiateSocketThreads(SimpleEntry<Integer, String> MC, SimpleEntry<Integer, String> MDB, SimpleEntry<Integer, String> MDR)
            throws IOException {

        this.exec = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(100000);
        //Thread para o canal principal MC;
        this.MC = new MCSocket(MC.getKey(), MC.getValue());
        Runnable mcThread = this.MC;
        this.exec.execute(mcThread);

        //Thread para o canal MDB
        this.MDB = new MDBSocket(MDB.getKey(), MDB.getValue());
        Runnable mdbThread = this.MDB;
        this.exec.execute(mdbThread);

        //Thread para o canal MDR
        this.MDR = new MDRSocket(MDR.getKey(), MDR.getValue());
        Runnable mdrThread = this.MDR;
        this.exec.execute(mdrThread);

        messageInterpreter = new MessageInterpreter();
        Runnable interpreterThread = messageInterpreter;
        this.exec.execute(interpreterThread);
    }


    public void initiateServerSockets() throws IOException{
        
        this.controlReceiver = new ReceiverSocket(0);

        this.controlReceiver.connect(this.peerID);
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
        InetAddress inet = InetAddress.getLocalHost();
        String address = inet.getHostAddress();
        int port = this.controlReceiver.getServerSocket().getLocalPort();
        MessageTemp message = new RegisterMessage(peerID, address, port, key);

        SenderSocket channelStarter = new SenderSocket(trackerPort, trackerIP);

        channelStarter.connect(peerID, "tracker",false,null);

        channelStarter.getHandler().sendMessage(message);

    }

    public static ScheduledExecutorService getExec() {
        return exec;
    }

    public static MCSocket getMC() {
        return MC;
    }

    public static MDBSocket getMDB() {
        return MDB;
    }

    public static MDRSocket getMDR() {
        return MDR;
    }

    public static String getPeerID() {
        return peerID;
    }

    public static StatusManager getStateManager() {
        return stateManager;
    }

    public static MessageInterpreter getMessageInterpreter() {
        return messageInterpreter;
    }

    public static String getVersion() {
        return version;
    }

    /**
     * @return the window
     */
    public static int getWindow() {
        return window;
    }

    /**
     * @param window the window to set
     */
    public static void setWindow(Integer window) {
        Peer.window = window;
    }

    /**
     * @param flag the flag to set
     */
    public static void setFlag(boolean flag) {
        Peer.flag = flag;
    }

    public static void decWindow(){
        Peer.window--;
    }

    public class checkRestore implements Runnable{

        private String pathname;
        private String fileId;
        checkRestore(String pathname,String fileId){
            this.pathname = pathname;
            this.fileId = fileId;
        };

        @Override
        public void run(){
            while(true){
                if(stateManager.isChunkToRestoreEmpty()){
                    FileManager manager = new FileManager(pathname);
                        try {
                            manager.mergeChunks(fileId);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return;
                }
            }
        }
    }



}