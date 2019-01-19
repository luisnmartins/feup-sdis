package Peer;

import Chunk.ChunkData;
import Messages.*;
import RMI.RMIHandler;
import RMI.remoteInterface;
import Sockets.MCSocket;
import Sockets.MDBSocket;
import Sockets.MDRSocket;
import Workers.RestoreChecker;
import java.util.AbstractMap.SimpleEntry;

import java.util.*;
import java.io.*;
import java.rmi.RemoteException;
import java.util.concurrent.*;

/**
 * peer
 */
public class Peer implements remoteInterface {

    private static String peerID;
    private static String version;
    private static String accessPoint;
    private static MCSocket MC;
    private static MDBSocket MDB;
    private static MDRSocket MDR;
    private static MessageInterpreter messageInterpreter;

    private static ScheduledThreadPoolExecutor exec;
    private static StatusManager stateManager;

    private volatile static Integer window;
    private volatile static boolean flag = true;

    public Peer() {
    }

    /**
     * Simplified constructor of a Peer.Peer only use for testing
     */
    public Peer(String id) throws IOException {
        version = "1.0";
        peerID = id;
        this.initiateSocketThreads();
        LogsManager statusData = new LogsManager();
        this.stateManager = statusData.LoadData();
        stateManager.updateData();

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
        if (args.length != 1 && args.length != 9) {

            System.err.println("Error retrieving function arguments");
            return;
        }
        System.setProperty("java.net.preferIPv4Stack", "true");
        verifyArgs(args);

    }

    @Override
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

    @Override
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



    @Override
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

    @Override
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

    @Override
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

    /**
     * Verfies the arguments sent to start the application
     */
    public static void verifyArgs(String args[]) throws IOException {
        Peer peer;
        RMIHandler handler = new RMIHandler();
        if (args.length == 1) {
            peer = new Peer(args[0]);
            handler.sendToRegistry(peer, peerID);

        } else if (args.length == 9) {
            SimpleEntry<Integer, String> mc = new SimpleEntry<>(Integer.parseInt(args[3]), args[4]);
            SimpleEntry<Integer, String> mdb = new SimpleEntry<>(Integer.parseInt(args[5]), args[6]);
            SimpleEntry<Integer, String> mdr = new SimpleEntry<>(Integer.parseInt(args[7]), args[8]);
            peer = new Peer(args[0], args[1], args[2], mc, mdb, mdr);
            handler.sendToRegistry(peer, accessPoint);
        } else {
            System.err.println("Error retrieving function arguments");
            return;
        }

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