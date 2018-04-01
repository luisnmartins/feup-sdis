import javafx.util.Pair;

import java.util.*;
import java.io.*;
import java.rmi.RemoteException;
import java.util.concurrent.*;


/**
 * peer
 */
public class Peer implements remoteInterface{

    private static String peerID;
    private static String version;
    private static String accessPoint;
    private static MCSocket MC;
    private static MDBSocket MDB;
    private static MDRSocket MDR;
    private static MessageInterpreter messageInterpreter;

    private static ScheduledThreadPoolExecutor exec;
    private static StatusManager stateManager;


    public Peer(){}

    public Peer(String id) throws IOException {
        version = "1.0";
        peerID = id ;
        this.initiateSocketThreads();
        LogsManager statusData = new LogsManager();
        this.stateManager = statusData.loadData();
    }

    public Peer(String version, String id, String accessPoint, Pair<Integer,String> MC,Pair<Integer,String> MDB, Pair<Integer,String> MDR) throws IOException {
        this.version = version;
        peerID = id;
        this.accessPoint = accessPoint;
        this.initiateSocketThreads(MC,MDB,MDR);
        this.stateManager = new StatusManager();

    }


    public static void main(String[] args) throws IOException {
        if(args.length != 1 && args.length != 9){

            System.err.println("Error retrieving function arguments");
            return;
        }
        System.setProperty("java.net.preferIPv4Stack", "true");
        verifyArgs(args);


      }

    @Override
    public void backup(String pathname, int replicationDegree) throws RemoteException {


        FileManager chunks = new FileManager(pathname); //create a FileManager to get file in chunks
        
        String fileId = chunks.generateFileID(); //get fileId according to sha256 encryption

        String oldFileId;
        if((oldFileId = this.stateManager.isBackedUp(pathname)) != null){

            if(oldFileId.equals(fileId)) {
                System.out.println("You have already backedUp this file");
                return;
            }
            System.out.print("You have updated this file. Old ");
            this.delete(pathname);
        }
        this.stateManager.addFile(pathname,fileId);
        try {

            List<ChunkData> chunksArray = chunks.splitFile(); //get an array with all the chunks


            for(int i=0; i<chunksArray.size(); i++) {


                Message messageToSend = new PutChunkMessage(fileId, "1.0", peerID, chunksArray.get(i), replicationDegree);
                Runnable thread = new MessageCarrier(messageToSend, "MDB",chunksArray.get(i).getChunkNo());
                Peer.getExec().execute(thread);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void restore(String pathname) {
        String fileId;
        int currentChunkNo;
        if((fileId = stateManager.isBackedUp(pathname)) == null) {
            System.err.println("This file doesn't exist or your not the owner of it");
            return;
        }
        Set<String> set = stateManager.getChunkTable().keySet();
        for(String key: set) {
            if(key.contains(fileId)) {
                currentChunkNo = Integer.parseInt(key.substring(key.indexOf(".")+1,key.length()));
                stateManager.addChunkToRestore(currentChunkNo);

                Message getChunkMessage = new GetChunkMessage("1.0", peerID, fileId, currentChunkNo);
                Runnable thread = new MessageCarrier(getChunkMessage, "MC");
                exec.execute(thread);
            }
        }


    }

    @Override
    public void state() throws RemoteException {

        System.out.println("PEER FILES:   ");
        ConcurrentHashMap<String, String> peerFiles = stateManager.getFilesTables();
        Set<String> s = peerFiles.keySet();
        Set<String> chunksKeys = stateManager.getChunkTable().keySet();
        for(String pathname: s){
            String fileId = peerFiles.get(pathname);
            System.out.println("FILE: ");
            System.out.println("   Pathname: "+pathname);
            System.out.println("   File Id: "+fileId);
            for(String fileIdKey: chunksKeys){
                if(fileIdKey.contains(fileId)){
                    System.out.println("   Chunk: " + stateManager.getChunkTable().get(fileIdKey).getChunkNo() + " ReplicationDegree: " + stateManager.getChunkTable().get(fileIdKey).getDesiredReplicationDegree());


                }
            }

        }

        List<String> backedUpFiles = stateManager.getBackedUpFiles();
        for(int i = 0; i< backedUpFiles.size(); i++){
            String string_aux = backedUpFiles.get(i);
            System.out.println("Chunk:  " + backedUpFiles.get(i));
            System.out.println("   CurrentReplicationDegree: " + stateManager.getChunkTable().get(string_aux).getCurrentReplicationDegree());
            System.out.println("   Chunk size: " + stateManager.getChunkTable().get(string_aux).getSize() + " Bytes");
        }

        for(String ajuda : chunksKeys){
            System.out.println("Chunk: " + ajuda);
            for(String string: stateManager.getChunkTable().get(ajuda).getPeers()){
                System.out.println("Peerid:  " + string);
            }
        }


    }

    @Override
    public void reclaim(Integer memory) throws RemoteException {

    }

    @Override
    public void delete(String pathname) throws RemoteException {

        String fileId;
        if((fileId = stateManager.deleteFile(pathname)) == null) {
            System.err.println("You didn't backup up this file so you can't delete it");
            return;

        } else {
            System.out.println("Files will be deleted");
            //stateManager.getFilesTables().remove(pathname);
            Message deleteMessage = new DeleteMessage(fileId, "1.0", peerID);
            Runnable thread = new MessageCarrier(deleteMessage, "MC");
            Peer.getExec().execute(thread);
        }

    }

    //Inicia as threads para os 3 canais necessarios
    public void initiateSocketThreads() throws IOException {


        this.exec = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(100);
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

    public void initiateSocketThreads(Pair<Integer,String> MC,Pair<Integer,String> MDB,Pair<Integer,String> MDR) throws IOException {

        this.exec = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(100);
        //Thread para o canal principal MC;
        this.MC = new MCSocket(MC.getKey(),MC.getValue());
        Runnable mcThread = this.MC;
        this.exec.execute(mcThread);

        //Thread para o canal MDB
        this.MDB = new MDBSocket(MDB.getKey(),MDB.getValue());
        Runnable mdbThread = this.MDB;
        this.exec.execute(mdbThread);

        //Thread para o canal MDR
        this.MDR = new MDRSocket(MDR.getKey(),MDR.getValue());
        Runnable mdrThread = this.MDR;
        this.exec.execute(mdrThread);

        messageInterpreter = new MessageInterpreter();
        Runnable interpreterThread = messageInterpreter;
        this.exec.execute(interpreterThread);
    }

    public static void verifyArgs(String args[]) throws IOException {
        Peer peer;
        RMIHandler handler = new RMIHandler();
        if(args.length == 1){
             peer = new Peer(args[0]);
            handler.sendToRegistry(peer,peerID);


        }else if (args.length == 9){
            Pair<Integer,String> mc = new Pair<>(Integer.parseInt(args[3]),args[4]);
            Pair<Integer,String> mdb = new Pair<>(Integer.parseInt(args[5]),args[6]);
            Pair<Integer,String> mdr = new Pair<>(Integer.parseInt(args[7]),args[8]);
            peer = new Peer(args[0],args[1],args[2],mc,mdb,mdr);
            handler.sendToRegistry(peer,accessPoint);
        }else{
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
}